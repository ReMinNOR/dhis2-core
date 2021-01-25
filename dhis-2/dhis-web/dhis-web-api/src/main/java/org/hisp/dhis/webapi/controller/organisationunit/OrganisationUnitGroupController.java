package org.hisp.dhis.webapi.controller.organisationunit;



import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.schema.descriptors.OrganisationUnitGroupSchemaDescriptor;
import org.hisp.dhis.webapi.controller.AbstractCrudController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = OrganisationUnitGroupSchemaDescriptor.API_ENDPOINT )
public class OrganisationUnitGroupController
    extends AbstractCrudController<OrganisationUnitGroup>
{
}
