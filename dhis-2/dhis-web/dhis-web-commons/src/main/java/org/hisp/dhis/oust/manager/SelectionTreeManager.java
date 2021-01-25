package org.hisp.dhis.oust.manager;



import java.util.Collection;

import org.hisp.dhis.organisationunit.OrganisationUnit;

/**
 * The selection tree is used for data output and analysis.
 * 
 * @author Torgeir Lorange Ostby
 */
public interface SelectionTreeManager
{
    String ID = SelectionTreeManager.class.getName();

    /**
     * Sets the roots of the selection tree by specifying the roots' parent. The
     * selected OrganisationUnits will be removed. The OrganisationUnit doesn't
     * have to be fetched within the current transaction.
     * 
     * @param units
     *            The root OrganisationUnit parent to set.
     * @throws IllegalArgumentException
     *             if the argument is null
     */
    void setRootOrganisationUnitsParent( OrganisationUnit unit );

    /**
     * Sets the root of the selection tree. The selected OrganisationUnits will
     * be removed. The OrganisationUnit doesn't have to be fetched within the
     * current transaction.
     * 
     * @param units
     *            The root OrganisationUnit to set.
     * @throws IllegalArgumentException
     *             if the argument is null
     */
    void setRootOrganisationUnits( Collection<OrganisationUnit> units );

    /**
     * Returns the root parent of the selection tree. The OrganisationUnit is
     * fetched within the current transaction.
     * 
     * @return the root OrganisationUnit parent
     */
    OrganisationUnit getRootOrganisationUnitsParent();

    /**
     * Returns the roots of the selection tree. The OrganisationUnits are
     * fetched within the current transaction.
     * 
     * @return the root OrganisationUnits
     */
    Collection<OrganisationUnit> getRootOrganisationUnits();
    
    /**
     * Resets the selection tree to use the actual root of the OrganisationUnit
     * tree.
     */
    void resetRootOrganisationUnits();

    /**
     * Sets the selected OrganisationUnits. The OrganisationUnits don't have to
     * be fetched within the current transaction.
     * 
     * @param units
     *            the selected OrganisationUnits to set
     * @throws IllegalArgumentException
     *             if the argument is null
     */
    void setSelectedOrganisationUnits( Collection<OrganisationUnit> units );

    /**
     * Returns the selected OrganisationUnits. The returned OrganisationUnits
     * are always in the subtree of the selected root. 
     * 
     * @return the selected OrganisationUnits or an empty collection if no unit
     *         is selected
     */
    Collection<OrganisationUnit> getSelectedOrganisationUnits();
    
    /**
     * Convenience method for getting one selected OrganisationUnit. If multiple
     * OrganisationUnits are selected, this method returns one of them.
     * 
     * @return a selected OrganisationUnit or null if no OrganisationUnit is
     *         selected
     */
    OrganisationUnit getSelectedOrganisationUnit();

    /**
     * Returns the selected OrganisationUnits. The returned OrganisationUnits
     * are always in the subtree of the selected root. The OrganisationUnits
     * are associated with the current session.
     * 
     * @return the selected OrganisationUnits or an empty collection if no unit
     *         is selected
     */
    Collection<OrganisationUnit> getReloadedSelectedOrganisationUnits();

    /**
     * Convenience method for getting one selected OrganisationUnit. If multiple
     * OrganisationUnits are selected, this method returns one of them. The 
     * OrganisationUnits are associated with the current session.
     * 
     * @return a selected OrganisationUnit or null if no OrganisationUnit is
     *         selected
     */
    OrganisationUnit getReloadedSelectedOrganisationUnit();
    
    /**
     * Clears the selection and makes getSelectedOrganisationUnit() return null.
     */
    void clearSelectedOrganisationUnits();

    /**
     * Convenience method for setting one selected OrganisationUnit.
     * 
     * @param unit
     *            the OrganisationUnit to set
     * @throws IllegalArgumentException
     *             if the argument is null
     */
    void setSelectedOrganisationUnit( OrganisationUnit unit );
}
