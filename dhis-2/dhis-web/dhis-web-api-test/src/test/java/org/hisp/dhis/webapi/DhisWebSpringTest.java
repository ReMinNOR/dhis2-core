package org.hisp.dhis.webapi;



import org.hisp.dhis.DhisConvenienceTest;
import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.render.RenderService;
import org.hisp.dhis.schema.SchemaService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserService;
import org.hisp.dhis.utils.TestUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@RunWith( SpringRunner.class )
@WebAppConfiguration
@ContextConfiguration( classes = { MvcTestConfig.class, WebTestConfiguration.class } )
@ActiveProfiles( "test-h2" )
@Transactional
public abstract class DhisWebSpringTest extends DhisConvenienceTest
{
    @Autowired
    protected WebApplicationContext webApplicationContext;

    @Autowired
    protected IdentifiableObjectManager manager;

    @Autowired
    protected RenderService renderService;

    @Autowired
    protected UserService _userService;

    protected MockMvc mvc;

    @Autowired
    protected SchemaService schemaService;

    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation( "target/generated-snippets" );

    @Before
    public void setup()
        throws Exception
    {
        userService = _userService;
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding( "UTF-8" );
        characterEncodingFilter.setForceEncoding( true );
        mvc = MockMvcBuilders.webAppContextSetup( webApplicationContext )
            .apply( documentationConfiguration( this.restDocumentation ) )
            .build();

        TestUtils.executeStartupRoutines( webApplicationContext );

        setUpTest();
    }

    protected void setUpTest()
        throws Exception
    {
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    public MockHttpSession getSession( String... authorities )
    {
        SecurityContextHolder.getContext().setAuthentication( getPrincipal( authorities ) );
        MockHttpSession session = new MockHttpSession();

        session.setAttribute( HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
            SecurityContextHolder.getContext() );

        return session;
    }

    protected UsernamePasswordAuthenticationToken getPrincipal( String... authorities )
    {
        User user = createAdminUser( authorities );
        List<GrantedAuthority> grantedAuthorities = user.getUserCredentials().getAllAuthorities()
            .stream().map( SimpleGrantedAuthority::new ).collect( Collectors.toList() );

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
            user.getUserCredentials().getUsername(), user.getUserCredentials().getPassword(), grantedAuthorities );

        return new UsernamePasswordAuthenticationToken(
            userDetails,
            userDetails.getPassword(),
            userDetails.getAuthorities()
        );
    }

    public RestDocumentationResultHandler documentPrettyPrint( String useCase, Snippet... snippets )
    {
        return document( useCase, preprocessRequest( prettyPrint() ), preprocessResponse( prettyPrint() ), snippets );
    }

    public SchemaService getSchemaService()
    {
        return schemaService;
    }

    public MockMvc getMvc()
    {
        return mvc;
    }

    public IdentifiableObjectManager getManager()
    {
        return manager;
    }
}
