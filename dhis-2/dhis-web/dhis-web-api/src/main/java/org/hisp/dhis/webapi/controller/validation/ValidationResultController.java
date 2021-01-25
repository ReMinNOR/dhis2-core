package org.hisp.dhis.webapi.controller.validation;


import static org.hisp.dhis.webapi.utils.ContextUtils.setNoStore;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.common.DhisApiVersion;
import org.hisp.dhis.dxf2.webmessage.WebMessageException;
import org.hisp.dhis.dxf2.webmessage.WebMessageUtils;
import org.hisp.dhis.fieldfilter.FieldFilterParams;
import org.hisp.dhis.fieldfilter.FieldFilterService;
import org.hisp.dhis.node.NodeUtils;
import org.hisp.dhis.node.Preset;
import org.hisp.dhis.node.types.RootNode;
import org.hisp.dhis.schema.descriptors.ValidationResultSchemaDescriptor;
import org.hisp.dhis.validation.ValidationResult;
import org.hisp.dhis.validation.ValidationResultService;
import org.hisp.dhis.validation.ValidationResultsDeletionRequest;
import org.hisp.dhis.validation.comparator.ValidationResultQuery;
import org.hisp.dhis.webapi.mvc.annotation.ApiVersion;
import org.hisp.dhis.webapi.service.ContextService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;

/**
 * @author Stian Sandvold
 */
@RestController
@RequestMapping( value = ValidationResultSchemaDescriptor.API_ENDPOINT )
@ApiVersion( { DhisApiVersion.ALL, DhisApiVersion.DEFAULT } )
public class ValidationResultController
{
    private final FieldFilterService fieldFilterService;

    private final ValidationResultService validationResultService;

    private final ContextService contextService;

    public ValidationResultController( FieldFilterService fieldFilterService,
        ValidationResultService validationResultService,
        ContextService contextService )
    {
        this.fieldFilterService = fieldFilterService;
        this.validationResultService = validationResultService;
        this.contextService = contextService;
    }

    @GetMapping
    public
    @ResponseBody
    RootNode getObjectList( ValidationResultQuery query, HttpServletResponse response )
    {
        List<String> fields = Lists.newArrayList( contextService.getParameterValues( "fields" ) );

        if ( fields.isEmpty() )
        {
            fields.addAll( Preset.ALL.getFields() );
        }

        List<ValidationResult> validationResults = validationResultService.getValidationResults( query );

        RootNode rootNode = NodeUtils.createMetadata();

        if ( !query.isSkipPaging() )
        {
            query.setTotal( validationResultService.countValidationResults( query ) );
            rootNode.addChild( NodeUtils.createPager( query.getPager() ) );
        }

        rootNode.addChild( fieldFilterService.toCollectionNode( ValidationResult.class, new FieldFilterParams( validationResults, fields ) ) );

        setNoStore( response );
        return rootNode;
    }

    @GetMapping( value = "/{id}" )
    public @ResponseBody ValidationResult getObject( @PathVariable int id )
        throws WebMessageException
    {
        ValidationResult result = validationResultService.getById( id );
        checkFound(id, result);
        return result;
    }

    @PreAuthorize( "hasRole('F_PERFORM_MAINTENANCE')" )
    @DeleteMapping( value = "/{id}" )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    public void delete( @PathVariable int id )
        throws WebMessageException
    {
        ValidationResult result = validationResultService.getById( id );
        checkFound(id, result);
        validationResultService.deleteValidationResult( result );
    }

    @PreAuthorize( "hasRole('F_PERFORM_MAINTENANCE')" )
    @DeleteMapping
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    public void deleteValidationResults( ValidationResultsDeletionRequest request )
    {
        validationResultService.deleteValidationResults( request );
    }

    private void checkFound( int id, ValidationResult result )
        throws WebMessageException
    {
        if ( result == null )
        {
            throw new WebMessageException(
                WebMessageUtils.notFound( "Validation result with id " + id + " was not found" ) );
        }
    }
}
