package org.hisp.dhis.webapi.controller.dataitem;

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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.emptyList;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.SPACE;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.wrap;
import static org.hisp.dhis.common.DimensionItemType.DATA_ELEMENT;
import static org.hisp.dhis.common.DimensionItemType.INDICATOR;
import static org.hisp.dhis.common.DimensionItemType.PROGRAM_ATTRIBUTE;
import static org.hisp.dhis.common.DimensionItemType.PROGRAM_DATA_ELEMENT;
import static org.hisp.dhis.common.DimensionItemType.PROGRAM_INDICATOR;
import static org.hisp.dhis.common.DimensionItemType.REPORTING_RATE;
import static org.hisp.dhis.webapi.controller.dataitem.helper.FilteringHelper.containsDimensionTypeFilter;
import static org.hisp.dhis.webapi.controller.dataitem.helper.FilteringHelper.extractEntitiesFromInFilter;
import static org.hisp.dhis.webapi.controller.dataitem.helper.FilteringHelper.extractEntityFromEqualFilter;
import static org.hisp.dhis.webapi.controller.dataitem.helper.FilteringHelper.extractValueFromIlikeNameFilter;
import static org.hisp.dhis.webapi.controller.dataitem.helper.FilteringHelper.removePostFilters;
import static org.hisp.dhis.webapi.controller.dataitem.helper.OrderingHelper.sort;
import static org.hisp.dhis.webapi.controller.dataitem.helper.PaginationHelper.slice;
import static org.hisp.dhis.webapi.utils.PaginationUtils.NO_PAGINATION;
import static org.springframework.util.Assert.notNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.common.BaseDimensionalItemObject;
import org.hisp.dhis.common.ValueType;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dxf2.common.OrderParams;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.program.ProgramDataElementDimensionItem;
import org.hisp.dhis.program.ProgramIndicator;
import org.hisp.dhis.program.ProgramTrackedEntityAttributeDimensionItem;
import org.hisp.dhis.query.Pagination;
import org.hisp.dhis.query.Query;
import org.hisp.dhis.query.QueryService;
import org.hisp.dhis.security.acl.AclService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.webapi.webdomain.WebOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableMap;

import lombok.extern.slf4j.Slf4j;

/**
 * This class is tight to the controller layer and is responsible to encapsulate
 * logic that does not belong to the controller but does not belong to the
 * service layer either. In other words, these set of methods sit between the
 * controller and service layers. The main goal is to alleviate the controller
 * layer.
 */
@Slf4j
@Component
public class DataItemServiceFacade
{
    private final int PAGINATION_FIRST_RESULT = 0;

    private final Set<String> METRICS = newHashSet( "Actual reports", "Actual reports on time", "Expected reports",
        "Reporting rate", "Reporting rate on time" );

    private final QueryService queryService;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    
    private final CurrentUserService currentUserService;

    private final AclService aclService;

    /**
     * This Map holds the allowed data types to be queried.
     */
    // @formatter:off
    public static final Map<String, Class<? extends BaseDimensionalItemObject>> DATA_TYPE_ENTITY_MAP = ImmutableMap
        .<String, Class<? extends BaseDimensionalItemObject>> builder()
            .put( "INDICATOR", Indicator.class )
            .put( "DATA_ELEMENT", DataElement.class )
            //.put( "DATA_ELEMENT_OPERAND", DataElementOperand.class )
            .put( "DATA_SET", DataSet.class )
            .put( "PROGRAM_INDICATOR", ProgramIndicator.class )
            .put( "PROGRAM_DATA_ELEMENT", ProgramDataElementDimensionItem.class )
            .put( "PROGRAM_ATTRIBUTE", ProgramTrackedEntityAttributeDimensionItem.class )
            .build();
    // @formatter:on

    DataItemServiceFacade( final QueryService queryService,
        @Qualifier( "readOnlyJdbcTemplate" ) JdbcTemplate jdbcTemplate,
        final CurrentUserService currentUserService, final AclService aclService )
    {
        checkNotNull( queryService );
        checkNotNull( jdbcTemplate );
        checkNotNull( currentUserService );
        checkNotNull( aclService );

        this.queryService = queryService;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate( jdbcTemplate );
        this.currentUserService = currentUserService;
        this.aclService = aclService;
    }

    private String getProgramDataElementQueryWith( final boolean filterByValueType, final boolean filterByIlikeName )
    {
        final StringBuilder sql = new StringBuilder(
            "SELECT p.\"name\" AS program_name, p.uid AS program_uid, p.publicaccess AS program_public_access,"
                + " de.\"name\" AS name, de.uid AS uid, de.valuetype AS valuetype"
                + " FROM programstagedataelement psde, dataelement de, programstage ps, program p"
                + " WHERE p.programid = ps.programid AND psde.programstageid = ps.programstageid AND psde.dataelementid = de.dataelementid"
                + " AND ("
                + " ((p.publicaccess LIKE '__r%' OR p.publicaccess LIKE 'r%' OR p.publicaccess IS NULL)"
                + " AND (de.publicaccess LIKE '__r%' OR de.publicaccess LIKE 'r%' OR de.publicaccess IS NULL))"
                + " OR p.programid IN (SELECT pua.programid FROM programuseraccesses pua WHERE pua.useraccessid"
                + " IN (SELECT useraccessid FROM useraccess WHERE access LIKE '__r%' AND useraccess.userid = :userId))"
                + " OR p.programid IN (SELECT puga.programid FROM programusergroupaccesses puga WHERE puga.usergroupaccessid"
                + " IN (SELECT usergroupaccessid FROM usergroupaccess WHERE access LIKE '__r%' AND usergroupid"
                + " IN (SELECT usergroupid FROM usergroupmembers WHERE userid = :userId)))"
                + " OR de.dataelementid IN (SELECT pua.dataelementid FROM dataelementuseraccesses pua"
                + " WHERE pua.useraccessid IN (SELECT useraccessid FROM useraccess WHERE access LIKE '__r%' AND useraccess.userid = :userId))"
                + " OR de.dataelementid IN (SELECT puga.dataelementid FROM dataelementusergroupaccesses puga"
                + " WHERE puga.usergroupaccessid IN (SELECT usergroupaccessid FROM usergroupaccess WHERE access LIKE '__r%' AND usergroupid"
                + " IN (SELECT usergroupid FROM usergroupmembers WHERE userid = :userId)))"
                + ")");

        if ( filterByIlikeName )
        {
            sql.append( "AND (p.\"name\" ILIKE :ilike OR de.\"name\" ILIKE :ilike)" );
        }

//        if ( filterByValueType )
//        {
//            sql.append( " AND (de.valuetype IN (:valueTypes))" );
//        }

        sql.append( " ORDER BY de.\"name\"" );

        return sql.toString();
    }

    private String getProgramAttributeQueryWith( final boolean filterByValueType, final boolean filterByIlikeName )
    {
        final StringBuilder sql = new StringBuilder(
            "SELECT p.\"name\" AS program_name, p.uid AS program_uid, t.\"name\" AS name, t.uid AS uid, t.valuetype"
                + " AS valuetype FROM program_attributes pa, trackedentityattribute t, program p"
                + " WHERE pa.programid = p.programid AND pa.trackedentityattributeid = t.trackedentityattributeid"
                + " AND ("
                + " ((p.publicaccess LIKE '__r%' OR p.publicaccess LIKE 'r%' OR p.publicaccess IS NULL)"
                + " AND (t.publicaccess LIKE '__r%' OR t.publicaccess LIKE 'r%' OR t.publicaccess IS NULL))"
                + " OR p.programid IN (SELECT pua.programid FROM programuseraccesses pua WHERE pua.useraccessid"
                + " IN (SELECT useraccessid FROM useraccess WHERE access LIKE '__r%' AND useraccess.userid = :userId))"
                + " OR p.programid IN (SELECT puga.programid FROM programusergroupaccesses puga WHERE puga.usergroupaccessid"
                + " IN (SELECT usergroupaccessid FROM usergroupaccess WHERE access LIKE '__r%' AND usergroupid"
                + " IN (SELECT usergroupid FROM usergroupmembers WHERE userid = :userId)))"
                + " OR t.trackedentityattributeid IN (SELECT taua.trackedentityattributeid FROM trackedentityattributeuseraccesses taua"
                + " WHERE taua.useraccessid IN (SELECT useraccessid FROM useraccess WHERE access LIKE '__r%' AND useraccess.userid = :userId))"
                + " OR t.trackedentityattributeid IN (SELECT tagua.trackedentityattributeid FROM trackedentityattributeusergroupaccesses tagua"
                + " WHERE tagua.usergroupaccessid IN (SELECT usergroupaccessid FROM usergroupaccess WHERE access LIKE '__r%' AND usergroupid "
                + " IN (SELECT usergroupid FROM usergroupmembers WHERE userid = :userId)))"
                + ")" );

        if ( filterByIlikeName )
        {
            sql.append( "AND (p.\"name\" ILIKE :ilike OR t.\"name\" ILIKE :ilike)" );
        }

//        if ( filterByValueType )
//        {
//            sql.append( " AND (t.valuetype IN (:valueTypes))" );
//        }

        sql.append( " ORDER BY t.\"name\"" );

        return sql.toString();
    }

    private String getDataElementQueryWith( final boolean filterByValueType, final boolean filterByIlikeName )
    {
        final StringBuilder sql = new StringBuilder(
            "SELECT de.\"name\" AS name, de.uid AS uid, de.valuetype AS valuetype"
                + " FROM dataelement de"
                + " WHERE ("
                + " (de.publicaccess LIKE '__r%' OR de.publicaccess LIKE 'r%' OR de.publicaccess IS NULL)"
                + " OR de.dataelementid IN (SELECT deua.dataelementid FROM dataelementuseraccesses deua WHERE deua.useraccessid"
                + " IN (SELECT useraccessid FROM useraccess WHERE access LIKE '__r%' AND useraccess.userid = :userId))"
                + " OR de.dataelementid IN (SELECT deuga.dataelementid FROM dataelementusergroupaccesses deuga WHERE deuga.usergroupaccessid"
                + " IN (SELECT usergroupaccessid FROM usergroupaccess WHERE access LIKE '__r%' AND usergroupid"
                + " IN (SELECT usergroupid FROM usergroupmembers WHERE userid = :userId)))"
                + ")" );

        if ( filterByIlikeName )
        {
            sql.append( "AND (de.\"name\" ILIKE :ilike)" );
        }

//        if ( filterByValueType )
//        {
//            sql.append( " AND (de.valuetype IN (:valueTypes))" );
//        }

        sql.append( " ORDER BY de.\"name\"" );

        return sql.toString();
    }
    
    private String getDateSetQuery( final boolean filterByIlikeName )
    {
        final StringBuilder sql = new StringBuilder(
            "SELECT ds.\"name\" AS name, ds.uid AS uid"
                + " FROM dataset ds WHERE"
                + "("
                + " (ds.publicaccess LIKE '__r%' OR ds.publicaccess LIKE 'r%' OR ds.publicaccess IS NULL)"
                + " OR ds.datasetid IN (SELECT dsua.datasetid FROM datasetuseraccesses dsua WHERE dsua.useraccessid"
                + " IN (SELECT useraccessid FROM useraccess WHERE access LIKE '__r%' AND useraccess.userid = :userId))"
                + " OR ds.datasetid IN (SELECT dsuga.datasetid FROM datasetusergroupaccesses dsuga WHERE dsuga.usergroupaccessid"
                + " IN (SELECT usergroupaccessid FROM usergroupaccess WHERE access LIKE '__r%' AND usergroupid"
                + " IN (SELECT usergroupid FROM usergroupmembers WHERE userid = :userId)))"
                + ")" );

        if ( filterByIlikeName )
        {
            sql.append( "AND (ds.\"name\" ILIKE :ilike)" );
        }

        sql.append( " ORDER BY ds.\"name\"" );

        return sql.toString();
    }

    private String getProgramIndicatorQuery( final boolean filterByIlikeName )
    {
        final StringBuilder sql = new StringBuilder(
            "SELECT pi.\"name\" AS name, pi.uid AS uid"
                + " FROM programindicator pi WHERE"
                + "("
                + " (pi.publicaccess LIKE '__r%' OR pi.publicaccess LIKE 'r%' OR pi.publicaccess IS NULL)"
                + " OR pi.programindicatorid IN (SELECT piua.programindicatorid FROM programindicatoruseraccesses piua"
                + " WHERE piua.useraccessid IN (SELECT useraccessid FROM useraccess WHERE access LIKE '__r%' AND useraccess.userid = :userId))"
                + " OR pi.programindicatorid IN (SELECT piuga.programindicatorid FROM programindicatorusergroupaccesses piuga"
                + " WHERE piuga.usergroupaccessid IN (SELECT usergroupaccessid FROM usergroupaccess WHERE access LIKE '__r%' AND usergroupid"
                + " IN (SELECT usergroupid FROM usergroupmembers WHERE userid = :userId)))"
                + ")" );

        if ( filterByIlikeName )
        {
            sql.append( "AND (pi.\"name\" ILIKE :ilike)" );
        }

        sql.append( " ORDER BY pi.\"name\"" );

        return sql.toString();
    }

    private String getIndicatorQuery( final boolean filterByIlikeName )
    {
        final StringBuilder sql = new StringBuilder(
            "SELECT i.\"name\" AS name, i.uid AS uid"
                + " FROM indicator i WHERE"
                + "("
                + " (i.publicaccess LIKE '__r%' OR i.publicaccess LIKE 'r%' OR i.publicaccess IS NULL)"
                + " OR i.indicatorid IN (SELECT iua.indicatorid FROM indicatoruseraccesses iua"
                + " WHERE iua.useraccessid IN (SELECT useraccessid FROM useraccess WHERE access LIKE '__r%' AND useraccess.userid = :userId))"
                + " OR i.indicatorid IN (SELECT iuga.indicatorid FROM indicatorusergroupaccesses iuga"
                + " WHERE iuga.usergroupaccessid IN (SELECT usergroupaccessid FROM usergroupaccess WHERE access LIKE '__r%' AND usergroupid"
                + " IN (SELECT usergroupid FROM usergroupmembers WHERE userid = :userId)))"
                + ")" );

        if ( filterByIlikeName )
        {
            sql.append( "AND (i.\"name\" ILIKE :ilike)" );
        }

        sql.append( " ORDER BY i.\"name\"" );

        return sql.toString();
    }

    /**
     * This method will iterate through the list of target entities, and query each
     * one of them using the filters and params provided. The result list will bring
     * together the results of all target entities queried.
     * 
     * @param targetEntities the list of entities to be retrieved
     * @param orderParams request ordering params
     * @param filters request filters
     * @param options request options
     * @return the consolidated collection of entities found.
     */
    List<DataItemViewObject> retrieveDataItemEntities(
        final Set<Class<? extends BaseDimensionalItemObject>> targetEntities, final List<String> filters,
        final WebOptions options, final OrderParams orderParams )
    {
        List<DataItemViewObject> dataItemViewObjects = new ArrayList<>();

        final User user = currentUserService.getCurrentUser();

        if ( isNotEmpty( targetEntities ) )
        {
            // Retrieving all items for each entity type.
            for ( final Class<? extends BaseDimensionalItemObject> entity : targetEntities )
            {
                if ( !aclService.canRead( user, entity ) )
                {
                    continue;
                }

                if ( isEquals( entity, ProgramDataElementDimensionItem.class ) )
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

//                    if ( containsValueTypeFilter( filters ) )
//                    {
//                        paramsMap.addValue( "valueTypes", extractAllValueTypesFromFilters( filters ) );
//                        filterByValueType = true;
//                    }

                    final SqlRowSet rowSet = namedParameterJdbcTemplate.queryForRowSet(
                        getProgramDataElementQueryWith( filterByValueType, filterByIlikeName ), paramsMap );

                    while ( rowSet.next() )
                    {
                        final DataItemViewObject viewItem = new DataItemViewObject();
                        final ValueType valueType = ValueType.fromString( rowSet.getString( "valuetype" ) );

                        viewItem.setName( rowSet.getString( "program_name" ) + SPACE + rowSet.getString( "name" ) );
                        viewItem.setValueType( valueType );
                        viewItem.setCombinedId( rowSet.getString( "program_uid" ) + "." + rowSet.getString( "uid" ) );
                        viewItem.setProgramId( rowSet.getString( "program_uid" ) );
                        viewItem.setUid( rowSet.getString( "uid" ) );
                        viewItem.setDimensionItemType( PROGRAM_DATA_ELEMENT );

                        dataItemViewObjects.add( viewItem );
                    }
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
                    
//                    if ( containsValueTypeFilter( filters ) )
//                    {
//                        paramsMap.addValue( "valueTypes", extractAllValueTypesFromFilters( filters ) );
//                        filterByValueType = true;
//                    }

                    final SqlRowSet rowSet = namedParameterJdbcTemplate.queryForRowSet(
                        getProgramAttributeQueryWith( filterByValueType, filterByIlikeName ), paramsMap );

                    while ( rowSet.next() )
                    {
                        final DataItemViewObject viewItem = new DataItemViewObject();
                        final ValueType valueType = ValueType.fromString( rowSet.getString( "valuetype" ) );

                        viewItem.setName( rowSet.getString( "program_name" ) + SPACE + rowSet.getString( "name" ) );
                        viewItem.setValueType( valueType );
                        viewItem.setCombinedId( rowSet.getString( "program_uid" ) + "." + rowSet.getString( "uid" ) );
                        viewItem.setProgramId( rowSet.getString( "program_uid" ) );
                        viewItem.setUid( rowSet.getString( "uid" ) );
                        viewItem.setDimensionItemType( PROGRAM_ATTRIBUTE );

                        dataItemViewObjects.add( viewItem );
                    }
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

                    final SqlRowSet rowSet = namedParameterJdbcTemplate.queryForRowSet(
                        getProgramIndicatorQuery( filterByIlikeName ), paramsMap );

                    while ( rowSet.next() )
                    {
                        final DataItemViewObject viewItem = new DataItemViewObject();

                        viewItem.setName( rowSet.getString( "name" ) );
                        viewItem.setUid( rowSet.getString( "uid" ) );
                        viewItem.setDimensionItemType( PROGRAM_INDICATOR );

                        dataItemViewObjects.add( viewItem );
                    }
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

                    final SqlRowSet rowSet = namedParameterJdbcTemplate
                        .queryForRowSet( getDateSetQuery( filterByIlikeName ), paramsMap );

                    while ( rowSet.next() )
                    {
                        final DataItemViewObject viewItem = new DataItemViewObject();

                        viewItem.setName( rowSet.getString( "name" ) );
                        viewItem.setUid( rowSet.getString( "uid" ) );
                        viewItem.setDimensionItemType( REPORTING_RATE );

                        // Setting the dimension type to REPORTING_RATE, for all DataSet object.
                        for ( final String metric : METRICS )
                        {
                            viewItem.getReportMetrics().add( viewItem.getDisplayFormName() + " (" + metric + ")" );
                        }

                        dataItemViewObjects.add( viewItem );
                    }
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

                    final SqlRowSet rowSet = namedParameterJdbcTemplate
                        .queryForRowSet( getIndicatorQuery( filterByIlikeName ), paramsMap );
                    
                    while ( rowSet.next() )
                    {
                        final DataItemViewObject viewItem = new DataItemViewObject();

                        viewItem.setName( rowSet.getString( "name" ) );
                        viewItem.setUid( rowSet.getString( "uid" ) );
                        viewItem.setDimensionItemType( INDICATOR );

                        dataItemViewObjects.add( viewItem );
                    }
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

//                    if ( containsValueTypeFilter( filters ) )
//                    {
//                        paramsMap.addValue( "valueTypes", extractAllValueTypesFromFilters( filters ) );
//                        filterByValueType = true;
//                    }

                    final SqlRowSet rowSet = namedParameterJdbcTemplate.queryForRowSet(
                        getDataElementQueryWith( filterByValueType, filterByIlikeName ), paramsMap );

                    while ( rowSet.next() )
                    {
                        final DataItemViewObject viewItem = new DataItemViewObject();
                        final ValueType valueType = ValueType.fromString( rowSet.getString( "valuetype" ) );

                        viewItem.setName( rowSet.getString( "name" ) );
                        viewItem.setValueType( valueType );
                        viewItem.setUid( rowSet.getString( "uid" ) );
                        viewItem.setDimensionItemType( DATA_ELEMENT );

                        dataItemViewObjects.add( viewItem );
                    }
                }
            }

            // In memory sorting
            sort( dataItemViewObjects, orderParams );

            // In memory pagination.
            dataItemViewObjects = slice( options, dataItemViewObjects );
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

     /**
     * This method returns a set of BaseDimensionalItemObject's based on the
     * provided filters. It will also remove, from the filters, the objects found.
     *
     * @param filters
     * @return the data items classes to be queried
     */
    Set<Class<? extends BaseDimensionalItemObject>> extractTargetEntities( final List<String> filters )
    {
        final Set<Class<? extends BaseDimensionalItemObject>> targetedEntities = new HashSet<>( 0 );

        if ( containsDimensionTypeFilter( filters ) )
        {
            final Iterator<String> iterator = filters.iterator();

            while ( iterator.hasNext() )
            {
                final String filter = iterator.next();
                final Class<? extends BaseDimensionalItemObject> entity = extractEntityFromEqualFilter( filter );
                final Set<Class<? extends BaseDimensionalItemObject>> entities = extractEntitiesFromInFilter( filter );

                if ( entity != null || isNotEmpty( entities ) )
                {
                    if ( entity != null )
                    {
                        targetedEntities.add( entity );
                    }

                    if ( isNotEmpty( entities ) )
                    {
                        targetedEntities.addAll( entities );
                    }

                    iterator.remove();
                }
            }
        }
        else
        {
            // If no filter is set we search for all entities.
            targetedEntities.addAll( DATA_TYPE_ENTITY_MAP.values() );
        }

        return targetedEntities;
    }

    /**
     * This method will build a Query object based on the provided arguments.
     * 
     * @param entity the BaseDimensionalItemObject class to be queried.
     * @param filters request filters
     * @param options request options
     * @return the built query
     * @throws org.hisp.dhis.query.QueryParserException if errors occur during the
     *         query creation
     */
    private Query buildQueryForEntity( final Class<? extends BaseDimensionalItemObject> entity,
        final List<String> filters, final WebOptions options )
    {
        final int maxLimit = options.getPage() * options.getPageSize();
        final Pagination pagination = options.hasPaging()
            ? new Pagination( PAGINATION_FIRST_RESULT, maxLimit )
            : NO_PAGINATION;
        
        final Query query = queryService.getQueryFromUrl( entity, removePostFilters( filters ), emptyList(),
            pagination, options.getRootJunction() );
        query.setDefaultOrder();

        return query;
    }
}
