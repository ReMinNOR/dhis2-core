package org.hisp.dhis.webapi.controller;



import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.hisp.dhis.category.CategoryCombo;
import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dbms.DbmsManager;
import org.hisp.dhis.translation.Translation;
import org.hisp.dhis.translation.TranslationProperty;
import org.hisp.dhis.webapi.DhisWebSpringTest;
import org.hisp.dhis.webapi.documentation.common.TestUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Viet Nguyen <viet@dhis2.org>
 */
@Slf4j
public class TranslationWebApiTest extends DhisWebSpringTest
{
    @Autowired
    protected DbmsManager dbmsManager;

    @Autowired
    protected TransactionTemplate transactionTemplate;

    @Autowired
    private IdentifiableObjectManager identifiableObjectManager;

    @Test
    @Ignore
    public void testOK()
        throws Exception
    {
        final Locale locale = Locale.FRENCH;
        final CategoryCombo categoryCombo = createCategoryCombo( 'C' );
        final DataElement dataElementA = createDataElement( 'A', categoryCombo );
        final String valueToCheck = "frenchTranslated";
        final MockHttpSession session = getSession( "ALL" );

        transactionTemplate.execute( status -> {

            identifiableObjectManager.save( categoryCombo );
            identifiableObjectManager.save( dataElementA );

            dataElementA.getTranslations().add( new Translation( locale.getLanguage(), TranslationProperty.NAME, valueToCheck ) );

            try
            {
                mvc.perform( put( "/dataElements/" + dataElementA.getUid() + "/translations" )
                    .session( session )
                    .contentType( TestUtils.APPLICATION_JSON_UTF8 )
                    .content( TestUtils.convertObjectToJsonBytes( dataElementA ) ) )
                    .andExpect( status().is( HttpStatus.SC_NO_CONTENT ) );
            }
            catch ( Exception e )
            {
                log.error( "Failed:" + e.getMessage(), e );
            }

            dbmsManager.clearSession();
            return null;
        } );

        MvcResult result = mvc.perform( get( "/dataElements/" + dataElementA.getUid() + "?locale=" + locale.getLanguage() ).session( session )
            .contentType( TestUtils.APPLICATION_JSON_UTF8 ) ).andReturn();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree( result.getResponse().getContentAsString() );

        assertEquals( valueToCheck, node.get( "displayName" ).asText() );
    }
}
