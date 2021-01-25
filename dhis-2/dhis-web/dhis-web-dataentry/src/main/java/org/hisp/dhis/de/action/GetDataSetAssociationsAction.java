package org.hisp.dhis.de.action;



import static org.hisp.dhis.commons.util.TextUtils.SEP;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitDataSetAssociationSet;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.util.DateUtils;
import org.hisp.dhis.webapi.utils.ContextUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;
/**
 * @author Lars Helge Overland
 */
public class GetDataSetAssociationsAction
    implements Action
{
    @Autowired
    private OrganisationUnitService organisationUnitService;
    
    @Autowired
    private IdentifiableObjectManager identifiableObjectManager;

    @Autowired
    private CurrentUserService currentUserService;
    
    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private List<Set<String>> dataSetAssociationSets = new ArrayList<>();

    public List<Set<String>> getDataSetAssociationSets()
    {
        return dataSetAssociationSets;
    }

    private Map<String, Integer> organisationUnitAssociationSetMap = new HashMap<>();

    public Map<String, Integer> getOrganisationUnitAssociationSetMap()
    {
        return organisationUnitAssociationSetMap;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
    {
        User user = currentUserService.getCurrentUser();

        Integer level = organisationUnitService.getOfflineOrganisationUnitLevels();

        Date lastUpdated = DateUtils.max( 
            identifiableObjectManager.getLastUpdated( DataSet.class ), 
            identifiableObjectManager.getLastUpdated( OrganisationUnit.class ) );
        String tag = lastUpdated != null && user != null ? ( DateUtils.getLongDateString( lastUpdated ) + SEP + level + SEP + user.getUid() ): null;
        
        if ( ContextUtils.isNotModified( ServletActionContext.getRequest(), ServletActionContext.getResponse(), tag ) )
        {
            return SUCCESS;
        }
        
        OrganisationUnitDataSetAssociationSet organisationUnitSet = organisationUnitService.getOrganisationUnitDataSetAssociationSet( level );

        dataSetAssociationSets = organisationUnitSet.getDataSetAssociationSets();

        organisationUnitAssociationSetMap = organisationUnitSet.getOrganisationUnitAssociationSetMap();

        return SUCCESS;
    }
}
