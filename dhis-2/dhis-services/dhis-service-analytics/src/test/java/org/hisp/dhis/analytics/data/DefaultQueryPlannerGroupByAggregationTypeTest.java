/*
 * Copyright (c) 2004-2020, University of Oslo
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

package org.hisp.dhis.analytics.data;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hisp.dhis.DhisConvenienceTest.*;
import static org.hisp.dhis.analytics.DataQueryParams.DISPLAY_NAME_DATA_X;
import static org.hisp.dhis.analytics.DataQueryParams.DISPLAY_NAME_ORGUNIT;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.analytics.*;
import org.hisp.dhis.analytics.partition.PartitionManager;
import org.hisp.dhis.category.CategoryCombo;
import org.hisp.dhis.common.BaseDimensionalObject;
import org.hisp.dhis.common.DimensionType;
import org.hisp.dhis.common.DimensionalItemObject;
import org.hisp.dhis.common.ValueType;
import org.hisp.dhis.dataelement.DataElementDomain;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * @author Luciano Fiandesio
 */
public class DefaultQueryPlannerGroupByAggregationTypeTest
{
    private DefaultQueryPlanner subject;

    @Mock
    private QueryValidator queryValidator;

    @Mock
    private PartitionManager partitionManager;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setUp()
    {
        subject = new DefaultQueryPlanner( queryValidator, partitionManager );
    }

    @Test
    public void verifyMultipleDataElementIsAggregatedWithTwoQueryGroupWhenDataTypeIsDifferent()
    {
        List<DimensionalItemObject> periods = new ArrayList<>();
        periods.add( new MonthlyPeriodType().createPeriod( new DateTime( 2014, 4, 1, 0, 0 ).toDate() ) );
        // DataQueryParams with **two** DataElement with different data type as
        // dimension
        DataQueryParams queryParams = DataQueryParams.newBuilder().withDimensions(
            // PERIOD DIMENSION
            Lists.newArrayList( new BaseDimensionalObject( "pe", DimensionType.PERIOD, periods ),
                new BaseDimensionalObject( "dx", DimensionType.DATA_X, DISPLAY_NAME_DATA_X, "display name",
                    Lists.newArrayList( createDataElement( 'A', new CategoryCombo() ),
                        createDataElement( 'B', ValueType.TEXT, AggregationType.COUNT,
                            DataElementDomain.AGGREGATE ) ) ) ) )
            .withFilters( Lists.newArrayList(
                // OU FILTER
                new BaseDimensionalObject( "ou", DimensionType.ORGANISATION_UNIT, null, DISPLAY_NAME_ORGUNIT,
                    ImmutableList.of( new OrganisationUnit( "bbb", "bbb", "OU_2", null, null, "c2" ) ) ) ) )
            .withAggregationType( AnalyticsAggregationType.AVERAGE ).build();

        DataQueryGroups dataQueryGroups = subject.planQuery( queryParams,
            QueryPlannerParams.newBuilder().withTableType( AnalyticsTableType.DATA_VALUE ).build() );

        assertThat( dataQueryGroups.getAllQueries(), hasSize( 2 ) );

        assertThat( dataQueryGroups.getAllQueries(), hasItem(
            both( hasProperty( "aggregationType", hasProperty( "aggregationType", is( AggregationType.AVERAGE ) ) ) )
                .and( hasProperty( "aggregationType", hasProperty( "dataType", is( DataType.NUMERIC ) ) ) ) ) );

        assertThat( dataQueryGroups.getAllQueries(), hasItem(
            both( hasProperty( "aggregationType", hasProperty( "aggregationType", is( AggregationType.AVERAGE ) ) ) )
                .and( hasProperty( "aggregationType", hasProperty( "dataType", is( DataType.TEXT ) ) ) ) ) );
    }

    @Test
    public void verifySingleNonDataElementRetainAggregationTypeButNullDataType()
    {
        //
        // Only single Data Element in filter are retaining the Data Type
        //
        List<DimensionalItemObject> periods = new ArrayList<>();
        periods.add( new MonthlyPeriodType().createPeriod( new DateTime( 2014, 4, 1, 0, 0 ).toDate() ) );
        // DataQueryParams with **one** Indicator
        DataQueryParams queryParams = DataQueryParams.newBuilder().withDimensions(
                // PERIOD DIMENSION
                Lists.newArrayList( new BaseDimensionalObject( "pe", DimensionType.PERIOD, periods ),
                        new BaseDimensionalObject( "dx", DimensionType.DATA_X, DISPLAY_NAME_DATA_X, "display name",
                                Lists.newArrayList( createIndicator('A', createIndicatorType( 'A' ) ) ) ) ) )
                .withFilters( Lists.newArrayList(
                        // OU FILTER
                        new BaseDimensionalObject( "ou", DimensionType.ORGANISATION_UNIT, null, DISPLAY_NAME_ORGUNIT,
                                ImmutableList.of( new OrganisationUnit( "bbb", "bbb", "OU_2", null, null, "c2" ) ) ) ) )
                .withAggregationType( AnalyticsAggregationType.AVERAGE ).build();

        DataQueryGroups dataQueryGroups = subject.planQuery( queryParams,
                QueryPlannerParams.newBuilder().withTableType( AnalyticsTableType.DATA_VALUE ).build() );

        assertThat( dataQueryGroups.getAllQueries(), hasSize( 1 ) );

        assertThat( dataQueryGroups.getAllQueries(), hasItem(
                both( hasProperty( "aggregationType", hasProperty( "aggregationType", is( AggregationType.AVERAGE ) ) ) )
                        .and( hasProperty( "aggregationType", hasProperty( "dataType", is( nullValue() ) ) ) ) ) );
    }

    @Test
    public void verifyASingleDataElementAsFilterRetainAggregationTypeAndAggregationDataType()
    {
        // DataQueryParams with **one** DataElement as filter
        DataQueryParams queryParams = createDataQueryParams(
            new BaseDimensionalObject( "dx", DimensionType.DATA_X, DISPLAY_NAME_DATA_X, "display name",
                Lists.newArrayList( createDataElement( 'A', new CategoryCombo() ) ) ) );

        DataQueryGroups dataQueryGroups = subject.planQuery( queryParams,
            QueryPlannerParams.newBuilder().withTableType( AnalyticsTableType.DATA_VALUE ).build() );

        assertThat( dataQueryGroups.getAllQueries(), hasSize( 1 ) );
        DataQueryParams dataQueryParam = dataQueryGroups.getAllQueries().get( 0 );

        assertTrue( dataQueryParam.getAggregationType().isAggregationType( AggregationType.AVERAGE ) );

        // Expect the datatype = NUMERIC (which will allow the SQL generator to pick-up
        // the proper SQL function)
        assertThat( dataQueryParam.getAggregationType().getDataType(), is( DataType.NUMERIC ) );
        assertThat( dataQueryParam.getPeriods(), hasSize( 1 ) );
        assertThat( dataQueryParam.getFilterDataElements(), hasSize( 1 ) );
        assertThat( dataQueryParam.getFilterOrganisationUnits(), hasSize( 1 ) );
    }

    @Test
    public void verifyMultipleDataElementAsFilterRetainAggregationTypeAndAggregationDataType()
    {
        // DataQueryParams with **two** DataElement as filter
        DataQueryParams queryParams = createDataQueryParams( new BaseDimensionalObject( "dx", DimensionType.DATA_X,
            DISPLAY_NAME_DATA_X, "display name", Lists.newArrayList( createDataElement( 'A', new CategoryCombo() ),
                createDataElement( 'B', new CategoryCombo() ) ) ) );

        DataQueryGroups dataQueryGroups = subject.planQuery( queryParams,
            QueryPlannerParams.newBuilder().withTableType( AnalyticsTableType.DATA_VALUE ).build() );

        assertThat( dataQueryGroups.getAllQueries(), hasSize( 1 ) );
        DataQueryParams dataQueryParam = dataQueryGroups.getAllQueries().get( 0 );

        assertTrue( dataQueryParam.getAggregationType().isAggregationType( AggregationType.AVERAGE ) );
        assertThat( dataQueryParam.getAggregationType().getDataType(), is( nullValue() ) );
        assertThat( dataQueryParam.getPeriods(), hasSize( 1 ) );
        assertThat( dataQueryParam.getFilterDataElements(), hasSize( 2 ) );
        assertThat( dataQueryParam.getFilterOrganisationUnits(), hasSize( 1 ) );
    }

    private DataQueryParams createDataQueryParams( BaseDimensionalObject filterDataElements )
    {
        List<DimensionalItemObject> periods = new ArrayList<>();
        periods.add( new MonthlyPeriodType().createPeriod( new DateTime( 2014, 4, 1, 0, 0 ).toDate() ) );

        return DataQueryParams.newBuilder().withDimensions(
            // PERIOD DIMENSION
            Lists.newArrayList( new BaseDimensionalObject( "pe", DimensionType.PERIOD, periods ) ) )
            .withFilters( Lists.newArrayList(
                // OU FILTER
                new BaseDimensionalObject( "ou", DimensionType.ORGANISATION_UNIT, null, DISPLAY_NAME_ORGUNIT,
                    ImmutableList.of( new OrganisationUnit( "bbb", "bbb", "OU_2", null, null, "c2" ) ) ),
                // DATA ELEMENT AS FILTER
                filterDataElements ) )
            .withAggregationType( AnalyticsAggregationType.AVERAGE ).build();
    }
}