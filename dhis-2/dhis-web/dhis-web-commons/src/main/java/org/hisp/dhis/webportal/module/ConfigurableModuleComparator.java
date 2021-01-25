package org.hisp.dhis.webportal.module;



import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Comparator for sorting modules according to a specified order. Modules not
 * listed in the given order are sorted alphabetically after the specified ones.
 * 
 * @author Torgeir Lorange Ostby
 * @version $Id: ConfigurableModuleComparator.java 2869 2007-02-20 14:26:09Z andegje $
 */
public class ConfigurableModuleComparator
    implements Comparator<Module>
{
    // -------------------------------------------------------------------------
    // Configuration
    // -------------------------------------------------------------------------

    private List<String> order = new ArrayList<>();

    public void setOrder( List<String> order )
    {
        this.order = order;
    }

    // -------------------------------------------------------------------------
    // Comparator
    // -------------------------------------------------------------------------

    @Override
    public int compare( Module moduleA, Module moduleB )
    {
        int indexA = order.indexOf( moduleA.getName() );
        int indexB = order.indexOf( moduleB.getName() );

        // ---------------------------------------------------------------------
        // If indexA and indexB have different signs, make the positive one come
        // first {if A is 0/+ and B is - return - (A before B), if A is - and B
        // is 0/+ return + (B before A)}. If both are -, compare the names. If
        // both are 0/+, compare the indices.
        // ---------------------------------------------------------------------

        if ( (indexA < 0) ^ (indexB < 0) )
        {
            return indexB * 2 + 1;
        }

        if ( indexA < 0 )
        {
            return moduleA.getName().compareTo( moduleB.getName() );
        }

        return indexA - indexB;
    }
}
