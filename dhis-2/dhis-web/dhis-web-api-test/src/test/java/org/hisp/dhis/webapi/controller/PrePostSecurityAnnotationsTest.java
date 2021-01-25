package org.hisp.dhis.webapi.controller;



import org.hisp.dhis.webapi.DhisWebSpringTest;
import org.junit.Test;
import org.springframework.mock.web.MockHttpSession;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class PrePostSecurityAnnotationsTest extends DhisWebSpringTest
{
    @Test
    public void authorityAllCanAccessApps() throws Exception
    {
        MockHttpSession session = getSession( "ALL" );

        mvc.perform( put( "/apps" ).session( session ) )
            .andExpect( status().isNoContent() );
    }

    @Test
    public void authorityNoAuthorityCantAccessApps() throws Exception
    {
        MockHttpSession session = getSession( "NO_AUTHORITY" );

        mvc.perform( put( "/apps" ).session( session ) )
            .andExpect( status().isForbidden() );
    }
}
