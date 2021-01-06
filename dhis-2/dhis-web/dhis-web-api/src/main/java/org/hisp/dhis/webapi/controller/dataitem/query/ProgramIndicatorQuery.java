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
import static org.hisp.dhis.common.DimensionItemType.PROGRAM_INDICATOR;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.webapi.controller.dataitem.DataItemViewObject;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

@Component
public class ProgramIndicatorQuery implements DataItemQuery
{
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public ProgramIndicatorQuery( @Qualifier( "readOnlyJdbcTemplate" )
    final JdbcTemplate jdbcTemplate )
    {
        checkNotNull( jdbcTemplate );

        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate( jdbcTemplate );
    }

    private String getProgramIndicatorQuery( final MapSqlParameterSource paramsMap )
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

        if ( paramsMap.hasValue( "ilikeName" ) )
        {
            sql.append( "AND (pi.\"name\" ILIKE :ilikeName)" );
        }

        sql.append( " ORDER BY pi.\"name\"" );

        return sql.toString();
    }

    public List<DataItemViewObject> find( final MapSqlParameterSource paramsMap )
    {
        final List<DataItemViewObject> dataItemViewObjects = new ArrayList<>();

        final SqlRowSet rowSet = namedParameterJdbcTemplate.queryForRowSet(
            getProgramIndicatorQuery( paramsMap ), paramsMap );

        while ( rowSet.next() )
        {
            final DataItemViewObject viewItem = new DataItemViewObject();

            viewItem.setName( rowSet.getString( "name" ) );
            viewItem.setUid( rowSet.getString( "uid" ) );
            viewItem.setDimensionItemType( PROGRAM_INDICATOR );

            dataItemViewObjects.add( viewItem );
        }

        return dataItemViewObjects;
    }
}
