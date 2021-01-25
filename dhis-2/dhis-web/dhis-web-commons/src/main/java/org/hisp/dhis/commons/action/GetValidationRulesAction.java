package org.hisp.dhis.commons.action;



import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.util.ContextUtils;
import org.hisp.dhis.validation.ValidationRule;
import org.hisp.dhis.validation.ValidationRuleService;

import com.opensymphony.xwork2.Action;

/**
 * @author mortenoh
 */
public class GetValidationRulesAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ValidationRuleService validationRuleService;

    public void setValidationRuleService( ValidationRuleService validationRuleService )
    {
        this.validationRuleService = validationRuleService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private List<ValidationRule> validationRules;

    public List<ValidationRule> getValidationRules()
    {
        return this.validationRules;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
    {
        validationRules = new ArrayList<>( validationRuleService.getAllValidationRules() );

        ContextUtils.clearIfNotModified( ServletActionContext.getRequest(), ServletActionContext.getResponse(), validationRules );
        
        Collections.sort( validationRules );

        return SUCCESS;
    }
}
