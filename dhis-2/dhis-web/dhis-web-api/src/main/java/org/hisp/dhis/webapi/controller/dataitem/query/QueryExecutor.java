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

import static org.apache.commons.lang3.StringUtils.SPACE;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.wrap;
import static org.hisp.dhis.common.DimensionItemType.DATA_ELEMENT;
import static org.hisp.dhis.common.DimensionItemType.INDICATOR;
import static org.hisp.dhis.common.DimensionItemType.PROGRAM_ATTRIBUTE;
import static org.hisp.dhis.common.DimensionItemType.PROGRAM_INDICATOR;
import static org.hisp.dhis.common.DimensionItemType.REPORTING_RATE;
import static org.hisp.dhis.webapi.controller.dataitem.helper.FilteringHelper.extractValueFromIlikeNameFilter;
import static org.springframework.util.Assert.notNull;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.common.BaseDimensionalItemObject;
import org.hisp.dhis.common.ValueType;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.program.ProgramDataElementDimensionItem;
import org.hisp.dhis.program.ProgramIndicator;
import org.hisp.dhis.program.ProgramTrackedEntityAttributeDimensionItem;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.webapi.controller.dataitem.DataItemViewObject;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class QueryExecutor
{

    private final ProgramDataElementDimensionQuery programDataElementDimensionQuery;

    private final CurrentUserService currentUserService;

    public List<DataItemViewObject> execute( final Class<? extends BaseDimensionalItemObject> entity,
        final List<String> filters )
    {
        final List<DataItemViewObject> dataItemViewObjects = new ArrayList<>();

        final User user = currentUserService.getCurrentUser();

        if ( isEquals( entity, ProgramDataElementDimensionItem.class ) )
        {
            final MapSqlParameterSource paramsMap = new MapSqlParameterSource().addValue( "userId",
                user.getId() );

            boolean filterByValueType = false;
            boolean filterByIlikeName = false;

            final String ilikeName = extractValueFromIlikeNameFilter( filters );

            if ( isNotBlank( ilikeName ) )
            {
                paramsMap.addValue( "ilikeName", wrap( ilikeName, "%" ) );
                filterByIlikeName = true;
            }

            // if ( containsValueTypeFilter( filters ) )
            // {
            // paramsMap.addValue( "valueTypes", extractAllValueTypesFromFilters( filters )
            // );
            // filterByValueType = true;
            // }
            return programDataElementDimensionQuery.find(paramsMap);
        }
        else if ( isEquals( entity, ProgramTrackedEntityAttributeDimensionItem.class ) )
        {
            final MapSqlParameterSource paramsMap = new MapSqlParameterSource().addValue( "userId",
                user.getId() );

            boolean filterByValueType = false;
            boolean filterByIlikeName = false;

            final String ilikeName = extractValueFromIlikeNameFilter( filters );

            if ( isNotBlank( ilikeName ) )
            {
                paramsMap.addValue( "ilike", wrap( ilikeName, "%" ) );
                filterByIlikeName = true;
            }

            // if ( containsValueTypeFilter( filters ) )
            // {
            // paramsMap.addValue( "valueTypes", extractAllValueTypesFromFilters( filters )
            // );
            // filterByValueType = true;
            // }

//            final SqlRowSet rowSet = namedParameterJdbcTemplate.queryForRowSet(
//                getProgramAttributeQueryWith( filterByValueType, filterByIlikeName ), paramsMap );
//
//            while ( rowSet.next() )
//            {
//                final DataItemViewObject viewItem = new DataItemViewObject();
//                final ValueType valueType = ValueType.fromString( rowSet.getString( "valuetype" ) );
//
//                viewItem.setName( rowSet.getString( "program_name" ) + SPACE + rowSet.getString( "name" ) );
//                viewItem.setValueType( valueType );
//                viewItem.setCombinedId( rowSet.getString( "program_uid" ) + "." + rowSet.getString( "uid" ) );
//                viewItem.setProgramId( rowSet.getString( "program_uid" ) );
//                viewItem.setUid( rowSet.getString( "uid" ) );
//                viewItem.setDimensionItemType( PROGRAM_ATTRIBUTE );
//
//                dataItemViewObjects.add( viewItem );
//            }
        }
        else if ( isEquals( entity, ProgramIndicator.class ) )
        {
            final MapSqlParameterSource paramsMap = new MapSqlParameterSource().addValue( "userId",
                user.getId() );

            boolean filterByIlikeName = false;

            final String ilikeName = extractValueFromIlikeNameFilter( filters );

            if ( isNotBlank( ilikeName ) )
            {
                paramsMap.addValue( "ilike", wrap( ilikeName, "%" ) );
                filterByIlikeName = true;
            }

//            final SqlRowSet rowSet = namedParameterJdbcTemplate.queryForRowSet(
//                getProgramIndicatorQuery( filterByIlikeName ), paramsMap );
//
//            while ( rowSet.next() )
//            {
//                final DataItemViewObject viewItem = new DataItemViewObject();
//
//                viewItem.setName( rowSet.getString( "name" ) );
//                viewItem.setUid( rowSet.getString( "uid" ) );
//                viewItem.setDimensionItemType( PROGRAM_INDICATOR );
//
//                dataItemViewObjects.add( viewItem );
//            }
        }
        else if ( isEquals( entity, DataSet.class ) )
        {
            final MapSqlParameterSource paramsMap = new MapSqlParameterSource().addValue( "userId",
                user.getId() );

            boolean filterByIlikeName = false;

            final String ilikeName = extractValueFromIlikeNameFilter( filters );

            if ( isNotBlank( ilikeName ) )
            {
                paramsMap.addValue( "ilike", wrap( ilikeName, "%" ) );
                filterByIlikeName = true;
            }

//            final SqlRowSet rowSet = namedParameterJdbcTemplate
//                .queryForRowSet( getDateSetQuery( filterByIlikeName ), paramsMap );
//
//            while ( rowSet.next() )
//            {
//                final DataItemViewObject viewItem = new DataItemViewObject();
//
//                viewItem.setName( rowSet.getString( "name" ) );
//                viewItem.setUid( rowSet.getString( "uid" ) );
//                viewItem.setDimensionItemType( REPORTING_RATE );
//
//                // Setting the dimension type to REPORTING_RATE, for all DataSet object.
//                for ( final String metric : METRICS )
//                {
//                    viewItem.getReportMetrics().add( viewItem.getDisplayFormName() + " (" + metric + ")" );
//                }
//
//                dataItemViewObjects.add( viewItem );
//            }
        }
        else if ( isEquals( entity, Indicator.class ) )
        {
            final MapSqlParameterSource paramsMap = new MapSqlParameterSource().addValue( "userId",
                user.getId() );

            boolean filterByIlikeName = false;

            final String ilikeName = extractValueFromIlikeNameFilter( filters );

            if ( isNotBlank( ilikeName ) )
            {
                paramsMap.addValue( "ilike", wrap( ilikeName, "%" ) );
                filterByIlikeName = true;
            }

//            final SqlRowSet rowSet = namedParameterJdbcTemplate
//                .queryForRowSet( getIndicatorQuery( filterByIlikeName ), paramsMap );
//
//            while ( rowSet.next() )
//            {
//                final DataItemViewObject viewItem = new DataItemViewObject();
//
//                viewItem.setName( rowSet.getString( "name" ) );
//                viewItem.setUid( rowSet.getString( "uid" ) );
//                viewItem.setDimensionItemType( INDICATOR );
//
//                dataItemViewObjects.add( viewItem );
//            }
        }
        else if ( isEquals( entity, DataElement.class ) )
        {
            final MapSqlParameterSource paramsMap = new MapSqlParameterSource().addValue( "userId",
                user.getId() );

            boolean filterByValueType = false;
            boolean filterByIlikeName = false;

            final String ilikeName = extractValueFromIlikeNameFilter( filters );

            if ( isNotBlank( ilikeName ) )
            {
                paramsMap.addValue( "ilike", wrap( ilikeName, "%" ) );
                filterByIlikeName = true;
            }

            // if ( containsValueTypeFilter( filters ) )
            // {
            // paramsMap.addValue( "valueTypes", extractAllValueTypesFromFilters( filters )
            // );
            // filterByValueType = true;
            // }

//            final SqlRowSet rowSet = namedParameterJdbcTemplate.queryForRowSet(
//                getDataElementQueryWith( filterByValueType, filterByIlikeName ), paramsMap );
//
//            while ( rowSet.next() )
//            {
//                final DataItemViewObject viewItem = new DataItemViewObject();
//                final ValueType valueType = ValueType.fromString( rowSet.getString( "valuetype" ) );
//
//                viewItem.setName( rowSet.getString( "name" ) );
//                viewItem.setValueType( valueType );
//                viewItem.setUid( rowSet.getString( "uid" ) );
//                viewItem.setDimensionItemType( DATA_ELEMENT );
//
//                dataItemViewObjects.add( viewItem );
//            }
        }

        return dataItemViewObjects;
    }

    private boolean isEquals( final Class<? extends BaseDimensionalItemObject> entity,
        final Class<? extends BaseDimensionalItemObject> other )
    {
        notNull( entity, "The entity must not be null" );
        notNull( entity, "The other must not be null" );

        return entity.getSimpleName().equals( other.getSimpleName() );
    }
}
