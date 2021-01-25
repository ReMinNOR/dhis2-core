package org.hisp.dhis.webapi.controller.organisationunit;



import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.hisp.dhis.common.DhisApiVersion;
import org.hisp.dhis.dxf2.metadata.Metadata;
import org.hisp.dhis.dxf2.webmessage.WebMessageException;
import org.hisp.dhis.dxf2.webmessage.WebMessageUtils;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.webapi.mvc.annotation.ApiVersion;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author Lars Helge Overland
 */
@Controller
@RequestMapping( value = "/filledOrganisationUnitLevels" )
@ApiVersion( { DhisApiVersion.DEFAULT, DhisApiVersion.ALL } )
public class FilledOrganisationUnitLevelController
{
    private final ObjectMapper jsonMapper;

    private final OrganisationUnitService organisationUnitService;

    public FilledOrganisationUnitLevelController(
        ObjectMapper jsonMapper,
        OrganisationUnitService organisationUnitService )
    {
        this.jsonMapper = jsonMapper;
        this.organisationUnitService = organisationUnitService;
    }

    @RequestMapping( method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE )
    public @ResponseBody List<OrganisationUnitLevel> getList()
    {
        return organisationUnitService.getFilledOrganisationUnitLevels();
    }

    @RequestMapping( method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE )
    @ResponseStatus( HttpStatus.CREATED )
    public void setList( HttpServletRequest request, HttpServletResponse response )
        throws Exception
    {
        Metadata metadata = jsonMapper.readValue( request.getInputStream(), Metadata.class );

        List<OrganisationUnitLevel> levels = metadata.getOrganisationUnitLevels();

        for ( OrganisationUnitLevel level : levels )
        {
            if ( level.getLevel() <= 0 )
            {
                throw new WebMessageException( WebMessageUtils.conflict( "Level must be greater than zero" ) );
            }

            if ( StringUtils.isBlank( level.getName() ) )
            {
                throw new WebMessageException( WebMessageUtils.conflict( "Name must be specified" ) );
            }

            organisationUnitService.addOrUpdateOrganisationUnitLevel( new OrganisationUnitLevel( level.getLevel(), level.getName(), level.getOfflineLevels() ) );
        }
    }
}
