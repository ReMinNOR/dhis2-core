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

import static java.util.Collections.emptyList;
import static org.springframework.util.Assert.notNull;

import java.util.List;

import org.hisp.dhis.common.BaseDimensionalItemObject;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataitem.DataItem;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.program.ProgramDataElementDimensionItem;
import org.hisp.dhis.program.ProgramIndicator;
import org.hisp.dhis.program.ProgramTrackedEntityAttributeDimensionItem;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class QueryExecutor
{
    private final ProgramDataElementDimensionQuery programDataElementDimensionQuery;

    private final ProgramAttributeQuery programAttributeQuery;

    private final ProgramIndicatorQuery programIndicatorQuery;

    private final DataSetQuery dataSetQuery;

    private final IndicatorQuery indicatorQuery;

    private final DataElementQuery dataElementQuery;

    // TODO: MAIKEL: Check if there is a way to avoid this if/elses.
    public List<DataItem> find( final Class<? extends BaseDimensionalItemObject> entity,
        final MapSqlParameterSource paramsMap )
    {
        if ( isEquals( entity, ProgramDataElementDimensionItem.class ) )
        {
            return programDataElementDimensionQuery.find( paramsMap );
        }
        else if ( isEquals( entity, ProgramTrackedEntityAttributeDimensionItem.class ) )
        {
            return programAttributeQuery.find( paramsMap );
        }
        else if ( isEquals( entity, ProgramIndicator.class ) )
        {
            return programIndicatorQuery.find( paramsMap );
        }
        else if ( isEquals( entity, DataSet.class ) )
        {
            return dataSetQuery.find( paramsMap );
        }
        else if ( isEquals( entity, Indicator.class ) )
        {
            return indicatorQuery.find( paramsMap );
        }
        else if ( isEquals( entity, DataElement.class ) )
        {
            return dataElementQuery.find( paramsMap );
        }

        return emptyList();
    }

    // TODO: MAIKEL: Check if there is a way to avoid this if/elses.
    public int count( final Class<? extends BaseDimensionalItemObject> entity,
        final MapSqlParameterSource paramsMap )
    {
        if ( isEquals( entity, ProgramDataElementDimensionItem.class ) )
        {
            return programDataElementDimensionQuery.count( paramsMap );
        }
        else if ( isEquals( entity, ProgramTrackedEntityAttributeDimensionItem.class ) )
        {
            return programAttributeQuery.count( paramsMap );
        }
        else if ( isEquals( entity, ProgramIndicator.class ) )
        {
            return programIndicatorQuery.count( paramsMap );
        }
        else if ( isEquals( entity, DataSet.class ) )
        {
            return dataSetQuery.count( paramsMap );
        }
        else if ( isEquals( entity, Indicator.class ) )
        {
            return indicatorQuery.count( paramsMap );
        }
        else if ( isEquals( entity, DataElement.class ) )
        {
            return dataElementQuery.count( paramsMap );
        }

        return 0;
    }

    private boolean isEquals( final Class<? extends BaseDimensionalItemObject> entity,
        final Class<? extends BaseDimensionalItemObject> other )
    {
        notNull( entity, "The entity must not be null" );
        notNull( entity, "The other must not be null" );

        return entity.getSimpleName().equals( other.getSimpleName() );
    }
}
