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

import static org.hisp.dhis.dataitem.query.DataItemQuery.USER_GROUP_UIDS;
import static org.hisp.dhis.dataitem.query.DataItemQuery.USER_UID;
import static org.hisp.dhis.hibernate.jsonb.type.JsonbFunctions.CHECK_USER_GROUPS_ACCESS;
import static org.hisp.dhis.hibernate.jsonb.type.JsonbFunctions.HAS_USER_GROUP_IDS;
import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.isInstanceOf;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

/**
 * This class held common user access SQL statements for data items.
 *
 * @author maikel arabori
 */
public class UserAccessStatement
{
    private UserAccessStatement()
    {
    }

    public static String sharingConditions( final String tableName, final MapSqlParameterSource paramsMap )
    {
        final StringBuilder conditions = new StringBuilder();

        conditions
            .append( publicAccessCondition( tableName ) )
            .append( " OR " )
            .append( ownerAccessCondition( tableName ) )
            .append( " OR " )
            .append( userAccessCondition( tableName ) );

        if ( paramsMap != null && paramsMap.hasValue( USER_GROUP_UIDS ) )
        {
            isInstanceOf( String.class, paramsMap.getValue( USER_GROUP_UIDS ),
                USER_GROUP_UIDS + " must be a String." );
            hasText( (String) paramsMap.getValue( USER_GROUP_UIDS ), USER_GROUP_UIDS + " cannot be null/blank." );

            conditions.append( " OR (" + userGroupAccessCondition( tableName ) + ")" );
        }

        return conditions.toString();
    }

    public static String sharingConditions( final String tableOne, final String tableTwo,
        final MapSqlParameterSource paramsMap )
    {
        final StringBuilder conditions = new StringBuilder();

        conditions
            .append( "(" ) // Table 1 conditions
            .append( publicAccessCondition( tableOne ) )
            .append( " OR " )
            .append( ownerAccessCondition( tableOne ) )
            .append( " OR " )
            .append( userAccessCondition( tableOne ) )
            .append( ")" ) // Table 1 conditions end
            .append( " AND (" ) // Table 2 conditions
            .append( publicAccessCondition( tableTwo ) )
            .append( " OR " )
            .append( ownerAccessCondition( tableTwo ) )
            .append( " OR " )
            .append( userAccessCondition( tableTwo ) )
            .append( ")" ); // Table 2 conditions end

        if ( paramsMap != null && paramsMap.hasValue( USER_GROUP_UIDS ) )
        {
            isInstanceOf( String.class, paramsMap.getValue( USER_GROUP_UIDS ),
                USER_GROUP_UIDS + " must be a String." );
            hasText( (String) paramsMap.getValue( USER_GROUP_UIDS ), USER_GROUP_UIDS + " cannot be null/blank." );

            conditions.append( " OR (" );

            // Program group access checks
            conditions.append( userGroupAccessCondition( tableOne ) );

            // DataElement access checks
            conditions.append( " AND " + userGroupAccessCondition( tableTwo ) );

            // Closing OR condition
            conditions.append( ")" );
        }

        return conditions.toString();
    }

    static String ownerAccessCondition( final String tableName )
    {
        assertTableAlias( tableName );

        return "(jsonb_extract_path_text(" + tableName + ".sharing, 'owner') IS NULL OR "
            + "jsonb_extract_path_text(" + tableName + ".sharing, 'owner') = 'null' OR "
            + "jsonb_extract_path_text(" + tableName + ".sharing, 'owner') = :userUid)";
    }

    static String publicAccessCondition( final String tableName )
    {
        assertTableAlias( tableName );

        return "(jsonb_extract_path_text(" + tableName + ".sharing, 'public') IS NULL OR "
            + "jsonb_extract_path_text(" + tableName + ".sharing, 'public') = 'null' OR "
            + "jsonb_extract_path_text(" + tableName + ".sharing, 'public') LIKE 'r%')";
    }

    static String userAccessCondition( final String tableName )
    {
        assertTableAlias( tableName );

        return "(jsonb_has_user_id(" + tableName + ".sharing, :" + USER_UID + ") = TRUE "
            + "AND jsonb_check_user_access(" + tableName + ".sharing, :" + USER_UID + ", 'r%') = TRUE)";
    }

    static String userGroupAccessCondition( final String tableName )
    {
        assertTableAlias( tableName );

        return "(" + HAS_USER_GROUP_IDS + "(" + tableName + ".sharing, :" + USER_GROUP_UIDS + ") = TRUE " +
            "AND " + CHECK_USER_GROUPS_ACCESS + "(" + tableName + ".sharing, 'r%', :" + USER_GROUP_UIDS + ") = TRUE)";
    }

    private static void assertTableAlias( String tableName )
    {
        hasText( tableName, "The argument tableName cannot be null/blank." );
    }
}
