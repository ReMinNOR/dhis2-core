package org.hisp.dhis.webapi.controller;



import com.google.common.collect.Lists;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.webapi.webdomain.WebOptions;
import org.springframework.stereotype.Controller;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

/**
 * @author Lars Helge Overland
 */
@Controller
@RequestMapping( value = IdentifiableObjectController.RESOURCE_PATH )
public class IdentifiableObjectController
    extends AbstractCrudController<IdentifiableObject>
{
    public static final String RESOURCE_PATH = "/identifiableObjects";

    @Override
    public List<IdentifiableObject> getEntity( String uid, WebOptions options )
    {
        List<IdentifiableObject> identifiableObjects = Lists.newArrayList();
        Optional<IdentifiableObject> optional = Optional.ofNullable( manager.get( uid ) );
        optional.ifPresent( identifiableObjects::add );

        return identifiableObjects;
    }

    @Override
    public void postXmlObject( HttpServletRequest request, HttpServletResponse response ) throws Exception
    {
        throw new HttpRequestMethodNotSupportedException( "POST" );
    }

    @Override
    public void postJsonObject( HttpServletRequest request, HttpServletResponse response ) throws Exception
    {
        throw new HttpRequestMethodNotSupportedException( "POST" );
    }

    @Override
    public void putJsonObject( @PathVariable( "uid" ) String pvUid, HttpServletRequest request, HttpServletResponse response ) throws Exception
    {
        throw new HttpRequestMethodNotSupportedException( "PUT" );
    }

    @Override
    public void deleteObject( @PathVariable( "uid" ) String pvUid, HttpServletRequest request, HttpServletResponse response ) throws Exception
    {
        throw new HttpRequestMethodNotSupportedException( "PUT" );
    }
}
