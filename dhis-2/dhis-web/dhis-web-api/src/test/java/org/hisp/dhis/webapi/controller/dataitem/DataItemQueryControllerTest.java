package org.hisp.dhis.webapi.controller.dataitem;

/*
 * Copyright (c) 2004-2021, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.junit.MockitoJUnit.rule;
import static org.springframework.http.HttpStatus.FOUND;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.common.BaseDimensionalItemObject;
import org.hisp.dhis.common.IllegalQueryException;
import org.hisp.dhis.dataitem.DataItem;
import org.hisp.dhis.dxf2.common.OrderParams;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.node.types.RootNode;
import org.hisp.dhis.security.acl.AclService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.webapi.service.ContextService;
import org.hisp.dhis.webapi.webdomain.WebOptions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoRule;
import org.springframework.http.ResponseEntity;

public class DataItemQueryControllerTest
{
    @Mock
    private DataItemServiceFacade dataItemServiceFacade;

    @Mock
    private ContextService contextService;

    @Mock
    private ResponseHandler responseHandler;

    @Mock
    private AclService aclService;

    @Rule
    public MockitoRule mockitoRule = rule();

    private DataItemQueryController dataItemQueryController;

    @Before
    public void setUp()
    {
        dataItemQueryController = new DataItemQueryController( dataItemServiceFacade, contextService, responseHandler,
            aclService );
    }

    @Test
    public void testGetWithSuccess()
    {
        // Given
        final Map<String, String> anyUrlParameters = new HashMap<>();
        final OrderParams anyOrderParams = new OrderParams();
        final User anyUser = new User();
        final Set<Class<? extends BaseDimensionalItemObject>> targetEntities = new HashSet<>(
            singletonList( Indicator.class ) );
        final List<DataItem> itemsFound = singletonList( new DataItem() );

        // When
        when( dataItemServiceFacade.extractTargetEntities( anyList() ) ).thenReturn( targetEntities );
        when( aclService.canRead( anyUser, Indicator.class ) ).thenReturn( true );
        when( dataItemServiceFacade.retrieveDataItemEntities(
            anySet(), anyList(), any( WebOptions.class ), any( OrderParams.class ) ) ).thenReturn( itemsFound );

        final ResponseEntity<RootNode> actualResponse = dataItemQueryController.getJson( anyUrlParameters,
            anyOrderParams, anyUser );

        // Then
        assertThat( actualResponse, is( not( nullValue() ) ) );
        assertThat( actualResponse.getStatusCode(), is( FOUND ) );
        verify( responseHandler, times( 1 ) ).addResultsToNode( any( RootNode.class ), anyList(), anyList() );
        verify( responseHandler, times( 1 ) ).addPaginationToNode( any( RootNode.class ), anyList(), any(), any(),
            anyList() );
    }

    @Test
    public void testGetWhenItemsAreNotFound()
    {
        // Given
        final Map<String, String> anyUrlParameters = new HashMap<>();
        final OrderParams anyOrderParams = new OrderParams();
        final User anyUser = new User();
        final Set<Class<? extends BaseDimensionalItemObject>> targetEntities = new HashSet<>(
            singletonList( Indicator.class ) );
        final List<DataItem> itemsFound = emptyList();

        // When
        when( dataItemServiceFacade.extractTargetEntities( anyList() ) ).thenReturn( targetEntities );
        when( aclService.canRead( anyUser, Indicator.class ) ).thenReturn( true );
        when( dataItemServiceFacade.retrieveDataItemEntities(
            anySet(), anyList(), any( WebOptions.class ), any( OrderParams.class ) ) ).thenReturn( itemsFound );

        final ResponseEntity<RootNode> actualResponse = dataItemQueryController.getJson( anyUrlParameters,
            anyOrderParams, anyUser );

        // Then
        assertThat( actualResponse, is( not( nullValue() ) ) );
        assertThat( actualResponse.getStatusCode(), is( NOT_FOUND ) );
        verify( responseHandler, times( 1 ) ).addResultsToNode( any(), anyList(), anyList() );
        verify( responseHandler, times( 1 ) ).addPaginationToNode( any(), anyList(), any(), any(), anyList() );
    }

    @Test
    public void testGetWhenAclIsInvalid()
    {
        // Given
        final Map<String, String> anyUrlParameters = new HashMap<>();
        final OrderParams anyOrderParams = new OrderParams();
        final User anyUser = new User();
        final Set<Class<? extends BaseDimensionalItemObject>> targetEntities = new HashSet<>(
            singletonList( Indicator.class ) );
        final boolean invalidAcl = false;

        // When
        when( dataItemServiceFacade.extractTargetEntities( anyList() ) ).thenReturn( targetEntities );
        when( aclService.canRead( anyUser, Indicator.class ) ).thenReturn( invalidAcl );

        final IllegalQueryException ex = assertThrows(IllegalQueryException.class,
                () -> dataItemQueryController.getJson(anyUrlParameters, anyOrderParams, anyUser));
        assertThat(ex.getMessage(), containsString( "does not have read access for object" ));
    }
}
