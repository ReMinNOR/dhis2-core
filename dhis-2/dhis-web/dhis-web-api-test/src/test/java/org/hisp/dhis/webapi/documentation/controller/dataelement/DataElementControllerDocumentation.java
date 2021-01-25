package org.hisp.dhis.webapi.documentation.controller.dataelement;



import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.schema.Property;
import org.hisp.dhis.schema.Schema;
import org.hisp.dhis.webapi.documentation.common.ResponseDocumentation;
import org.hisp.dhis.webapi.documentation.common.TestUtils;
import org.hisp.dhis.webapi.documentation.controller.AbstractWebApiTest;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.restdocs.payload.FieldDescriptor;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNull;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class DataElementControllerDocumentation
    extends AbstractWebApiTest<DataElement>
{


    @Test
    public void testGetById404() throws Exception
    {
        MockHttpSession session = getSession( "ALL" );

        mvc.perform( get( "/dataElements/{id}", "deabcdefghA" ).session( session ).accept( MediaType.APPLICATION_JSON ) )
            .andExpect( status().isNotFound() );
    }


    @Test
    public void testCreateValidation() throws Exception
    {
        MockHttpSession session = getSession( "F_DATAELEMENT_PUBLIC_ADD" );

        DataElement de = createDataElement( 'A' );
        de.setName( null );

        mvc.perform( post( "/dataElements" )
            .session( session )
            .contentType( TestUtils.APPLICATION_JSON_UTF8 )
            .content( TestUtils.convertObjectToJsonBytes( de ) ) )
        ;

        de = manager.getByName( DataElement.class, "DataElementA" );

        assertNull( de );
    }

    @Test
    public void testFilterLike() throws Exception
    {
        MockHttpSession session = getSession( "F_DATAELEMENT_PUBLIC_ADD" );
        DataElement de = createDataElement( 'A' );
        manager.save( de );

        List<FieldDescriptor> fieldDescriptors = new ArrayList<>(ResponseDocumentation.pager());
        fieldDescriptors.add( fieldWithPath( "dataElements" ).description( "Data elements" ) );

        mvc.perform( get( "/dataElements?filter=name:like:DataElementA" )
            .session( session )
            .contentType( TestUtils.APPLICATION_JSON_UTF8 ) )
            .andExpect( jsonPath( "$.pager.total", org.hamcrest.Matchers.greaterThan( 0 ) ) )
            .andDo( documentPrettyPrint( "data-elements/filter",
                responseFields( fieldDescriptors.toArray( new FieldDescriptor[fieldDescriptors.size()] ) ) ) );
    }

    @Test
    public void testFilteriLikeOk() throws Exception
    {
        MockHttpSession session = getSession( "F_DATAELEMENT_PUBLIC_ADD" );
        DataElement de = createDataElement( 'A' );
        manager.save( de );

        mvc.perform( get( "/dataElements?filter=name:ilike:DataElementA" )
            .session( session )
            .contentType( TestUtils.APPLICATION_JSON_UTF8 ) )
            .andExpect( jsonPath( "$.pager.total", org.hamcrest.Matchers.greaterThan( 0 ) ) );
    }

    @Test
    public void testFilterEqualOk() throws Exception
    {
        MockHttpSession session = getSession( "F_DATAELEMENT_PUBLIC_ADD" );
        DataElement de = createDataElement( 'A' );
        manager.save( de );

        mvc.perform( get( "/dataElements?filter=name:eq:DataElementA" )
            .session( session )
            .contentType( TestUtils.APPLICATION_JSON_UTF8 ) )
            .andExpect( jsonPath( "$.pager.total", org.hamcrest.Matchers.greaterThan( 0 ) ) );
    }

    @Test
    public void testFieldsFilterOk() throws Exception
    {
        MockHttpSession session = getSession( "F_DATAELEMENT_PUBLIC_ADD" );
        DataElement de = createDataElement( 'A' );
        manager.save( de );

        List<FieldDescriptor> fieldDescriptors = new ArrayList<>(ResponseDocumentation.pager());
        fieldDescriptors.add( fieldWithPath( "dataElements" ).description( "Data elements" ) );

        mvc.perform( get( "/dataElements?filter=name:eq:DataElementA&fields=id,name,valueType" )
            .session( session )
            .contentType( TestUtils.APPLICATION_JSON_UTF8 ) )
            .andExpect( jsonPath( "$.dataElements[*].id" ).exists() )
            .andExpect( jsonPath( "$.dataElements[*].name" ).exists() )
            .andExpect( jsonPath( "$.dataElements[*].valueType" ).exists() )
            .andExpect( jsonPath( "$.dataElements[*].categoryCombo" ).doesNotExist() )
            .andDo( documentPrettyPrint( "data-elements/fields",
                responseFields( fieldDescriptors.toArray( new FieldDescriptor[fieldDescriptors.size()] ) ) ) );
    }

    @Test
    public void testAddDeleteCollectionItem() throws Exception
    {
        MockHttpSession session = getSession( "ALL" );

        DataElement de = createDataElement( 'A' );
        manager.save( de );

        Schema schema = schemaService.getSchema( DataElement.class );

        List<Property> properties = schema.getProperties();

        for ( Property property : properties )
        {
            if ( property.isCollection() )
            {
                String collectionName = property.getCollectionName();

                IdentifiableObject item = createTestObject( property.getItemKlass(), 'A' );

                if ( item == null )
                {
                    continue;
                }
                else
                {
                    manager.save( item );
                }

                mvc.perform( post( "/dataElements/" + de.getUid() + "/" + collectionName + "/" + item.getUid() )
                    .session( session )
                    .contentType( TestUtils.APPLICATION_JSON_UTF8 ) )
                    .andDo( documentPrettyPrint( "data-elements/add" + collectionName ) )
                    .andExpect( status().isNoContent() );

                mvc.perform( delete( "/dataElements/" + de.getUid() + "/" + collectionName + "/" + item.getUid() )
                    .session( session )
                    .contentType( TestUtils.APPLICATION_JSON_UTF8 ) )
                    .andDo( documentPrettyPrint( "data-elements/delete" + collectionName ) )
                    .andExpect( status().isNoContent() );

            }
        }
    }
}
