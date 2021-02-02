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
package org.hisp.dhis.dataitem.query.shared;

import static java.util.Collections.emptySet;
import static org.apache.commons.collections4.SetUtils.unmodifiableSet;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hisp.dhis.common.ValueType.NUMBER;
import static org.hisp.dhis.dataitem.query.DataItemQuery.NAME;
import static org.hisp.dhis.dataitem.query.DataItemQuery.VALUE_TYPES;
import static org.hisp.dhis.dataitem.query.shared.FilteringStatement.nameFiltering;
import static org.hisp.dhis.dataitem.query.shared.FilteringStatement.skipValueType;
import static org.hisp.dhis.dataitem.query.shared.FilteringStatement.valueTypeFiltering;
import static org.junit.Assert.assertThrows;

import org.junit.Test;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

/**
 * Unit tests for FilteringStatement.
 *
 * @author maikel arabori
 */
public class FilteringStatementTest
{
    @Test
    public void testCommonFilteringUsingOneTableAliasAndIlikeFilterIsSet()
    {
        // Given
        final String aTableAlias = "de";
        final MapSqlParameterSource theParameterSource = new MapSqlParameterSource()
            .addValue( NAME, "abc" );
        final String expectedStatement = " AND (" + aTableAlias + ".\"name\" ILIKE :" + NAME + ")";

        // When
        final String resultStatement = nameFiltering( aTableAlias, theParameterSource );

        // Then
        assertThat( resultStatement, is( expectedStatement ) );
    }

    @Test
    public void testCommonFilteringUsingOneTableAliasAndIlikeFilterIsNotSet()
    {
        // Given
        final String aTableAlias = "de";
        final MapSqlParameterSource noFiltersParameterSource = new MapSqlParameterSource();
        final String expectedStatement = EMPTY;

        // When
        final String resultStatement = nameFiltering( aTableAlias, noFiltersParameterSource );

        // Then
        assertThat( resultStatement, is( expectedStatement ) );
    }

    @Test
    public void testCommonFilteringUsingOneTableAliasAndIlikeFilterIsNull()
    {
        // Given
        final String aTableAlias = "de";
        final MapSqlParameterSource theParameterSource = new MapSqlParameterSource()
            .addValue( NAME, null );

        // When throws
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> nameFiltering( aTableAlias, theParameterSource ) );

        assertThat( thrown.getMessage(), containsString( NAME + " cannot be null and must be a String." ) );
    }

    @Test
    public void testCommonFilteringUsingOneTableAliasAndIlikeFilterIsEmpty()
    {
        // Given
        final String aTableAlias = "de";
        final MapSqlParameterSource theParameterSource = new MapSqlParameterSource()
            .addValue( NAME, EMPTY );

        // When throws
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> nameFiltering( aTableAlias, theParameterSource ) );

        assertThat( thrown.getMessage(), containsString( NAME + " cannot be null/blank." ) );
    }

    @Test
    public void testCommonFilteringUsingTwoTableAliasAndIlikeFilterIsSet()
    {
        // Given
        final String tableAlias1 = "de";
        final String tableAlias2 = "p";
        final MapSqlParameterSource theParameterSource = new MapSqlParameterSource()
            .addValue( NAME, "abc" );
        final String expectedStatement = " AND (" + tableAlias1 + ".\"name\" ILIKE :" + NAME + " OR "
            + tableAlias2 + ".\"name\" ILIKE :" + NAME + ")";

        // When
        final String resultStatement = FilteringStatement.nameFiltering( tableAlias1, tableAlias2, theParameterSource );

        // Then
        assertThat( resultStatement, is( expectedStatement ) );
    }

    @Test
    public void testCommonFilteringUsingTwoTableAliasAndIlikeFilterIsNotSet()
    {
        // Given
        final String tableAlias1 = "de";
        final String tableAlias2 = "p";
        final MapSqlParameterSource noFiltersParameterSource = new MapSqlParameterSource();
        final String expectedStatement = EMPTY;

        // When
        final String resultStatement = FilteringStatement.nameFiltering( tableAlias1, tableAlias2,
            noFiltersParameterSource );

        // Then
        assertThat( resultStatement, is( expectedStatement ) );
    }

    @Test
    public void testValueTypeFilteringUsingOneTableAliasWhenFilterIsSet()
    {
        // Given
        final String aTableAlias = "de";
        final MapSqlParameterSource theParameterSource = new MapSqlParameterSource()
            .addValue( VALUE_TYPES, unmodifiableSet( "NUMBER", "INTEGER" ) );
        final String expectedStatement = " AND (" + aTableAlias + ".valuetype IN (:" + VALUE_TYPES + "))";

        // When
        final String resultStatement = valueTypeFiltering( aTableAlias, theParameterSource );

        // Then
        assertThat( resultStatement, is( expectedStatement ) );
    }

    @Test
    public void testValueTypeFilteringUsingOneTableAliasWhenFilterIsNotSet()
    {
        // Given
        final String aTableAlias = "de";
        final MapSqlParameterSource noFiltersParameterSource = new MapSqlParameterSource();
        final String expectedStatement = EMPTY;

        // When
        final String resultStatement = valueTypeFiltering( aTableAlias, noFiltersParameterSource );

        // Then
        assertThat( resultStatement, is( expectedStatement ) );
    }

    @Test
    public void testValueTypeFilteringUsingOneTableAliasWhenFilterHasEmptySet()
    {
        // Given
        final String aTableAlias = "de";
        final MapSqlParameterSource filtersParameterSource = new MapSqlParameterSource()
            .addValue( VALUE_TYPES, emptySet() );

        // When throws
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> valueTypeFiltering( aTableAlias, filtersParameterSource ) );

        assertThat( thrown.getMessage(), containsString( VALUE_TYPES + " cannot be empty." ) );
    }

    @Test
    public void testValueTypeFilteringUsingOneTableAliasWhenFilterIsSetToNull()
    {
        // Given
        final String aTableAlias = "de";
        final MapSqlParameterSource filtersParameterSource = new MapSqlParameterSource()
            .addValue( VALUE_TYPES, null );

        // When throws
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> valueTypeFiltering( aTableAlias, filtersParameterSource ) );

        // Then
        assertThat( thrown.getMessage(), containsString( VALUE_TYPES + " cannot be null and must be a Set." ) );
    }

    @Test
    public void testValueTypeFilteringUsingOneTableAliasWhenFilterIsNotSetInstance()
    {
        // Given
        final String aTableAlias = "de";
        final MapSqlParameterSource filtersParameterSource = new MapSqlParameterSource()
            .addValue( VALUE_TYPES, "String" );

        // When throws
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> valueTypeFiltering( aTableAlias, filtersParameterSource ) );

        // Then
        assertThat( thrown.getMessage(), containsString( "valueTypes cannot be null and must be a Set." ) );
    }

    @Test
    public void testSkipNumberValueTypeWhenNumberTypeIsPresentInParameters()
    {
        // Given
        final MapSqlParameterSource theParameterSource = new MapSqlParameterSource()
            .addValue( VALUE_TYPES, unmodifiableSet( "NUMBER", "INTEGER" ) );
        final boolean expectedResult = false;

        // When
        final boolean actualResult = skipValueType( NUMBER, theParameterSource );

        // Then
        assertThat( actualResult, is( expectedResult ) );
    }

    @Test
    public void testSkipNumberValueTypeWhenNumberTypeIsNotPresentInParameters()
    {
        // Given
        final MapSqlParameterSource theParameterSource = new MapSqlParameterSource()
            .addValue( VALUE_TYPES, unmodifiableSet( "BOOLEAN", "INTEGER" ) );
        final boolean expectedResult = true;

        // When
        final boolean actualResult = skipValueType( NUMBER, theParameterSource );

        // Then
        assertThat( actualResult, is( expectedResult ) );
    }
}
