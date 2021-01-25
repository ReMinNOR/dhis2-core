package org.hisp.dhis.webapi.controller;



import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.hisp.dhis.webapi.DhisWebSpringTest;
import org.junit.Test;
import org.springframework.mock.web.MockHttpSession;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class ApiVersionMethodTest extends DhisWebSpringTest
{
    @Test
    public void testMethodV31V32() throws Exception
    {
        MockHttpSession session = getSession( "ALL" );
        String endpoint = "/method/testV31V32";

        mvc.perform( get( endpoint ).session( session ) )
            .andExpect( status().isNotFound() );

        mvc.perform( get( "/31" + endpoint ).session( session ) )
            .andExpect( status().isNotFound() );

        mvc.perform( get( "/32" + endpoint ).session( session ) )
            .andExpect( status().isNotFound() );

        mvc.perform( get( "/31" + endpoint + "/a" ).session( session ) )
            .andExpect( status().isOk() );

        mvc.perform( post( "/31" + endpoint + "/a" ).session( session ) )
            .andExpect( status().isOk() );

        mvc.perform( put( "/31" + endpoint + "/a" ).session( session ) )
            .andExpect( status().isMethodNotAllowed() );

        mvc.perform( get( "/32" + endpoint + "/b" ).session( session ) )
            .andExpect( status().isOk() );

        mvc.perform( post( "/32" + endpoint + "/b" ).session( session ) )
            .andExpect( status().isMethodNotAllowed() );

        mvc.perform( put( "/32" + endpoint + "/b" ).session( session ) )
            .andExpect( status().isOk() );
    }

    @Test
    public void testMethodAll() throws Exception
    {
        MockHttpSession session = getSession( "ALL" );
        String endpoint = "/method/testAll";

        mvc.perform( get( endpoint ).session( session ) )
            .andExpect( status().isNotFound() );

        mvc.perform( get( "/31" + endpoint ).session( session ) )
            .andExpect( status().isNotFound() );

        mvc.perform( get( "/32" + endpoint ).session( session ) )
            .andExpect( status().isNotFound() );

        mvc.perform( get( "/31" + endpoint + "/a" ).session( session ) )
            .andExpect( status().isOk() );

        mvc.perform( get( "/32" + endpoint + "/b" ).session( session ) )
            .andExpect( status().isOk() );
    }

    @Test
    public void testMethodAllExcludeV32() throws Exception
    {
        MockHttpSession session = getSession( "ALL" );
        String endpoint = "/method/testAllExcludeV32";

        mvc.perform( get( endpoint ).session( session ) )
            .andExpect( status().isNotFound() );

        mvc.perform( get( "/32" + endpoint ).session( session ) )
            .andExpect( status().isNotFound() );

        mvc.perform( get( "/32" + endpoint + "/a" ).session( session ) )
            .andExpect( status().isNotFound() );

        mvc.perform( get( "/32" + endpoint + "/b" ).session( session ) )
            .andExpect( status().isNotFound() );

        mvc.perform( get( "/31" + endpoint + "/a" ).session( session ) )
            .andExpect( status().isOk() );

        mvc.perform( get( "/31" + endpoint + "/b" ).session( session ) )
            .andExpect( status().isOk() );
    }

    @Test
    public void testMethodDefault() throws Exception
    {
        MockHttpSession session = getSession( "ALL" );
        String endpoint = "/method/testDefault";

        mvc.perform( get( endpoint ).session( session ) )
            .andExpect( status().isNotFound() );

        mvc.perform( get( "/31" + endpoint ).session( session ) )
            .andExpect( status().isNotFound() );

        mvc.perform( get( "/32" + endpoint ).session( session ) )
            .andExpect( status().isNotFound() );

        mvc.perform( get( "/31" + endpoint + "/a" ).session( session ) )
            .andExpect( status().isNotFound() );

        mvc.perform( get( "/32" + endpoint + "/b" ).session( session ) )
            .andExpect( status().isNotFound() );

        mvc.perform( get( endpoint + "/a" ).session( session ) )
            .andExpect( status().isOk() );

        mvc.perform( get( endpoint + "/b" ).session( session ) )
            .andExpect( status().isOk() );
    }
}
