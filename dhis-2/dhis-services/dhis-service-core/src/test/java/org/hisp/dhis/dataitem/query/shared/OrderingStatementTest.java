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

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hisp.dhis.dataitem.query.DataItemQuery.NAME_ORDER;
import static org.hisp.dhis.dataitem.query.shared.OrderingStatement.commonOrdering;
import static org.junit.Assert.assertThrows;

import org.junit.Test;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

/**
 * Unit tests for OrderingStatement.
 *
 * @author maikel arabori
 */
public class OrderingStatementTest
{
    @Test
    public void testCommonOrderingWhenOrderIsAsc()
    {
        // Given
        final String tableAlias = "t";
        final MapSqlParameterSource theParameterSource = new MapSqlParameterSource()
            .addValue( NAME_ORDER, "asc" );
        final String expectedStatement = " ORDER BY " + tableAlias + ".\"name\" ASC";

        // When
        final String actualStatement = commonOrdering( tableAlias, theParameterSource );

        // Then
        assertThat( actualStatement, is( expectedStatement ) );
    }

    @Test
    public void testCommonOrderingWhenOrderIsDesc()
    {
        // Given
        final String tableAlias = "t";
        final MapSqlParameterSource theParameterSource = new MapSqlParameterSource()
            .addValue( NAME_ORDER, "desc" );
        final String expectedStatement = " ORDER BY " + tableAlias + ".\"name\" DESC";

        // When
        final String actualStatement = commonOrdering( tableAlias, theParameterSource );

        // Then
        assertThat( actualStatement, is( expectedStatement ) );
    }

    @Test
    public void testCommonOrderingWhenOrderIsNotSet()
    {
        // Given
        final String tableAlias = "t";
        final MapSqlParameterSource noParameterSource = new MapSqlParameterSource();
        final String expectedStatement = EMPTY;

        // When
        final String actualStatement = commonOrdering( tableAlias, noParameterSource );

        // Then
        assertThat( actualStatement, is( expectedStatement ) );
    }

    @Test
    public void testCommonOrderingWhenParameterSourceIsNull()
    {
        // Given
        final String tableAlias = "t";
        final MapSqlParameterSource nullParameterSource = null;
        final String expectedStatement = EMPTY;

        // When
        final String actualStatement = commonOrdering( tableAlias, nullParameterSource );

        // Then
        assertThat( actualStatement, is( expectedStatement ) );
    }

    @Test
    public void testCommonOrderingWhenOrderFilterIsSetToNull()
    {
        // Given
        final String aTableAlias = "de";
        final MapSqlParameterSource theParameterSource = new MapSqlParameterSource()
            .addValue( NAME_ORDER, null );

        // When throws
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> commonOrdering( aTableAlias, theParameterSource ) );

        assertThat( thrown.getMessage(), containsString( NAME_ORDER + " cannot be null and must be a String." ) );
    }

    @Test
    public void testCommonOrderingWhenOrderFilterIsSetToEmpty()
    {
        // Given
        final String aTableAlias = "de";
        final MapSqlParameterSource theParameterSource = new MapSqlParameterSource()
            .addValue( NAME_ORDER, EMPTY );

        // When throws
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> commonOrdering( aTableAlias, theParameterSource ) );

        assertThat( thrown.getMessage(), containsString( NAME_ORDER + " cannot be null/blank." ) );
    }
}
