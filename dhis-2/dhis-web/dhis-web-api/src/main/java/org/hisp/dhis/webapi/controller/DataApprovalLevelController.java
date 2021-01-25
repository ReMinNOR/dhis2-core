package org.hisp.dhis.webapi.controller;



import org.hisp.dhis.dataapproval.DataApprovalLevel;
import org.hisp.dhis.dataapproval.DataApprovalLevelService;
import org.hisp.dhis.schema.descriptors.DataApprovalLevelSchemaDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping( value = DataApprovalLevelSchemaDescriptor.API_ENDPOINT )
public class DataApprovalLevelController
    extends AbstractCrudController<DataApprovalLevel>
{
    @Autowired
    private DataApprovalLevelService dataApprovalLevelService;

    @Override
    protected void preCreateEntity( DataApprovalLevel entity )
    {
        dataApprovalLevelService.prepareAddDataApproval( entity );
    }

    @Override
    protected void postDeleteEntity()
    {
        dataApprovalLevelService.postDeleteDataApprovalLevel();
    }
}
