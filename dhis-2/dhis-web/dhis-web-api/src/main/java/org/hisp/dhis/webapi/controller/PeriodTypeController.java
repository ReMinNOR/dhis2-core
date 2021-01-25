package org.hisp.dhis.webapi.controller;



import java.util.List;
import java.util.stream.Collectors;

import org.hisp.dhis.common.DhisApiVersion;
import org.hisp.dhis.fieldfilter.FieldFilterParams;
import org.hisp.dhis.fieldfilter.FieldFilterService;
import org.hisp.dhis.node.NodeUtils;
import org.hisp.dhis.node.Preset;
import org.hisp.dhis.node.types.RootNode;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.RelativePeriodEnum;
import org.hisp.dhis.webapi.mvc.annotation.ApiVersion;
import org.hisp.dhis.webapi.service.ContextService;
import org.hisp.dhis.webapi.webdomain.PeriodTypeDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@RestController
@ApiVersion( { DhisApiVersion.DEFAULT, DhisApiVersion.ALL } )
@RequestMapping( value = "/periodTypes" )
public class PeriodTypeController
{
    private final PeriodService periodService;
    private final ContextService contextService;
    private final FieldFilterService fieldFilterService;

    public PeriodTypeController( PeriodService periodService, ContextService contextService, FieldFilterService fieldFilterService )
    {
        this.periodService = periodService;
        this.contextService = contextService;
        this.fieldFilterService = fieldFilterService;
    }

    @GetMapping
    public RootNode getPeriodTypes()
    {
        List<String> fields = Lists.newArrayList( contextService.getParameterValues( "fields" ) );
        List<PeriodTypeDto> periodTypes = periodService.getAllPeriodTypes().stream()
            .map( PeriodTypeDto::new )
            .collect( Collectors.toList() );

        if ( fields.isEmpty() )
        {
            fields.addAll( Preset.ALL.getFields() );
        }

        RootNode rootNode = NodeUtils.createMetadata();
        rootNode.addChild( fieldFilterService.toCollectionNode( PeriodTypeDto.class, new FieldFilterParams( periodTypes, fields ) ) );

        return rootNode;
    }

    @RequestMapping( value = "/relativePeriodTypes", method = RequestMethod.GET, produces = { "application/json", "application/javascript" } )
    public @ResponseBody RelativePeriodEnum[] getRelativePeriodTypes()
    {
        return RelativePeriodEnum.values();
    }
}
