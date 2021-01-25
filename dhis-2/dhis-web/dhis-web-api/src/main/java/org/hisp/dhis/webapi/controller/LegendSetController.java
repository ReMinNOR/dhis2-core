package org.hisp.dhis.webapi.controller;



import org.hisp.dhis.legend.LegendSet;
import org.hisp.dhis.schema.descriptors.LegendSetSchemaDescriptor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = LegendSetSchemaDescriptor.API_ENDPOINT )
public class LegendSetController
    extends AbstractCrudController<LegendSet>
{
    @Override
    @PreAuthorize( "hasRole('F_LEGEND_SET_PUBLIC_ADD') or hasRole('F_LEGEND_SET_PRIVATE_ADD') or hasRole('ALL')" )
    public void postJsonObject( HttpServletRequest request, HttpServletResponse response ) throws Exception
    {
        super.postJsonObject( request, response );
    }

    @Override
    @PreAuthorize( "hasRole('F_LEGEND_SET_PUBLIC_ADD') or hasRole('F_LEGEND_SET_PRIVATE_ADD')  or hasRole('ALL')" )
    public void putJsonObject( @PathVariable String uid, HttpServletRequest request, HttpServletResponse response ) throws Exception
    {
        super.putJsonObject( uid, request, response );
    }

    @Override
    @PreAuthorize( "hasRole('F_LEGEND_SET_DELETE') or hasRole('ALL')" )
    public void deleteObject( @PathVariable String uid, HttpServletRequest request, HttpServletResponse response ) throws Exception
    {
        super.deleteObject( uid, request, response );
    }
}
