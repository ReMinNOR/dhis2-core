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

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hisp.dhis.dataitem.query.DataItemQuery.USER_GROUP_UIDS;
import static org.hisp.dhis.dataitem.query.DataItemQuery.USER_ID;
import static org.hisp.dhis.dataitem.query.shared.UserAccessStatement.ownerAccessCondition;
import static org.hisp.dhis.dataitem.query.shared.UserAccessStatement.publicAccessCondition;
import static org.hisp.dhis.dataitem.query.shared.UserAccessStatement.sharingConditions;
import static org.hisp.dhis.dataitem.query.shared.UserAccessStatement.userAccessCondition;
import static org.hisp.dhis.dataitem.query.shared.UserAccessStatement.userGroupAccessCondition;
import static org.hisp.dhis.hibernate.jsonb.type.JsonbFunctions.CHECK_USER_GROUPS_ACCESS;
import static org.hisp.dhis.hibernate.jsonb.type.JsonbFunctions.HAS_USER_GROUP_IDS;
import static org.junit.Assert.assertThrows;

import org.junit.Test;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

/**
 * Unit tests for UserAccessStatement.
 *
 * @author maikel arabori
 */
public class UserAccessStatementTest
{
    @Test
    public void testSharingConditionsUsingOneTableAliasWhenGroupUserIdsIsSet()
    {
        // Given
        final String tableAlias = "t";
        final MapSqlParameterSource theParameterSource = new MapSqlParameterSource()
            .addValue( USER_GROUP_UIDS, "uid-1, uid-2" );

        // When
        final String actualStatement = sharingConditions( tableAlias, theParameterSource );

        // Then
        assertThat( actualStatement, containsString( publicAccessCondition( tableAlias ) ) );
        assertThat( actualStatement, containsString( ownerAccessCondition( tableAlias ) ) );
        assertThat( actualStatement, containsString( userAccessCondition( tableAlias ) ) );
        assertThat( actualStatement, containsString( userGroupAccessCondition( tableAlias ) ) );
    }

    @Test
    public void testSharingConditionsUsingOneTableAliasWhenGroupUserIdsIsSetToNull()
    {
        // Given
        final String tableAlias = "t";
        final MapSqlParameterSource theParameterSource = new MapSqlParameterSource()
            .addValue( USER_GROUP_UIDS, null );

        // When throws
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> sharingConditions( tableAlias, theParameterSource ) );

        // Then
        assertThat( thrown.getMessage(), containsString( USER_GROUP_UIDS + " must be a String." ) );
    }

    @Test
    public void testSharingConditionsUsingOneTableAliasWhenGroupUserIdsIsSetToEmpty()
    {
        // Given
        final String tableAlias = "t";
        final MapSqlParameterSource theParameterSource = new MapSqlParameterSource()
            .addValue( USER_GROUP_UIDS, "" );

        // When throws
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> sharingConditions( tableAlias, theParameterSource ) );

        // Then
        assertThat( thrown.getMessage(), containsString( USER_GROUP_UIDS + " cannot be null/blank." ) );
    }

    @Test
    public void testSharingConditionsUsingOneTableAliasWhenGroupUserIdsIsNotSet()
    {
        // Given
        final String tableAlias = "t";
        final MapSqlParameterSource noParameterSource = new MapSqlParameterSource();

        // When
        final String actualStatement = sharingConditions( tableAlias, noParameterSource );

        // Then
        assertThat( actualStatement, containsString( publicAccessCondition( tableAlias ) ) );
        assertThat( actualStatement, containsString( ownerAccessCondition( tableAlias ) ) );
        assertThat( actualStatement, containsString( userAccessCondition( tableAlias ) ) );
        assertThat( actualStatement, not( containsString( userGroupAccessCondition( tableAlias ) ) ) );
    }

    @Test
    public void testSharingConditionsUsingTwoTableAliasWhenGroupUserIdsIsSet()
    {
        // Given
        final String tableAlias1 = "de";
        final String tableAlias2 = "t";
        final MapSqlParameterSource theParameterSource = new MapSqlParameterSource()
            .addValue( USER_GROUP_UIDS, "uid-1, uid-2" );

        // When
        final String actualStatement = sharingConditions( tableAlias1, tableAlias2, theParameterSource );

        // Then
        assertThat( actualStatement, containsString( publicAccessCondition( tableAlias1 ) ) );
        assertThat( actualStatement, containsString( ownerAccessCondition( tableAlias1 ) ) );
        assertThat( actualStatement, containsString( userAccessCondition( tableAlias1 ) ) );
        assertThat( actualStatement, containsString( userGroupAccessCondition( tableAlias1 ) ) );

        assertThat( actualStatement, containsString( publicAccessCondition( tableAlias2 ) ) );
        assertThat( actualStatement, containsString( ownerAccessCondition( tableAlias2 ) ) );
        assertThat( actualStatement, containsString( userAccessCondition( tableAlias2 ) ) );
        assertThat( actualStatement, containsString( userGroupAccessCondition( tableAlias2 ) ) );
    }

    @Test
    public void testSharingConditionsUsingTwoTableAliasWhenGroupUserIdsIsSetToNull()
    {
        // Given
        final String tableAlias1 = "de";
        final String tableAlias2 = "t";
        final MapSqlParameterSource theParameterSource = new MapSqlParameterSource()
            .addValue( USER_GROUP_UIDS, null );

        // When throws
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> sharingConditions( tableAlias1, tableAlias2, theParameterSource ) );

        // Then
        assertThat( thrown.getMessage(), containsString( USER_GROUP_UIDS + " must be a String." ) );
    }

    @Test
    public void testSharingConditionsUsingTwoTableAliasWhenGroupUserIdsIsSetToEmpty()
    {
        // Given
        final String tableAlias1 = "de";
        final String tableAlias2 = "t";
        final MapSqlParameterSource theParameterSource = new MapSqlParameterSource()
            .addValue( USER_GROUP_UIDS, "" );

        // When throws
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> sharingConditions( tableAlias1, tableAlias2, theParameterSource ) );

        // Then
        assertThat( thrown.getMessage(), containsString( USER_GROUP_UIDS + " cannot be null/blank." ) );
    }

    @Test
    public void testSharingConditionsUsingTwoTableAliasWhenGroupUserIdsIsNotSet()
    {
        // Given
        final String tableAlias1 = "de";
        final String tableAlias2 = "t";
        final MapSqlParameterSource noParameterSource = new MapSqlParameterSource();

        // When
        final String actualStatement = sharingConditions( tableAlias1, tableAlias2, noParameterSource );

        // Then
        assertThat( actualStatement, containsString( publicAccessCondition( tableAlias1 ) ) );
        assertThat( actualStatement, containsString( ownerAccessCondition( tableAlias1 ) ) );
        assertThat( actualStatement, containsString( userAccessCondition( tableAlias1 ) ) );
        assertThat( actualStatement, not( containsString( userGroupAccessCondition( tableAlias1 ) ) ) );

        assertThat( actualStatement, containsString( publicAccessCondition( tableAlias2 ) ) );
        assertThat( actualStatement, containsString( ownerAccessCondition( tableAlias2 ) ) );
        assertThat( actualStatement, containsString( userAccessCondition( tableAlias2 ) ) );
        assertThat( actualStatement, not( containsString( userGroupAccessCondition( tableAlias2 ) ) ) );
    }

    @Test
    public void testOwnerAccessCondition()
    {
        // Given
        final String tableAlias = "de";
        final String expectedStatement = "(jsonb_extract_path_text(" + tableAlias + ".sharing, 'owner') IS NULL OR "
            + "jsonb_extract_path_text(" + tableAlias + ".sharing, 'owner') = 'null' OR "
            + "jsonb_extract_path_text(" + tableAlias + ".sharing, 'owner') = :userUid)";

        // When
        final String actualStatement = ownerAccessCondition( tableAlias );

        // Then
        assertThat( actualStatement, is( expectedStatement ) );
    }

    @Test
    public void testOwnerAccessConditionWhenTableAliasIsNull()
    {
        // Given
        final String nullTableAlias = null;

        // When throws
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> ownerAccessCondition( nullTableAlias ) );

        // Then
        assertThat( thrown.getMessage(), containsString( "The argument tableAlias cannot be null/blank." ) );
    }

    @Test
    public void testPublicAccessCondition()
    {
        // Given
        final String tableAlias = "de";
        final String expectedStatement = "(jsonb_extract_path_text(" + tableAlias + ".sharing, 'public') IS NULL OR "
            + "jsonb_extract_path_text(" + tableAlias + ".sharing, 'public') = 'null' OR "
            + "jsonb_extract_path_text(" + tableAlias + ".sharing, 'public') LIKE 'r%')";

        // When
        final String actualStatement = publicAccessCondition( tableAlias );

        // Then
        assertThat( actualStatement, is( expectedStatement ) );
    }

    @Test
    public void testPublicAccessConditionWhenTableAliasIsEmpty()
    {
        // Given
        final String nullTableAlias = null;

        // When throws
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> publicAccessCondition( nullTableAlias ) );

        // Then
        assertThat( thrown.getMessage(), containsString( "The argument tableAlias cannot be null/blank." ) );
    }

    @Test
    public void testUserAccessCondition()
    {
        // Given
        final String tableAlias = "de";
        final String expectedStatement = "(jsonb_has_user_id(" + tableAlias + ".sharing, :" + USER_ID + ") = TRUE "
            + "AND jsonb_check_user_access(" + tableAlias + ".sharing, :" + USER_ID + ", 'r%') = TRUE)";

        // When
        final String actualStatement = userAccessCondition( tableAlias );

        // Then
        assertThat( actualStatement, is( expectedStatement ) );
    }

    @Test
    public void testUserAccessConditionWhenTableAliasIsEmpty()
    {
        // Given
        final String nullTableAlias = null;

        // When throws
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> userAccessCondition( nullTableAlias ) );

        // Then
        assertThat( thrown.getMessage(), containsString( "The argument tableAlias cannot be null/blank." ) );
    }

    @Test
    public void testUserGroupAccessCondition()
    {
        // Given
        final String tableAlias = "de";
        final String expectedStatement = "(" + HAS_USER_GROUP_IDS + "(" + tableAlias + ".sharing, :" + USER_GROUP_UIDS
            + ") = TRUE " + "AND " + CHECK_USER_GROUPS_ACCESS + "(" + tableAlias + ".sharing, 'r%', :" + USER_GROUP_UIDS
            + ") = TRUE)";

        // When
        final String actualStatement = userGroupAccessCondition( tableAlias );

        // Then
        assertThat( actualStatement, is( expectedStatement ) );
    }

    @Test
    public void testUserGroupAccessConditionWhenTableAliasIsBlank()
    {
        // Given
        final String nullTableAlias = null;

        // When throws
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> userGroupAccessCondition( nullTableAlias ) );

        // Then
        assertThat( thrown.getMessage(), containsString( "The argument tableAlias cannot be null/blank." ) );
    }
}
