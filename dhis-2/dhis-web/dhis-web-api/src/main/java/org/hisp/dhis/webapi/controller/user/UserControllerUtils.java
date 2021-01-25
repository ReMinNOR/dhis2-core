package org.hisp.dhis.webapi.controller.user;



import org.hisp.dhis.dataapproval.DataApprovalLevel;
import org.hisp.dhis.dataapproval.DataApprovalLevelService;
import org.hisp.dhis.dataapproval.DataApprovalService;
import org.hisp.dhis.dataapproval.DataApprovalWorkflow;
import org.hisp.dhis.node.NodeUtils;
import org.hisp.dhis.node.types.CollectionNode;
import org.hisp.dhis.node.types.ComplexNode;
import org.hisp.dhis.node.types.RootNode;
import org.hisp.dhis.node.types.SimpleNode;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.security.acl.AclService;
import org.hisp.dhis.setting.SettingKey;
import org.hisp.dhis.setting.SystemSettingManager;
import org.hisp.dhis.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Set;

import static org.hisp.dhis.dataapproval.DataApproval.*;
import static org.hisp.dhis.user.UserAuthorityGroup.AUTHORITY_ALL;

/**
 * @author Jim Grace
 */
@Component
public class UserControllerUtils
{
    @Autowired
    private DataApprovalService dataApprovalService;

    @Autowired
    private DataApprovalLevelService dataApprovalLevelService;

    @Autowired
    private AclService aclService;

    @Autowired
    private SystemSettingManager systemSettingManager;

    /**
     * Gets the data approval workflows a user can see, including the workflow
     * levels accessible to the user and the actions (if any) they can take at
     * those levels to approve (and accept if configured) data.
     *
     * @param user the user
     * @throws Exception if an error occurs
     */
    public RootNode getUserDataApprovalWorkflows( User user )
        throws Exception
    {
        CollectionNode collectionNode = new CollectionNode( "dataApprovalWorkflows", true );

        for ( DataApprovalWorkflow workflow : dataApprovalService.getAllWorkflows() )
        {
            if ( !aclService.canRead( user, workflow ) )
            {
                continue;
            }

            ComplexNode workflowNode = new ComplexNode( "dataApprovalWorkflow" );

            workflowNode.addChild( new SimpleNode( "name", workflow.getName() ) );
            workflowNode.addChild( new SimpleNode( "id", workflow.getUid() ) );
            workflowNode.addChild( getWorkflowLevelNodes( user, workflow ) );

            collectionNode.addChild( workflowNode );
        }

        collectionNode.getUnorderedChildren().sort( Comparator.comparing( c ->
            (String) ( (SimpleNode) c.getUnorderedChildren().get( 0 ) ).getValue() ) );

        RootNode rootNode = NodeUtils.createRootNode( "dataApprovalWorkflows" );
        rootNode.addChild( collectionNode );

        return rootNode;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * For a user and workflow, returns a list of levels accessible to the user
     * user and the actions (if any) they can take at those levels to approve
     * (and accept if configured) data.
     *
     * @param user the user
     * @param workflow the approval workflow for which to fetch the levels
     * @return a node with the ordered list of data approval levels
     */
    private CollectionNode getWorkflowLevelNodes( User user, DataApprovalWorkflow workflow )
    {
        Set<String> authorities = user.getUserCredentials().getAllAuthorities();

        boolean canApprove = authorities.contains( AUTHORITY_ALL ) || authorities.contains( AUTH_APPROVE );
        boolean canApproveLowerLevels = authorities.contains( AUTHORITY_ALL ) || authorities.contains( AUTH_APPROVE_LOWER_LEVELS );
        boolean canAccept = authorities.contains( AUTHORITY_ALL ) || authorities.contains( AUTH_ACCEPT_LOWER_LEVELS );

        boolean acceptConfigured = (Boolean) systemSettingManager.getSystemSetting( SettingKey.ACCEPTANCE_REQUIRED_FOR_APPROVAL );

        int lowestUserOrgUnitLevel = getLowsetUserOrgUnitLevel( user );

        CollectionNode levelNodes = new CollectionNode( "dataApprovalLevels", true );

        boolean highestLevelInWorkflow = true;

        for ( DataApprovalLevel level : dataApprovalLevelService.getUserDataApprovalLevels( user, workflow ) )
        {
            if ( level.getOrgUnitLevel() < lowestUserOrgUnitLevel )
            {
                continue;
            }

            ComplexNode levelNode = new ComplexNode( "dataApprovalLevel" );

            levelNode.addChild( new SimpleNode( "name", level.getName() ) );
            levelNode.addChild( new SimpleNode( "id", level.getUid() ) );
            levelNode.addChild( new SimpleNode( "level", level.getLevel() ) );
            levelNode.addChild( new SimpleNode( "approve", ( canApprove && highestLevelInWorkflow ) || canApproveLowerLevels ) );

            if ( acceptConfigured )
            {
                levelNode.addChild( new SimpleNode( "accept", canAccept && !highestLevelInWorkflow ) );
            }

            levelNodes.addChild( levelNode );

            highestLevelInWorkflow = false;
        }

        return levelNodes;
    }

    private int getLowsetUserOrgUnitLevel( User user )
    {
        Set<OrganisationUnit> userOrgUnits = user.getOrganisationUnits();

        return userOrgUnits.isEmpty() ? 9999 :
            userOrgUnits.stream().map( OrganisationUnit::getHierarchyLevel ).min( Integer::compare ).get();
    }
}
