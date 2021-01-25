package org.hisp.dhis.webapi.controller.mapping;



import static org.hamcrest.Matchers.hasSize;
import static org.hisp.dhis.common.DimensionalObjectUtils.getList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;

import org.geotools.geojson.geom.GeometryJSON;
import org.hisp.dhis.analytics.DataQueryParams;
import org.hisp.dhis.analytics.DataQueryService;
import org.hisp.dhis.common.DataQueryRequest;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.random.BeanRandomizer;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.webapi.utils.ContextUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * @author Luciano Fiandesio
 */
public class GeoFeatureControllerTest
{
    private MockMvc mockMvc;

    @Mock
    private DataQueryService dataQueryService;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private CurrentUserService currentUserService;

    private BeanRandomizer beanRandomizer = new BeanRandomizer();

    private final static String POINT = "{" +
        "\"type\": \"Point\"," +
        "\"coordinates\": [" +
        "51.17431640625," +
        "15.537052823106482" +
        "]" +
        "}";

    @InjectMocks
    private GeoFeatureController geoFeatureController;

    private final static String ENDPOINT = "/geoFeatures";

    @Before
    public void setUp()
    {
        mockMvc = MockMvcBuilders.standaloneSetup( geoFeatureController ).build();
    }

    @Test
    public void verifyGeoFeaturesReturnsOuData()
        throws Exception
    {
        OrganisationUnit ouA = createOrgUnitWithCoordinates();
        OrganisationUnit ouB = createOrgUnitWithCoordinates();
        OrganisationUnit ouC = createOrgUnitWithCoordinates();
        // This ou should be filtered out since it has no Coordinates
        OrganisationUnit ouD = createOrgUnitWithoutCoordinates();

        User user = beanRandomizer.randomObject(User.class);
        DataQueryParams params = DataQueryParams.newBuilder().withOrganisationUnits( getList( ouA, ouB, ouC, ouD ) )
            .build();

        when( dataQueryService.getFromRequest( any( DataQueryRequest.class ) ) ).thenReturn( params );
        when( currentUserService.getCurrentUser() ).thenReturn( user );

        mockMvc.perform( get( ENDPOINT ).accept(ContextUtils.CONTENT_TYPE_JSON)
            .param( "ou", "ou:LEVEL-2;LEVEL-3" ) )
            .andExpect( status().isOk() )
            .andExpect( content().contentType(ContextUtils.CONTENT_TYPE_JSON) )
            .andExpect( jsonPath( "$", hasSize( 3 ) ) );
    }

    private OrganisationUnit createOrgUnitWithoutCoordinates()
    {
        return beanRandomizer.randomObject( OrganisationUnit.class, "parent", "geometry" );
    }

    private OrganisationUnit createOrgUnitWithCoordinates()
        throws IOException
    {
        OrganisationUnit ou = createOrgUnitWithoutCoordinates();
        ou.setGeometry( new GeometryJSON().read( POINT ) );
        return ou;
    }
}
