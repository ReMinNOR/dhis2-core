package org.hisp.dhis.webapi.controller.dataitem.helper;



import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hisp.dhis.webapi.controller.dataitem.DataItemServiceFacade.DATA_TYPE_ENTITY_MAP;
import static org.hisp.dhis.webapi.controller.dataitem.helper.FilteringHelper.containsDimensionTypeFilter;
import static org.hisp.dhis.webapi.controller.dataitem.helper.FilteringHelper.extractEntitiesFromInFilter;
import static org.hisp.dhis.webapi.controller.dataitem.helper.FilteringHelper.extractEntityFromEqualFilter;
import static org.junit.Assert.assertThrows;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.common.BaseDimensionalItemObject;
import org.hisp.dhis.common.IllegalQueryException;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.indicator.Indicator;
import org.junit.Test;

public class FilteringHelperTest
{
    @Test
    public void testExtractEntitiesFromInFilter()
    {
        // Given
        final String anyFilters = "dimensionItemType:in:[INDICATOR,DATA_SET]";
        final Class<? extends BaseDimensionalItemObject>[] expectedClasses = new Class[] { Indicator.class,
            DataSet.class };

        // When
        final Set<Class<? extends BaseDimensionalItemObject>> actualClasses = extractEntitiesFromInFilter( anyFilters );

        // Then
        assertThat( actualClasses, hasSize( 2 ) );
        assertThat( actualClasses, containsInAnyOrder( expectedClasses ) );
    }

    @Test
    public void testExtractEntitiesFromInFilterWhenTypeIsInvalid()
    {
        // Given
        final String filtersWithInvalidType = "dimensionItemType:in:[INVALID_TYPE,DATA_SET]";

        // Then
        assertThrows(
            "Unable to parse element `" + "INVALID_TYPE` on filter `dimensionItemType`. The values available are: "
                + Arrays.toString( DATA_TYPE_ENTITY_MAP.keySet().toArray() ),
            IllegalQueryException.class, () -> extractEntitiesFromInFilter( filtersWithInvalidType ) );
    }

    @Test
    public void testExtractEntitiesFromInFilterWhenFilterContainsOnlyEmpties()
    {
        // Given
        final String filtersWithInvalidType = "dimensionItemType:in:[,]";

        // When
        assertThrows(
            "Unable to parse filter `" + filtersWithInvalidType + "`",
            IllegalQueryException.class, () -> extractEntitiesFromInFilter( filtersWithInvalidType ) );

    }

    @Test
    public void testExtractEntitiesFromInFilterWhenFilterIsNotFullyDefined()
    {
        // Given
        final String filtersNotFullyDefined = "dimensionItemType:in:[,DATA_SET]";
        final Class<? extends BaseDimensionalItemObject>[] expectedClasses = new Class[] { DataSet.class };

        // When
        final Set<Class<? extends BaseDimensionalItemObject>> actualClasses = extractEntitiesFromInFilter(
            filtersNotFullyDefined );

        // Then
        assertThat( actualClasses, hasSize( 1 ) );
        assertThat( actualClasses, containsInAnyOrder( expectedClasses ) );
    }

    @Test
    public void testExtractEntityFromEqualFilter()
    {
        // Given
        final String anyFilter = "dimensionItemType:eq:DATA_SET";
        final Class<? extends BaseDimensionalItemObject> expectedClass = DataSet.class;

        // When
        final Class<? extends BaseDimensionalItemObject> actualClass = extractEntityFromEqualFilter( anyFilter );

        // Then
        assertThat( actualClass, is( notNullValue() ) );
        assertThat( actualClass, is( equalTo( expectedClass ) ) );
    }

    @Test
    public void testExtractEntityFromEqualFilterWhenTypeIsInvalid()
    {
        // Given
        final String filtersWithInvalidType = "dimensionItemType:eq:INVALID_TYPE";

        // When
        assertThrows(
            "Unable to parse element `" + "INVALID_TYPE` on filter `dimensionItemType`. The values available are: "
                + Arrays.toString( DATA_TYPE_ENTITY_MAP.keySet().toArray() ),
            IllegalQueryException.class, () -> extractEntityFromEqualFilter( filtersWithInvalidType ) );

    }

    @Test
    public void testExtractEntityFromEqualFilterWhenFilterIsInvalid()
    {
        // Given
        final String invalidFilter = "dimensionItemType:eq:";

        // When
        assertThrows(
            "Unable to parse filter `" + invalidFilter + "`",
            IllegalQueryException.class, () -> extractEntityFromEqualFilter( invalidFilter ) );
    }

    @Test
    public void testContainsDimensionTypeFilterUsingEqualsQuery()
    {
        // Given
        final List<String> anyFilters = singletonList( "dimensionItemType:eq:DATA_SET" );
        final boolean expectedTrueResult = true;

        // When
        final boolean actualResult = containsDimensionTypeFilter( anyFilters );

        // Then
        assertThat( actualResult, is( expectedTrueResult ) );
    }

    @Test
    public void testContainsDimensionTypeFilterUsingInQuery()
    {
        // Given
        final List<String> anyFilters = singletonList( "dimensionItemType:in:[DATA_SET,INDICATOR]" );
        final boolean expectedTrueResult = true;

        // When
        final boolean actualResult = containsDimensionTypeFilter( anyFilters );

        // Then
        assertThat( actualResult, is( expectedTrueResult ) );
    }

    @Test
    public void testContainsDimensionTypeFilterWhenThereDimensionItemTypeFilterIsNotSet()
    {
        // Given
        final List<String> anyFilters = singletonList( "displayName:ilike:anc" );
        final boolean expectedFalseResult = false;

        // When
        final boolean actualResult = containsDimensionTypeFilter( anyFilters );

        // Then
        assertThat( actualResult, is( expectedFalseResult ) );
    }
}
