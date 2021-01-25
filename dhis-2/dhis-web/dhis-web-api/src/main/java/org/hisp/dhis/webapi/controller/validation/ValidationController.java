package org.hisp.dhis.webapi.controller.validation;



import org.hisp.dhis.common.DhisApiVersion;
import org.hisp.dhis.category.CategoryOptionCombo;
import org.hisp.dhis.category.CategoryService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.dxf2.webmessage.WebMessageException;
import org.hisp.dhis.dxf2.webmessage.WebMessageUtils;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.scheduling.JobConfiguration;
import org.hisp.dhis.scheduling.JobType;
import org.hisp.dhis.scheduling.SchedulingManager;
import org.hisp.dhis.validation.ValidationAnalysisParams;
import org.hisp.dhis.validation.ValidationService;
import org.hisp.dhis.validation.ValidationSummary;
import org.hisp.dhis.webapi.mvc.annotation.ApiVersion;
import org.hisp.dhis.webapi.service.WebMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

/**
 * @author Lars Helge Overland
 */
@Controller
@RequestMapping( value = "/validation" )
@ApiVersion( { DhisApiVersion.DEFAULT, DhisApiVersion.ALL } )
public class ValidationController
{
    @Autowired
    private ValidationService validationService;

    @Autowired
    private DataSetService dataSetService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SchedulingManager schedulingManager;

    @Autowired
    private WebMessageService webMessageService;

    @RequestMapping( value = "/dataSet/{ds}", method = RequestMethod.GET )
    public @ResponseBody ValidationSummary validate( @PathVariable String ds, @RequestParam String pe,
        @RequestParam String ou, @RequestParam( required = false ) String aoc,
        HttpServletResponse response, Model model ) throws WebMessageException
    {
        DataSet dataSet = dataSetService.getDataSet( ds );

        if ( dataSet == null )
        {
            throw new WebMessageException( WebMessageUtils.conflict( "Data set does not exist: " + ds ) );
        }

        Period period = PeriodType.getPeriodFromIsoString( pe );

        if ( period == null )
        {
            throw new WebMessageException( WebMessageUtils.conflict( "Period does not exist: " + pe ) );
        }

        OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( ou );

        if ( orgUnit == null )
        {
            throw new WebMessageException( WebMessageUtils.conflict( "Organisation unit does not exist: " + ou ) );
        }

        CategoryOptionCombo attributeOptionCombo = categoryService.getCategoryOptionCombo( aoc );

        if ( attributeOptionCombo == null )
        {
            attributeOptionCombo = categoryService.getDefaultCategoryOptionCombo();
        }

        ValidationSummary summary = new ValidationSummary();


        ValidationAnalysisParams params = validationService.newParamsBuilder( dataSet, orgUnit, period )
            .withAttributeOptionCombo( attributeOptionCombo )
            .build();

        summary.setValidationRuleViolations( new ArrayList<>( validationService.validationAnalysis( params ) ) );
        summary.setCommentRequiredViolations( validationService.validateRequiredComments( dataSet, period, orgUnit, attributeOptionCombo ) );

        return summary;
    }


    @RequestMapping( value = "/sendNotifications", method = { RequestMethod.PUT, RequestMethod.POST } )
    @PreAuthorize( "hasRole('ALL') or hasRole('M_dhis-web-app-management')" )
    public void runValidationNotificationsTask( HttpServletResponse response, HttpServletRequest request )
    {
        JobConfiguration validationResultNotification = new JobConfiguration("validation result notification from validation controller", JobType.VALIDATION_RESULTS_NOTIFICATION, "", null );

        schedulingManager.executeJob( validationResultNotification );

        webMessageService.send( WebMessageUtils.ok( "Initiated validation result notification" ), response, request );
    }
}
