package org.hisp.dhis.webapi.controller;



import org.hisp.dhis.dataapproval.DataApprovalWorkflow;
import org.hisp.dhis.schema.descriptors.DataApprovalWorkflowSchemaDescriptor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping( value = DataApprovalWorkflowSchemaDescriptor.API_ENDPOINT )
public class DataApprovalWorkflowController
    extends AbstractCrudController<DataApprovalWorkflow>
{
}
