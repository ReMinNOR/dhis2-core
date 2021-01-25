package org.hisp.dhis.webapi.controller.organisationunit;



import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.schema.descriptors.OrganisationUnitLevelSchemaDescriptor;
import org.hisp.dhis.webapi.controller.AbstractCrudController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = OrganisationUnitLevelSchemaDescriptor.API_ENDPOINT )
public class OrganisationUnitLevelController
    extends AbstractCrudController<OrganisationUnitLevel>
{
}
