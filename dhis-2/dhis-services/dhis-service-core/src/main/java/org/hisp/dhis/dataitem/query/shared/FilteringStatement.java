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

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.hisp.dhis.dataitem.query.DataItemQuery.DISPLAY_NAME;
import static org.hisp.dhis.dataitem.query.DataItemQuery.LOCALE;
import static org.hisp.dhis.dataitem.query.DataItemQuery.NAME;
import static org.hisp.dhis.dataitem.query.DataItemQuery.VALUE_TYPES;
import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.isInstanceOf;
import static org.springframework.util.Assert.notEmpty;
import static org.springframework.util.Assert.notNull;

import java.util.Set;

import org.hisp.dhis.common.ValueType;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

/**
 * This class held common filtering SQL statements for data items.
 *
 * @author maikel arabori
 */
public class FilteringStatement
{
    private FilteringStatement()
    {
    }

    public static String nameFiltering( final String tableName, final MapSqlParameterSource paramsMap )
    {
        final StringBuilder filtering = new StringBuilder();

        if ( paramsMap != null && paramsMap.hasValue( NAME ) )
        {
            isInstanceOf( String.class, paramsMap.getValue( NAME ),
                NAME + " cannot be null and must be a String." );
            hasText( (String) paramsMap.getValue( NAME ), NAME + " cannot be null/blank." );

            filtering.append( " AND (" + tableName + ".\"name\" ILIKE :" + NAME + ")" );
        }

        // filtering.append( displayNameAndLocaleFiltering( tableName, paramsMap
        // ) );

        return filtering.toString();
    }

    public static String nameFiltering( final String tableOne, final String tableTwo,
        final MapSqlParameterSource paramsMap )
    {
        final StringBuilder filtering = new StringBuilder();

        if ( paramsMap != null && paramsMap.hasValue( NAME ) )
        {
            isInstanceOf( String.class, paramsMap.getValue( NAME ),
                NAME + " cannot be null and must be a String." );
            hasText( (String) paramsMap.getValue( NAME ), NAME + " cannot be null/blank." );

            filtering.append( " AND (" + tableOne + ".\"name\" ILIKE :" + NAME + " OR " + tableTwo
                + ".\"name\" ILIKE :" + NAME + ")" );
        }

        return filtering.toString();
    }

    public static String valueTypeFiltering( final String tableName, final MapSqlParameterSource paramsMap )
    {
        final StringBuilder filtering = new StringBuilder();

        if ( paramsMap != null && paramsMap.hasValue( VALUE_TYPES ) )
        {
            isInstanceOf( Set.class, paramsMap.getValue( VALUE_TYPES ),
                VALUE_TYPES + " cannot be null and must be a Set." );
            notEmpty( (Set) paramsMap.getValue( VALUE_TYPES ), VALUE_TYPES + " cannot be empty." );

            filtering.append( " AND (" + tableName + ".valuetype IN (:" + VALUE_TYPES + "))" );
        }

        return filtering.toString();
    }

    public static boolean skipValueType( final ValueType valueTypeToSkip, final MapSqlParameterSource paramsMap )
    {
        if ( paramsMap != null && paramsMap.hasValue( VALUE_TYPES ) )
        {
            isInstanceOf( Set.class, paramsMap.getValue( VALUE_TYPES ), VALUE_TYPES + " must be a Set." );
            notNull( paramsMap.getValue( VALUE_TYPES ), VALUE_TYPES + " cannot be null." );
            notEmpty( (Set) paramsMap.getValue( VALUE_TYPES ), VALUE_TYPES + " cannot be empty." );

            final Set<String> valueTypeNames = (Set<String>) paramsMap.getValue( VALUE_TYPES );

            // Skip WHEN the value type list does NOT contain a NUMBER type.
            // This is specific for Indicator's types, as they don't have a
            // value type, but
            // are always interpreted as NUMBER.
            return !valueTypeNames.contains( valueTypeToSkip.name() );
        }

        return false;
    }

    public static String displayNameAndLocaleFiltering( final String tableName,
        final MapSqlParameterSource paramsMap )
    {
        if ( paramsMap != null && paramsMap.hasValue( DISPLAY_NAME ) )
        {
            isInstanceOf( String.class, paramsMap.getValue( DISPLAY_NAME ),
                DISPLAY_NAME + " cannot be null and must be a String." );
            hasText( (String) paramsMap.getValue( DISPLAY_NAME ), DISPLAY_NAME + " cannot be null/blank." );

            if ( paramsMap.hasValue( LOCALE ) )
            {
                isInstanceOf( String.class, paramsMap.getValue( LOCALE ),
                    LOCALE + " cannot be null and must be a String." );
                hasText( (String) paramsMap.getValue( LOCALE ), LOCALE + " cannot be null/blank." );

                final StringBuilder displayNameQuery = new StringBuilder();

                displayNameQuery
                    .append( " AND displayname.locale = :" + LOCALE )
                    .append( " AND displayname.property = 'NAME' AND displayname.value ILIKE :" + DISPLAY_NAME )
                    .append( " UNION " )
                    .append( " SELECT " + commonColumns( tableName ) )
                    .append( " FROM " + tableName + ", jsonb_to_recordset(" + tableName
                        + ".translations) as displayname(locale TEXT, property TEXT)" )
                    .append( " WHERE " + tableName + ".uid" )
                    .append( " NOT IN (" )
                    .append( " SELECT " + tableName + ".uid FROM " + tableName + "," )
                    .append( " jsonb_to_recordset(" + tableName
                        + ".translations) as displayname(locale TEXT, property TEXT)" )
                    .append( " WHERE displayname.locale = :" + LOCALE )
                    .append( ")" )
                    .append( " AND displayname.property = 'NAME'" )
                    .append( " AND " + tableName + ".name ILIKE :" + DISPLAY_NAME )
                    .append( " UNION " )
                    .append( " SELECT " + commonColumns( tableName ) )
                    .append( " FROM " + tableName )
                    .append( " WHERE " + tableName + ".translations = '[]' OR " + tableName + ".translations IS NULL" )
                    .append( " AND " + tableName + ".name ILIKE :" + DISPLAY_NAME );

                return displayNameQuery.toString();

                // return " AND (EXISTS (SELECT * FROM jsonb_array_elements(" +
                // tableAlias + ".translations)"
                // + " AS x(o) WHERE x.o ->> 'property' = 'NAME' "
                // + " AND x.o ->> 'locale' = :locale AND x.o ->> 'value' ILIKE
                // :" + DISPLAY_NAME + ")"
                // + " OR " + tableAlias + ".\"name\" ILIKE :" + DISPLAY_NAME +
                // ")";
            }
            else
            {
                // No locale, so we default the comparison to the raw name.
                return " AND (" + tableName + ".\"name\" ILIKE :" + NAME + ")";
            }
        }
        else
        {
            // DO NOT FILTER by display name, as no filter was set
            if ( paramsMap.hasValue( LOCALE ) ) // has locale
            {
                // BRING ALL i18n names based on locale instead.
                final StringBuilder displayNameQuery = new StringBuilder();

                displayNameQuery
                    .append( " AND displayname.locale = :" + LOCALE )
                    .append( " AND displayname.property = 'NAME'" )
                    .append( " UNION " )
                    .append( " SELECT " + commonColumns( tableName ) )
                    .append( " FROM " + tableName + ", jsonb_to_recordset(" + tableName
                        + ".translations) as displayname(locale TEXT, property TEXT)" )
                    .append( " WHERE " + tableName + ".uid" )
                    .append( " NOT IN (" )
                    .append( " SELECT " + tableName + ".uid FROM " + tableName + "," )
                    .append( " jsonb_to_recordset(" + tableName
                        + ".translations) as displayname(locale TEXT, property TEXT)" )
                    .append( " WHERE displayname.locale = :" + LOCALE )
                    .append( ")" )
                    .append( " AND displayname.property = 'NAME'" )
                    .append( " UNION " )
                    .append( " SELECT " + commonColumns( tableName ) )
                    .append( " FROM " + tableName )
                    .append( " WHERE " + tableName + ".translations = '[]' OR " + tableName + ".translations IS NULL" );

                return displayNameQuery.toString();
            }
        }

        return EMPTY;
    }

    private static String displayNameAndLocaleFiltering( final String tableOne, final String tableTwo,
        final MapSqlParameterSource paramsMap )
    {
        if ( paramsMap != null && paramsMap.hasValue( DISPLAY_NAME ) )
        {
            isInstanceOf( String.class, paramsMap.getValue( DISPLAY_NAME ),
                DISPLAY_NAME + " cannot be null and must be a String." );
            hasText( (String) paramsMap.getValue( DISPLAY_NAME ), DISPLAY_NAME + " cannot be null/blank." );

            if ( paramsMap.hasValue( LOCALE ) )
            {
                isInstanceOf( String.class, paramsMap.getValue( LOCALE ),
                    LOCALE + " cannot be null and must be a String." );
                hasText( (String) paramsMap.getValue( LOCALE ), LOCALE + " cannot be null/blank." );

                final StringBuilder displayNameQuery = new StringBuilder();

                displayNameQuery
                    .append( " AND displayname.locale = :" + LOCALE )
                    .append( " AND ((displayname.property = 'NAME' AND displayname.value ILIKE :" + DISPLAY_NAME + ")" )
                    .append(
                        " OR (displayname2.property = 'NAME' AND displayname2.value ILIKE :" + DISPLAY_NAME + "))" )
                    .append( " UNION " )
                    .append( " SELECT " + commonColumns( tableOne ) )
                    .append( " FROM " + tableOne + ", jsonb_to_recordset(" + tableOne
                        + ".translations) as displayname(locale TEXT, property TEXT), "
                        + tableTwo + ", jsonb_to_recordset(" + tableTwo
                        + ".translations) as displayname2(locale TEXT, property TEXT)" )
                    .append( " WHERE " + tableOne + ".uid" )
                    .append( " NOT IN (" )
                    .append( " SELECT " + tableOne + ".uid FROM " + tableOne + "," )
                    .append( " jsonb_to_recordset(" + tableOne
                        + ".translations) as displayname(locale TEXT, property TEXT)" ) // TODO:
                                                                                        // MAIKEL:
                                                                                        // Should
                                                                                        // we
                                                                                        // handle
                                                                                        // the
                                                                                        // two
                                                                                        // tables?
                    .append( " WHERE displayname.locale = :" + LOCALE )
                    .append( ")" )
                    .append( " AND (displayname.property = 'NAME'" )
                    .append( " AND " + tableOne + ".name ILIKE :" + DISPLAY_NAME + ")" )
                    .append( " OR (displayname2.property = 'NAME'" )
                    .append( " AND " + tableTwo + ".name ILIKE :" + DISPLAY_NAME + ")" )
                    .append( " UNION " )
                    .append( " SELECT " + commonColumns( tableOne ) )
                    .append( " FROM " + tableOne )
                    .append( " WHERE " + tableOne + ".translations = '[]' OR " + tableOne + ".translations IS NULL" )
                    .append( " AND ((" + tableOne + ".name ILIKE :" + DISPLAY_NAME + ")" )
                    .append( " OR (" + tableTwo + ".name ILIKE :" + DISPLAY_NAME + "))" );

                return displayNameQuery.toString();

                // return " AND (EXISTS (SELECT * FROM jsonb_array_elements(" +
                // tableAlias + ".translations)"
                // + " AS x(o) WHERE x.o ->> 'property' = 'NAME' "
                // + " AND x.o ->> 'locale' = :locale AND x.o ->> 'value' ILIKE
                // :" + DISPLAY_NAME + ")"
                // + " OR " + tableAlias + ".\"name\" ILIKE :" + DISPLAY_NAME +
                // ")";
            }
            else
            {
                // No locale, so we default the comparison to the raw name.
                return " AND (" + tableOne + ".\"name\" ILIKE :" + NAME + ")";
            }
        }
        else
        {
            // DO NOT FILTER by display name, as no filter was set
            if ( paramsMap.hasValue( LOCALE ) ) // has locale
            {
                // BRING ALL i18n names based on locale instead.
                final StringBuilder displayNameQuery = new StringBuilder();

                displayNameQuery
                    .append( " AND displayname.locale = :" + LOCALE )
                    .append( " AND displayname.property = 'NAME'" )
                    .append( " UNION " )
                    .append( " SELECT " + commonColumns( tableOne ) )
                    .append( " FROM " + tableOne + ", jsonb_to_recordset(" + tableOne
                        + ".translations) as displayname(locale TEXT, property TEXT)" )
                    .append( " WHERE " + tableOne + ".uid" )
                    .append( " NOT IN (" )
                    .append( " SELECT " + tableOne + ".uid FROM " + tableOne + "," )
                    .append( " jsonb_to_recordset(" + tableOne
                        + ".translations) as displayname(locale TEXT, property TEXT)" )
                    .append( " WHERE displayname.locale = :" + LOCALE )
                    .append( ")" )
                    .append( " AND displayname.property = 'NAME'" )
                    .append( " UNION " )
                    .append( " SELECT " + commonColumns( tableOne ) )
                    .append( " FROM " + tableOne )
                    .append( " WHERE " + tableOne + ".translations = '[]' OR " + tableOne + ".translations IS NULL" );

                return displayNameQuery.toString();
            }
        }

        return EMPTY;
    }

    private static String commonColumns( final String tableName )
    {
        String tableColumns = "null, null, " + tableName + ".uid," + tableName + ".\"name\", ";

        if ( equalsIgnoreCase( tableName, "dataelement" ) )
        {
            tableColumns += tableName + ".valuetype, ";
        }
        else
        {
            tableColumns += " null, ";
        }

        return tableColumns + tableName + ".code," + tableName + ".translations, " + tableName
            + ".\"name\" AS i18n_name";
    }

    // private static String displayNameAndLocaleFiltering( final String
    // tableAlias1, final String tableAlias2,
    // final MapSqlParameterSource paramsMap )
    // {
    // if ( paramsMap != null && paramsMap.hasValue( DISPLAY_NAME ) )
    // {
    // isInstanceOf( String.class, paramsMap.getValue( DISPLAY_NAME ),
    // DISPLAY_NAME + " cannot be null and must be a String." );
    // hasText( (String) paramsMap.getValue( DISPLAY_NAME ), DISPLAY_NAME + "
    // cannot be null/blank." );
    //
    // if ( paramsMap.hasValue( LOCALE ) )
    // {
    // isInstanceOf( String.class, paramsMap.getValue( LOCALE ),
    // LOCALE + " cannot be null and must be a String." );
    // hasText( (String) paramsMap.getValue( LOCALE ), LOCALE + " cannot be
    // null/blank." );
    //
    // return " AND (EXISTS (SELECT * FROM jsonb_array_elements(" + tableAlias1
    // + ".translations)"
    // + " AS x(o) WHERE x.o ->> 'property' = 'NAME' "
    // + " AND x.o ->> 'locale' = :locale AND x.o ->> 'value' ILIKE :" +
    // DISPLAY_NAME + ")"
    // + " OR " + tableAlias1 + ".\"name\" ILIKE :" + DISPLAY_NAME + ")"
    // + " OR"
    // + " (EXISTS (SELECT * FROM jsonb_array_elements(" + tableAlias2 +
    // ".translations)"
    // + " AS x(o) WHERE x.o ->> 'property' = 'NAME' "
    // + " AND x.o ->> 'locale' = :locale AND x.o ->> 'value' ILIKE :" +
    // DISPLAY_NAME + ")"
    // + " OR " + tableAlias2 + ".\"name\" ILIKE :" + DISPLAY_NAME + ")";
    // }
    // else
    // {
    // // No locale, so we default the comparison to the raw name.
    // return " AND (" + tableAlias1 + ".\"name\" ILIKE :" + NAME + " OR " +
    // tableAlias2
    // + ".\"name\" ILIKE :" + NAME + ")";
    // }
    // }
    //
    // return EMPTY;
    // }
}
