package org.hisp.dhis.webapi.documentation.controller.dataelement;





import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.category.Category;
import org.hisp.dhis.category.CategoryOption;
import org.hisp.dhis.schema.Property;
import org.hisp.dhis.schema.Schema;
import org.hisp.dhis.schema.descriptors.CategorySchemaDescriptor;
import org.hisp.dhis.webapi.documentation.common.ResponseDocumentation;
import org.hisp.dhis.webapi.documentation.common.TestUtils;
import org.hisp.dhis.webapi.documentation.controller.AbstractWebApiTest;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.restdocs.payload.FieldDescriptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertNotNull;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Viet Nguyen <viet@dhis.org>
 */
public class CategoryControllerDocumentation
    extends AbstractWebApiTest<Category>
{
    private static final String ENDPOINT = "categories";

    @Test
    @Override
    public void testGetAll() throws Exception
    {
        manager.save( createCategory( 'A' ) );
        manager.save( createCategory( 'B' ) );
        manager.save( createCategory( 'C' ) );
        manager.save( createCategory( 'D' ) );

        MockHttpSession session = getSession( "ALL" );

        List<FieldDescriptor> fieldDescriptors = new ArrayList<>();
        fieldDescriptors.addAll( ResponseDocumentation.pager() );
        fieldDescriptors.add( fieldWithPath( CategorySchemaDescriptor.PLURAL ).description( "Data element categories" ) );

        mvc.perform( get( "/" + CategorySchemaDescriptor.PLURAL ).session( session ).accept( TestUtils.APPLICATION_JSON_UTF8 ) )
            .andExpect( status().isOk() )
            .andExpect( content().contentTypeCompatibleWith( TestUtils.APPLICATION_JSON_UTF8 ) )
            .andExpect( jsonPath( "$." + CategorySchemaDescriptor.PLURAL ).isArray() )
            .andExpect( jsonPath( "$." + CategorySchemaDescriptor.PLURAL + ".length()" ).value( 5 ) )
            .andDo( documentPrettyPrint( CategorySchemaDescriptor.PLURAL + "/all",
                responseFields( fieldDescriptors.toArray( new FieldDescriptor[fieldDescriptors.size()] ) )
            ) );
    }

    @Test
    @Override
    public void testGetByIdOk() throws Exception
    {

        Category cat = createCategory( 'A' );
        manager.save( cat );
        MockHttpSession session = getSession( "ALL" );

        Schema schema = schemaService.getSchema( Category.class );
        Set<FieldDescriptor> fieldDescriptors = TestUtils.getFieldDescriptors( schema );

        mvc.perform( get( "/" + CategorySchemaDescriptor.PLURAL + "/{id}", cat.getUid() ).session( session ).accept( MediaType.APPLICATION_JSON ) )
            .andExpect( status().isOk() )
            .andExpect( content().contentTypeCompatibleWith( MediaType.APPLICATION_JSON ) )
            .andExpect( jsonPath( "$.name" ).value( "CategoryA" ) )
            .andExpect( jsonPath( "$.shortName" ).value( "CategoryA" ) )
            .andDo( documentPrettyPrint( ENDPOINT + "/id",
                responseFields( fieldDescriptors.toArray( new FieldDescriptor[fieldDescriptors.size()] ) )
            ) );
    }


    @Test
    public void testAddDeleteCollectionItem() throws Exception
    {
        MockHttpSession session = getSession( "ALL" );

        Category category = createCategory( 'A' );
        manager.save( category );

        Schema schema = schemaService.getSchema( Category.class );

        List<Property> properties = schema.getProperties();

        for ( Property property : properties )
        {
            if ( property.isCollection() )
            {
                String collectionName = property.getCollectionName();

                IdentifiableObject item = createTestObject( property.getItemKlass(), 'A', category );

                if ( item == null )
                {
                    continue;
                }
                else
                {
                    manager.save( item );
                }

                mvc.perform( post( "/" + ENDPOINT + "/" + category.getUid() + "/" + collectionName + "/" + item.getUid() )
                    .session( session )
                    .contentType( TestUtils.APPLICATION_JSON_UTF8 ) )
                    .andDo( documentPrettyPrint( ENDPOINT + "/add" + collectionName ) )
                    .andExpect( status().isNoContent() );

                mvc.perform( delete( "/" + ENDPOINT + "/" + category.getUid() + "/" + collectionName + "/" + item.getUid() )
                    .session( session )
                    .contentType( TestUtils.APPLICATION_JSON_UTF8 ) )
                    .andDo( documentPrettyPrint( ENDPOINT + "/delete" + collectionName ) )
                    .andExpect( status().isNoContent() );

            }
        }
    }


    @Test
    @Override
    public void testCreate() throws Exception
    {
        MockHttpSession session = getSession( "F_CATEGORY_PUBLIC_ADD" );

        CategoryOption categoryOptionA = createCategoryOption( 'A' );
        CategoryOption categoryOptionB = createCategoryOption( 'B' );
        CategoryOption categoryOptionC = createCategoryOption( 'C' );

        Category cat = createCategory( 'A', categoryOptionA, categoryOptionB, categoryOptionC );

        Schema schema = schemaService.getSchema( Category.class );

        Set<FieldDescriptor> fieldDescriptors = TestUtils.getFieldDescriptors( schema );

        mvc.perform( post( "/" + ENDPOINT )
            .session( session )
            .contentType( TestUtils.APPLICATION_JSON_UTF8 )
            .content( TestUtils.convertObjectToJsonBytes( cat ) ) )
            .andExpect( status().is( createdStatus ) )
            .andDo( documentPrettyPrint( "categories/create",
                requestFields( fieldDescriptors.toArray( new FieldDescriptor[fieldDescriptors.size()] ) ) )
            );

        cat = manager.getByName( Category.class, "CategoryA" );

        assertNotNull( cat );
    }


    @Test
    @Override
    public void testDeleteByIdOk() throws Exception
    {
        Category cat = createCategory( 'A' );
        manager.save( cat );
        MockHttpSession session = getSession( "ALL" );

        mvc.perform( delete( "/" + ENDPOINT + "/{id}", cat.getUid() ).session( session ).accept( MediaType.APPLICATION_JSON ) )
            .andExpect( status().is( deleteStatus ) )
            .andDo( documentPrettyPrint( "categories/delete" ) );
    }
}
