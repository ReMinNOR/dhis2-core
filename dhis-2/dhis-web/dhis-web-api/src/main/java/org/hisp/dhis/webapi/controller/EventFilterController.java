package org.hisp.dhis.webapi.controller;


import java.util.List;

import org.hisp.dhis.common.DhisApiVersion;
import org.hisp.dhis.common.IllegalQueryException;
import org.hisp.dhis.programstagefilter.ProgramStageInstanceFilter;
import org.hisp.dhis.programstagefilter.ProgramStageInstanceFilterService;
import org.hisp.dhis.schema.descriptors.ProgramStageInstanceFilterSchemaDescriptor;
import org.hisp.dhis.webapi.mvc.annotation.ApiVersion;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Ameen Mohamed <ameen@dhis2.org>
 */
@RestController
@RequestMapping( value = ProgramStageInstanceFilterSchemaDescriptor.API_ENDPOINT )
@ApiVersion( include = { DhisApiVersion.ALL, DhisApiVersion.DEFAULT } )
public class EventFilterController extends AbstractCrudController<ProgramStageInstanceFilter>
{
    private final ProgramStageInstanceFilterService psiFilterService;

    public EventFilterController( ProgramStageInstanceFilterService psiFilterService )
    {
        this.psiFilterService = psiFilterService;
    }

    @Override
    public void preCreateEntity( ProgramStageInstanceFilter eventFilter )
    {
        List<String> errors = psiFilterService.validate( eventFilter );
        if ( !errors.isEmpty() )
        {
            throw new IllegalQueryException( errors.toString() );
        }
    }

    @Override
    public void preUpdateEntity( ProgramStageInstanceFilter oldEventFilter , ProgramStageInstanceFilter newEventFilter )
    {
        List<String> errors = psiFilterService.validate( newEventFilter );
        if ( !errors.isEmpty() )
        {
            throw new IllegalQueryException( errors.toString() );
        }
    }
}
