package org.hisp.dhis.webapi.controller;



import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

import org.hisp.dhis.category.Category;
import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.dxf2.webmessage.WebMessageException;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.security.acl.AclService;
import org.hisp.dhis.user.CurrentUserService;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;

/**
 * Unit tests for {@link SharingController}.
 *
 * @author Volker Schmidt
 */
public class SharingControllerTest
{
    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private IdentifiableObjectManager manager;

    @Mock
    private AclService aclService;

    private MockHttpServletRequest request = new MockHttpServletRequest();

    private MockHttpServletResponse response = new MockHttpServletResponse();

    @InjectMocks
    private SharingController sharingController;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test( expected = AccessDeniedException.class )
    public void notSystemDefaultMetadataNoAccess() throws Exception
    {
        final OrganisationUnit organisationUnit = new OrganisationUnit();

        doReturn( OrganisationUnit.class ).when( aclService ).classForType( eq( "organisationUnit" ) );
        when( aclService.isClassShareable( eq( OrganisationUnit.class ) ) ).thenReturn( true );
        doReturn( organisationUnit ).when( manager ).get( eq( OrganisationUnit.class ), eq( "kkSjhdhks" ) );

        sharingController.setSharing( "organisationUnit", "kkSjhdhks", response, request );
    }

    @Test( expected = AccessDeniedException.class )
    public void systemDefaultMetadataNoAccess() throws Exception
    {
        final Category category = new Category();
        category.setName( Category.DEFAULT_NAME + "x" );

        doReturn( Category.class ).when( aclService ).classForType( eq( "category" ) );
        when( aclService.isClassShareable( eq( Category.class ) ) ).thenReturn( true );
        when( manager.get( eq( Category.class ), eq( "kkSjhdhks" ) ) ).thenReturn( category );

        sharingController.setSharing( "category", "kkSjhdhks", response, request );
    }

    @Test( expected = WebMessageException.class )
    public void systemDefaultMetadata() throws Exception
    {
        final Category category = new Category();
        category.setName( Category.DEFAULT_NAME );

        doReturn( Category.class ).when( aclService ).classForType( eq( "category" ) );
        when( aclService.isClassShareable( eq( Category.class ) ) ).thenReturn( true );
        when( manager.get( eq( Category.class ), eq( "kkSjhdhks" ) ) ).thenReturn( category );

        try
        {
            sharingController.setSharing( "category", "kkSjhdhks", response, request );
        }
        catch ( WebMessageException e )
        {
            assertThat( e.getWebMessage().getMessage(), containsString( "Sharing settings of system default metadata object" ) );
            throw e;
        }
    }
}
