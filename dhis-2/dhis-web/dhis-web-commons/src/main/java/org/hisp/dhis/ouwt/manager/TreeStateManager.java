package org.hisp.dhis.ouwt.manager;



import java.util.Collection;

import org.hisp.dhis.organisationunit.OrganisationUnit;

/**
 * @author Torgeir Lorange Ostby
 * @version $Id: TreeStateManager.java 2869 2007-02-20 14:26:09Z andegje $
 */
public interface TreeStateManager
{
    String ID = TreeStateManager.class.getName();

    void setSubtreeExpanded( OrganisationUnit unit );

    Collection<OrganisationUnit> setSubtreeCollapsed( OrganisationUnit unit );

    boolean isSubtreeExpanded( OrganisationUnit unit );

    void clearTreeState();
}
