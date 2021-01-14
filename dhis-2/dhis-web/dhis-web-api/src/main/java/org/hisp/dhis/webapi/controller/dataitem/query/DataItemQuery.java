package org.hisp.dhis.webapi.controller.dataitem.query;

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

import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.hisp.dhis.common.ValueType.NUMBER;
import static org.hisp.dhis.hibernate.jsonb.type.JsonbFunctions.CHECK_USER_GROUPS_ACCESS;
import static org.hisp.dhis.hibernate.jsonb.type.JsonbFunctions.HAS_USER_GROUP_IDS;

import java.util.List;
import java.util.Set;

import org.hisp.dhis.dataitem.DataItem;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

public interface DataItemQuery
{
    List<DataItem> find( MapSqlParameterSource paramsMap );

    int count( MapSqlParameterSource paramsMap );

    default boolean hasParam( final String paramName, final MapSqlParameterSource paramsMap )
    {
        return paramsMap.hasValue( paramName );
    }

    default String sharingConditions( final String tableAlias, final MapSqlParameterSource paramsMap )
    {
        final StringBuilder conditions = new StringBuilder();

        conditions
            .append( publicAccessCondition( tableAlias ) )
            .append( " OR " )
            .append( ownerAccessCondition( tableAlias ) )
            .append( " OR " )
            .append( userAccessCondition( tableAlias ) );

        if ( hasParam( "userGroupUids", paramsMap ) )
        {
            conditions.append( " OR (" + userGroupAccessCondition( tableAlias ) + ")" );
        }

        return conditions.toString();
    }

    default String sharingConditions( final String tableAlias1, final String tableAlias2,
        final MapSqlParameterSource paramsMap )
    {
        final StringBuilder conditions = new StringBuilder();

        conditions
            .append( "(" ) // Table 1 conditions
            .append( publicAccessCondition( tableAlias1 ) )
            .append( " OR " )
            .append( ownerAccessCondition( tableAlias1 ) )
            .append( " OR " )
            .append( userAccessCondition( tableAlias1 ) )
            .append( ")" ) // Table 1 conditions end
            .append( " AND (" ) // Table 2 conditions
            .append( publicAccessCondition( tableAlias2 ) )
            .append( " OR " )
            .append( ownerAccessCondition( tableAlias2 ) )
            .append( " OR " )
            .append( userAccessCondition( tableAlias2 ) )
            .append( ")" ); // Table 2 conditions end

        if ( hasParam( "userGroupUids", paramsMap ) )
        {
            conditions.append( " OR (" );

            // Program group access checks
            conditions.append( userGroupAccessCondition( tableAlias1 ) );

            // DataElement access checks
            conditions.append( " AND " + userGroupAccessCondition( tableAlias2 ) );

            // Closing OR condition
            conditions.append( ")" );
        }

        return conditions.toString();
    }

    default String ownerAccessCondition( final String tableAlias )
    {
        return "(jsonb_extract_path_text(" + tableAlias + ".sharing, 'owner') IS NULL OR "
            + "jsonb_extract_path_text(" + tableAlias + ".sharing, 'owner') = 'null' OR "
            + "jsonb_extract_path_text(" + tableAlias + ".sharing, 'owner') = :userUid)";
    }

    default String publicAccessCondition( final String tableAlias )
    {
        return "(jsonb_extract_path_text(" + tableAlias + ".sharing, 'public') IS NULL OR "
            + "jsonb_extract_path_text(" + tableAlias + ".sharing, 'public') = 'null' OR "
            + "jsonb_extract_path_text(" + tableAlias + ".sharing, 'public') LIKE 'r%')";
    }

    default String userAccessCondition( final String tableAlias )
    {
        return "(jsonb_has_user_id(" + tableAlias + ".sharing, :userUid) = TRUE "
            + "AND jsonb_check_user_access(" + tableAlias + ".sharing, :userUid, 'r%') = TRUE)";
    }

    default String userGroupAccessCondition( final String tableAlias )
    {
        return "(" + HAS_USER_GROUP_IDS + "(" + tableAlias + ".sharing, :userGroupUids) = TRUE " +
            "AND " + CHECK_USER_GROUPS_ACCESS + "(" + tableAlias + ".sharing, 'r%', :userGroupUids) = TRUE)";
    }

    default String commonOrdering( final String tableAlias, final MapSqlParameterSource paramsMap )
    {
        final StringBuilder ordering = new StringBuilder();

        if ( hasParam( "nameOrder", paramsMap ) )
        {
            if ( "ASC".equalsIgnoreCase( (String) paramsMap.getValue( "nameOrder" ) ) )
            {
                ordering.append( " ORDER BY " + tableAlias + ".\"name\" ASC" );
            }
            else if ( "DESC".equalsIgnoreCase( (String) paramsMap.getValue( "nameOrder" ) ) )
            {
                ordering.append( " ORDER BY " + tableAlias + ".\"name\" DESC" );
            }
        }

        return ordering.toString();
    }

    default String commonFiltering( final String tableAlias, final MapSqlParameterSource paramsMap )
    {
        final StringBuilder filtering = new StringBuilder();

        if ( hasParam( "ilikeName", paramsMap ) && isNotEmpty( (String) paramsMap.getValue( "ilikeName" ) ) )
        {
            filtering.append( " AND (" + tableAlias + ".\"name\" ILIKE :ilikeName)" );
        }

        return filtering.toString();
    }

    default String commonFiltering( final String tableAlias1, final String tableAlias2,
        final MapSqlParameterSource paramsMap )
    {
        final StringBuilder filtering = new StringBuilder();

        if ( paramsMap.hasValue( "ilikeName" ) && isNotEmpty( (String) paramsMap.getValue( "ilikeName" ) ) )
        {
            filtering.append( " AND (" + tableAlias1 + ".\"name\" ILIKE :ilikeName OR " + tableAlias2
                + ".\"name\" ILIKE :ilikeName)" );
        }

        return filtering.toString();
    }

    default boolean skipNumberValueType( final MapSqlParameterSource paramsMap )
    {
        if ( hasParam( "valueTypes", paramsMap ) && paramsMap.getValue( "valueTypes" ) != null )
        {
            final Set<String> valueTypeNames = (Set<String>) paramsMap.getValue( "valueTypes" );

            // Skip WHEN the value type list does NOT contain a NUMBER type.
            // This is specific for Indicator's types, as they don't have a value type, but
            // are always interpreted as NUMBER.
            return !valueTypeNames.contains( NUMBER.name() );
        }

        return false;
    }
}
