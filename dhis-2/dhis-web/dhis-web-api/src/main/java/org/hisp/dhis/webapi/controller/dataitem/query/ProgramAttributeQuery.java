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

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.SPACE;
import static org.hisp.dhis.common.DimensionItemType.PROGRAM_ATTRIBUTE;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.common.ValueType;
import org.hisp.dhis.webapi.controller.dataitem.DataItemViewObject;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

@Component
public class ProgramAttributeQuery implements DataItemQuery
{
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public ProgramAttributeQuery( @Qualifier( "readOnlyJdbcTemplate" )
    final JdbcTemplate jdbcTemplate )
    {
        checkNotNull( jdbcTemplate );

        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate( jdbcTemplate );
    }

    private String getProgramAttributeQueryWith( final MapSqlParameterSource paramsMap )
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

        if ( paramsMap.hasValue( "ilikeName" ) )
        {
            sql.append( "AND (p.\"name\" ILIKE :ilikeName OR t.\"name\" ILIKE :ilikeName)" );
        }

        // if ( filterByValueType )
        // {
        // sql.append( " AND (t.valuetype IN (:valueTypes))" );
        // }

        sql.append( " ORDER BY t.\"name\"" );

        return sql.toString();
    }

    public List<DataItemViewObject> find( final MapSqlParameterSource paramsMap )
    {
        final List<DataItemViewObject> dataItemViewObjects = new ArrayList<>();

        final SqlRowSet rowSet = namedParameterJdbcTemplate.queryForRowSet(
            getProgramAttributeQueryWith( paramsMap ), paramsMap );

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

        return dataItemViewObjects;
    }
}
