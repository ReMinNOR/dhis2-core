package org.hisp.dhis.webportal.module;



import org.hisp.dhis.appmanager.App;
import org.hisp.dhis.commons.util.TextUtils;

/**
 * @author Torgeir Lorange Ostby
 * @version $Id: Module.java 2869 2007-02-20 14:26:09Z andegje $
 */
public class Module
{
    private String name;

    private String namespace;

    private String defaultAction;
    
    private String displayName;
    
    // Apps only
    
    private String icon;
    
    private String description;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public Module()
    {
    }
    
    public Module( String name )
    {
        this.name = name;
    }

    public Module( String name, String namespace )
    {
        this( name, namespace, null );
    }

    public Module( String name, String namespace, String defaultAction )
    {
        this.name = name;
        this.namespace = namespace;
        this.defaultAction = defaultAction;
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    public static Module getModule( App app )
    {
        boolean hasIcon = app.getIcons() != null && app.getIcons().getIcon48() != null;
        
        String defaultAction = app.getLaunchUrl();

        String icon = hasIcon ? app.getBaseUrl() + "/" + app.getIcons().getIcon48() : null;

        String description = TextUtils.subString( app.getDescription(), 0, 80 );
        
        Module module = new Module( app.getShortName(), app.getShortName(), defaultAction );
        module.setIcon( icon );
        module.setDescription( description );
        module.setDisplayName(app.getName());
        
        return module;
    }
    
    public String getIconFallback()
    {
        return icon != null ? icon : "../icons/" + name + ".png";
    }
    
    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getNamespace()
    {
        return namespace;
    }

    public void setNamespace( String namespace )
    {
        this.namespace = namespace;
    }

    public String getDefaultAction()
    {
        return defaultAction;
    }

    public void setDefaultAction( String defaultAction )
    {
        this.defaultAction = defaultAction;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName( String displayName )
    {
        this.displayName = displayName;
    }

    public String getIcon()
    {
        return icon;
    }

    public void setIcon( String icon )
    {
        this.icon = icon;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    // -------------------------------------------------------------------------
    // hashCode, equals, toString
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( name == null ) ? 0 : name.hashCode() );
        return result;
    }

    @Override
    public boolean equals( Object object )
    {
        if ( this == object )
        {
            return true;
        }
        
        if ( object == null )
        {
            return false;
        }
        
        if ( !getClass().isAssignableFrom( object.getClass() ) )
        {
            return false;
        }
        
        final Module other = (Module) object;
        
        return name.equals( other.getName() );
    }
    
    @Override
    public String toString()
    {
        return "[Name: " + name + ", namespace: " + namespace + ", default action: " + defaultAction + "]";
    }    
}
