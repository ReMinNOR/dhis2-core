package org.hisp.dhis.webapi.controller;



import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.hisp.dhis.webapi.DhisWebSpringTest;
import org.junit.Test;
import org.springframework.mock.web.MockHttpSession;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class ApiVersionTypeTest extends DhisWebSpringTest
{
    @Test
    public void testTypeAnnotationDefault() throws Exception
    {
        MockHttpSession session = getSession( "ALL" );
        String endpoint = "/type/testDefault";

        mvc.perform( get( endpoint ).session( session ) )
            .andExpect( status().isOk() );

        mvc.perform( get( "/31" + endpoint ).session( session ) )
            .andExpect( status().isNotFound() );

        mvc.perform( get( "/32" + endpoint ).session( session ) )
            .andExpect( status().isNotFound() );
    }

    @Test
    public void testTypeAnnotationDefaultV31() throws Exception
    {
        MockHttpSession session = getSession( "ALL" );
        String endpoint = "/type/testDefaultV31";

        mvc.perform( get( endpoint ).session( session ) )
            .andExpect( status().isOk() );

        mvc.perform( get( "/31" + endpoint ).session( session ) )
            .andExpect( status().isOk() );

        mvc.perform( get( "/32" + endpoint ).session( session ) )
            .andExpect( status().isNotFound() );
    }

    @Test
    public void testTypeAnnotationV31V32() throws Exception
    {
        MockHttpSession session = getSession( "ALL" );
        String endpoint = "/type/testV31V32";

        mvc.perform( get( endpoint ).session( session ) )
            .andExpect( status().isNotFound() );

        mvc.perform( get( "/31" + endpoint ).session( session ) )
            .andExpect( status().isOk() );

        mvc.perform( get( "/32" + endpoint ).session( session ) )
            .andExpect( status().isOk() );
    }

    @Test
    public void testTypeAnnotationAll() throws Exception
    {
        MockHttpSession session = getSession( "ALL" );
        String endpoint = "/type/testAll";

        mvc.perform( get( endpoint ).session( session ) )
            .andExpect( status().isNotFound() );

        mvc.perform( get( "/31" + endpoint ).session( session ) )
            .andExpect( status().isOk() );

        mvc.perform( get( "/32" + endpoint ).session( session ) )
            .andExpect( status().isOk() );
    }

    @Test
    public void testTypeAnnotationAllExcludeV32() throws Exception
    {
        MockHttpSession session = getSession( "ALL" );
        String endpoint = "/type/testAllExcludeV32";

        mvc.perform( get( endpoint ).session( session ) )
            .andExpect( status().isNotFound() );

        mvc.perform( get( "/31" + endpoint ).session( session ) )
            .andExpect( status().isOk() );

        mvc.perform( get( "/32" + endpoint ).session( session ) )
            .andExpect( status().isNotFound() );
    }

    @Test
    public void testTypeAnnotationDefaultAll() throws Exception
    {
        MockHttpSession session = getSession( "ALL" );
        String endpoint = "/type/testDefaultAll";

        mvc.perform( get( endpoint ).session( session ) )
            .andExpect( status().isOk() );

        mvc.perform( get( "/31" + endpoint ).session( session ) )
            .andExpect( status().isOk() );

        mvc.perform( get( "/32" + endpoint ).session( session ) )
            .andExpect( status().isOk() );
    }
}
