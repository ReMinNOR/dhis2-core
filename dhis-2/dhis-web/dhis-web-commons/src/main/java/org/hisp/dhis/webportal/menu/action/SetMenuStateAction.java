package org.hisp.dhis.webportal.menu.action;



import com.opensymphony.xwork2.Action;
import org.hisp.dhis.webportal.menu.MenuState;
import org.hisp.dhis.webportal.menu.MenuStateManager;
import org.springframework.beans.factory.annotation.Autowired;

public class SetMenuStateAction
    implements Action
{
    @Autowired
    private MenuStateManager manager;

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String state;

    public void setState( String state )
    {
        this.state = state;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute() throws Exception
    {
        if ( state != null )
        {
            MenuState menuState;

            try
            {
                menuState = MenuState.valueOf( state.toUpperCase() );
            }
            catch ( IllegalArgumentException ex )
            {
                return INPUT;
            }

            manager.setMenuState( menuState );
        }

        return SUCCESS;
    }
}
