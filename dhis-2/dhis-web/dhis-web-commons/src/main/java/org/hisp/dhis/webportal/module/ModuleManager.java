package org.hisp.dhis.webportal.module;



import java.util.Collection;
import java.util.List;

/**
 * @author Torgeir Lorange Ostby
 * @version $Id: ModuleManager.java 4883 2008-04-12 13:12:54Z larshelg $
 */
public interface ModuleManager
{
    Module getModuleByName( String name );

    Module getModuleByNamespace( String namespace );

    boolean moduleExists( String name );
    
    List<Module> getMenuModules();
    
    List<Module> getAccessibleMenuModules();
    
    List<Module> getAccessibleMenuModulesAndApps( String contextPath );
    
    Collection<Module> getAllModules();

    Module getCurrentModule();

    void setCurrentModule( Module module );
}
