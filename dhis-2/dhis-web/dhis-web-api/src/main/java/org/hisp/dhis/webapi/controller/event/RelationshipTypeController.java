package org.hisp.dhis.webapi.controller.event;



import org.hisp.dhis.relationship.RelationshipType;
import org.hisp.dhis.schema.descriptors.RelationshipTypeSchemaDescriptor;
import org.hisp.dhis.webapi.controller.AbstractCrudController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = RelationshipTypeSchemaDescriptor.API_ENDPOINT )
public class RelationshipTypeController
    extends AbstractCrudController<RelationshipType>
{
}
