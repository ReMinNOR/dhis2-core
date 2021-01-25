package org.hisp.dhis.webapi.controller.user;



import org.hisp.dhis.schema.descriptors.UserGroupSchemaDescriptor;
import org.hisp.dhis.user.UserGroup;
import org.hisp.dhis.webapi.controller.AbstractCrudController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = UserGroupSchemaDescriptor.API_ENDPOINT )
public class UserGroupController
    extends AbstractCrudController<UserGroup>
{
    @Override
    protected void postUpdateEntity( UserGroup entity )
    {
        hibernateCacheManager.clearCache();
    }
}
