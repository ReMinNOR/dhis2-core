package org.hisp.dhis.webportal.module;



import java.util.Arrays;
import java.util.List;

import org.hisp.dhis.commons.filter.Filter;

/**
 * @author Lars Helge Overland
 */
public class StartableModuleFilter
    implements Filter<Module>
{
    private List<String> NOT_VIABLE = Arrays.asList( "dhis-web-mapping", "dhis-web-visualizer" );
    
    @Override
    public boolean retain( Module module )
    {
        return module != null && !NOT_VIABLE.contains( module.getName() );
    }
}
