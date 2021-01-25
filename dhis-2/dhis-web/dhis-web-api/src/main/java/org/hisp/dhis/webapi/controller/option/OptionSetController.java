package org.hisp.dhis.webapi.controller.option;



import org.hisp.dhis.dxf2.webmessage.WebMessageException;
import org.hisp.dhis.dxf2.webmessage.WebMessageUtils;
import org.hisp.dhis.node.types.RootNode;
import org.hisp.dhis.option.OptionService;
import org.hisp.dhis.option.OptionSet;
import org.hisp.dhis.schema.descriptors.OptionSetSchemaDescriptor;
import org.hisp.dhis.webapi.controller.AbstractCrudController;
import org.hisp.dhis.webapi.controller.metadata.MetadataExportControllerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = OptionSetSchemaDescriptor.API_ENDPOINT )
public class OptionSetController
    extends AbstractCrudController<OptionSet>
{
    @Autowired
    private OptionService optionService;

    @RequestMapping( value = "/{uid}/metadata", method = RequestMethod.GET )
    public ResponseEntity<RootNode> getOptionSetWithDependencies( @PathVariable( "uid" ) String pvUid, HttpServletResponse response, @RequestParam( required = false, defaultValue = "false" ) boolean download ) throws WebMessageException
    {
        OptionSet optionSet = optionService.getOptionSet( pvUid );

        if ( optionSet == null )
        {
            throw new WebMessageException( WebMessageUtils.notFound( "OptionSet not found for uid: " + pvUid ) );
        }

        return MetadataExportControllerUtils.getWithDependencies( contextService, exportService, optionSet, download );
    }
}
