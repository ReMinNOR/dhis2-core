package org.hisp.dhis.webapi.controller;



import org.hisp.dhis.common.DhisApiVersion;
import org.hisp.dhis.dataanalysis.MinMaxDataAnalysisService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.dxf2.webmessage.WebMessageException;
import org.hisp.dhis.dxf2.webmessage.WebMessageUtils;
import org.hisp.dhis.minmax.MinMaxDataElementService;
import org.hisp.dhis.minmax.MinMaxValueParams;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.setting.SettingKey;
import org.hisp.dhis.setting.SystemSettingManager;
import org.hisp.dhis.webapi.mvc.annotation.ApiVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * min max value endpoint to to generate and remove min max values
 *
 * @author Joao Antunes
 */
@Controller
@RequestMapping( value = MinMaxValueController.RESOURCE_PATH )
@ApiVersion( { DhisApiVersion.DEFAULT, DhisApiVersion.ALL } )
public class MinMaxValueController
{
    public static final String RESOURCE_PATH = "/minMaxValues";

    @Autowired
    private MinMaxDataElementService minMaxDataElementService;

    @Autowired
    private MinMaxDataAnalysisService minMaxDataAnalysisService;

    @Autowired
    private DataSetService dataSetService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private SystemSettingManager systemSettingManager;

    @RequestMapping( method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_PERFORM_MAINTENANCE')" )
    @ResponseStatus( HttpStatus.NO_CONTENT )
    public void generateMinMaxValue( @RequestBody MinMaxValueParams minMaxValueParams )
        throws WebMessageException
    {
        List<String> dataSets = minMaxValueParams.getDataSets();
        String organisationUnitId = minMaxValueParams.getOrganisationUnit();

        if ( dataSets == null || dataSets.isEmpty() )
        {
            throw new WebMessageException( WebMessageUtils.conflict( " No datasets defined" ) );
        }

        OrganisationUnit organisationUnit = this.organisationUnitService.getOrganisationUnit( organisationUnitId );
        if ( organisationUnitId == null )
        {
            throw new WebMessageException( WebMessageUtils.conflict( " No valid organisation unit" ) );
        }

        Collection<DataElement> dataElements = new HashSet<>();

        for ( String dataSetId : dataSets )
        {
            DataSet dataSet = this.dataSetService.getDataSet( dataSetId );
            dataElements.addAll( dataSet.getDataElements() );
        }

        Double factor = (Double) this.systemSettingManager.
            getSystemSetting( SettingKey.FACTOR_OF_DEVIATION );

        this.minMaxDataAnalysisService.generateMinMaxValues( organisationUnit, dataElements, factor );

    }

    @RequestMapping( value = "/{ou}", method = RequestMethod.DELETE )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_PERFORM_MAINTENANCE')" )
    @ResponseStatus( HttpStatus.NO_CONTENT )
    public void removeMinMaxValue( @PathVariable( "ou" ) String organisationUnitId,
        @RequestParam( "ds" ) List<String> dataSetIds )
        throws WebMessageException
    {

        if ( dataSetIds == null || dataSetIds.isEmpty() )
        {
            throw new WebMessageException( WebMessageUtils.conflict( " No datasets defined" ) );
        }

        OrganisationUnit organisationUnit = this.organisationUnitService.getOrganisationUnit( organisationUnitId );
        if ( organisationUnitId == null )
        {
            throw new WebMessageException( WebMessageUtils.conflict( " No valid organisation unit" ) );
        }

        Collection<DataElement> dataElements = new HashSet<>();

        for ( String dataSetId : dataSetIds )
        {
            DataSet dataSet = this.dataSetService.getDataSet( dataSetId );
            dataElements.addAll( dataSet.getDataElements() );
        }

        minMaxDataElementService.removeMinMaxDataElements( dataElements, organisationUnit );

    }
}
