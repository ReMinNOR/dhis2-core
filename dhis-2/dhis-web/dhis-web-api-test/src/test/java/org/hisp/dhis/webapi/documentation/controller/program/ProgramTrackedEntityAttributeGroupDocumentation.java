package org.hisp.dhis.webapi.documentation.controller.program;



import org.hisp.dhis.program.Program;

import org.hisp.dhis.program.ProgramTrackedEntityAttribute;
import org.hisp.dhis.program.ProgramTrackedEntityAttributeGroup;
import org.hisp.dhis.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.webapi.documentation.common.TestUtils;
import org.hisp.dhis.webapi.documentation.controller.AbstractWebApiTest;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Viet Nguyen <viet@dhis2.org>
 */


public class ProgramTrackedEntityAttributeGroupDocumentation
    extends AbstractWebApiTest<ProgramTrackedEntityAttributeGroup>
{
    @Test
    public void testAddAndRemoveMember() throws Exception
    {
        MockHttpSession session = getSession( "ALL" );

        Program pr = createProgram( 'A' );
        manager.save( pr );
        TrackedEntityAttribute tea = createTrackedEntityAttribute( 'A' );
        manager.save( tea );
        ProgramTrackedEntityAttribute attrA = createProgramTrackedEntityAttribute( pr, tea );
        manager.save( attrA );

        ProgramTrackedEntityAttributeGroup group = createProgramTrackedEntityAttributeGroup( 'A' );
        manager.save( group );

        attrA.addGroup( group );

        mvc.perform( post( schema.getRelativeApiEndpoint() + "/" + group.getUid() + "/attributes/" + attrA.getUid() )
            .session( session )
            .contentType( TestUtils.APPLICATION_JSON_UTF8 ) )
            .andExpect( status().isNoContent() );

        mvc.perform( get( schema.getRelativeApiEndpoint() + "/{id}", group.getUid() )
            .session( session ).accept( MediaType.APPLICATION_JSON ) )
            .andExpect( status().isOk() )
            .andExpect( content().contentTypeCompatibleWith( MediaType.APPLICATION_JSON ) )
            .andExpect( jsonPath( "$.attributes.length()" ).value( 1 ) );


         mvc.perform( delete( schema.getRelativeApiEndpoint() + "/" + group.getUid() + "/attributes/" + attrA.getUid() )
            .session( session )
            .contentType( TestUtils.APPLICATION_JSON_UTF8 ) )
            .andExpect( status().isNoContent() );


         mvc.perform( get( schema.getRelativeApiEndpoint() + "/{id}", group.getUid() )
            .session( session )
            .accept( MediaType.APPLICATION_JSON ) )
            .andExpect( status().isOk() )
            .andExpect( content().contentTypeCompatibleWith( MediaType.APPLICATION_JSON ) )
            .andExpect( jsonPath( "$.attributes.length()" ).value( 0 ) );


    }
}
