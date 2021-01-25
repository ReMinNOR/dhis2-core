package org.hisp.dhis.webapi.controller.organisationunit;



import org.hisp.dhis.attribute.Attribute;
import org.hisp.dhis.attribute.AttributeValue;
import org.hisp.dhis.common.ValueType;
import org.hisp.dhis.commons.filter.FilterUtils;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.render.RenderService;
import org.hisp.dhis.system.filter.OrganisationUnitPolygonCoveringCoordinateFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author James Chang <jamesbchang@gmail.com>
 */
@Controller
@RequestMapping( value = OrganisationUnitLocationController.RESOURCE_PATH )
public class OrganisationUnitLocationController
{
    public static final String RESOURCE_PATH = "/organisationUnitLocations";

    private static final String ORGUNIGROUP_SYMBOL = "orgUnitGroupSymbol";

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private RenderService renderService;

    /**
     * Get Organisation Units within a distance from a location
     */
    @RequestMapping( value = "/withinRange", method = RequestMethod.GET, produces = { "*/*", "application/json" } )
    public void getEntitiesWithinRange(
        @RequestParam Double longitude,
        @RequestParam Double latitude,
        @RequestParam Double distance,
        @RequestParam( required = false ) String orgUnitGroupSetId, HttpServletResponse response ) throws Exception
    {
        List<OrganisationUnit> entityList = new ArrayList<>(
            organisationUnitService.getOrganisationUnitWithinDistance( longitude, latitude, distance ) );

        for ( OrganisationUnit organisationUnit : entityList )
        {
            Set<AttributeValue> attributeValues = organisationUnit.getAttributeValues();
            attributeValues.clear();

            if ( orgUnitGroupSetId != null )
            {
                for ( OrganisationUnitGroup organisationUnitGroup : organisationUnit.getGroups() )
                {
                    for ( OrganisationUnitGroupSet orgunitGroupSet : organisationUnitGroup.getGroupSets() )
                    {
                        if ( orgunitGroupSet.getUid().compareTo( orgUnitGroupSetId ) == 0 )
                        {
                            AttributeValue attributeValue = new AttributeValue();
                            // attributeValue.setAttribute( new Attribute( ORGUNIGROUP_SYMBOL, ORGUNIGROUP_SYMBOL ) );
                            attributeValue.setAttribute( new Attribute( ORGUNIGROUP_SYMBOL, ValueType.TEXT ) );
                            attributeValue.setValue( organisationUnitGroup.getSymbol() );
                            attributeValues.add( attributeValue );
                        }
                    }
                }
            }

            organisationUnit.setAttributeValues( attributeValues );

            // Clear out all data not needed for this task

            organisationUnit.removeAllDataSets();
            organisationUnit.removeAllUsers();
            organisationUnit.removeAllOrganisationUnitGroups();
        }

        renderService.toJson( response.getOutputStream(), entityList );
    }

    /**
     * Get lowest level Org Units that includes the location in their polygon shape.
     */
    @RequestMapping( value = "/orgUnitByLocation", method = RequestMethod.GET, produces = { "*/*", "application/json" } )
    public void getParentByLocation(
        @RequestParam Double longitude,
        @RequestParam Double latitude,
        @RequestParam( required = false ) String topOrgUnit,
        @RequestParam( required = false ) Integer targetLevel, HttpServletResponse response ) throws Exception
    {
        List<OrganisationUnit> entityList = new ArrayList<>(
            organisationUnitService.getOrganisationUnitByCoordinate( longitude, latitude, topOrgUnit, targetLevel ) );

        // Remove unrelated details and output in JSON format

        for ( OrganisationUnit organisationUnit : entityList )
        {
            Set<AttributeValue> attributeValues = organisationUnit.getAttributeValues();
            attributeValues.clear();
            organisationUnit.removeAllDataSets();
            organisationUnit.removeAllUsers();
            organisationUnit.removeAllOrganisationUnitGroups();
        }

        renderService.toJson( response.getOutputStream(), entityList );
    }

    /**
     * Check if the location lies within the organisation unit boundary
     */
    @RequestMapping( value = "/locationWithinOrgUnitBoundary", method = RequestMethod.GET, produces = { "*/*", "application/json" } )
    public void checkLocationWithinOrgUnit( @RequestParam String orgUnitUid,
        @RequestParam Double longitude, @RequestParam Double latitude, HttpServletResponse response ) throws Exception
    {
        boolean withinOrgUnit = false;

        List<OrganisationUnit> organisationUnits = new ArrayList<>();
        organisationUnits.add( organisationUnitService.getOrganisationUnit( orgUnitUid ) );
        FilterUtils.filter( organisationUnits, new OrganisationUnitPolygonCoveringCoordinateFilter( longitude, latitude ) );

        if ( !organisationUnits.isEmpty() )
        {
            withinOrgUnit = true;
        }

        renderService.toJson( response.getOutputStream(), withinOrgUnit );
    }
}
