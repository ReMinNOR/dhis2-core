package org.hisp.dhis.commons.action;



import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.attribute.Attribute;
import org.hisp.dhis.attribute.AttributeService;
import org.hisp.dhis.util.ContextUtils;

import com.opensymphony.xwork2.Action;

/**
 * @author mortenoh
 */
public class GetAttributesAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private AttributeService attributeService;

    public void setAttributeService( AttributeService attributeService )
    {
        this.attributeService = attributeService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private List<Attribute> attributes;

    public List<Attribute> getAttributes()
    {
        return attributes;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
    {
        attributes = new ArrayList<>( attributeService.getAllAttributes() );
        
        ContextUtils.clearIfNotModified( ServletActionContext.getRequest(), ServletActionContext.getResponse(), attributes );
        
        Collections.sort( attributes );

        return SUCCESS;
    }
}
