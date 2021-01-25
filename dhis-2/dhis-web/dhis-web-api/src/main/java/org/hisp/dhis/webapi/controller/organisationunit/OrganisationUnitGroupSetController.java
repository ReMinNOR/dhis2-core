package org.hisp.dhis.webapi.controller.organisationunit;



import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.schema.descriptors.OrganisationUnitGroupSetSchemaDescriptor;
import org.hisp.dhis.webapi.controller.AbstractCrudController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = OrganisationUnitGroupSetSchemaDescriptor.API_ENDPOINT )
public class OrganisationUnitGroupSetController
    extends AbstractCrudController<OrganisationUnitGroupSet>
{
}
