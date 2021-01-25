package org.hisp.dhis.webapi.webdomain.sharing;



import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class Meta
{
    @JsonProperty
    private boolean allowPublicAccess;

    @JsonProperty
    private boolean allowExternalAccess;

    public Meta()
    {
    }

    public boolean isAllowPublicAccess()
    {
        return allowPublicAccess;
    }

    public void setAllowPublicAccess( boolean allowPublicAccess )
    {
        this.allowPublicAccess = allowPublicAccess;
    }

    public boolean isAllowExternalAccess()
    {
        return allowExternalAccess;
    }

    public void setAllowExternalAccess( boolean allowExternalAccess )
    {
        this.allowExternalAccess = allowExternalAccess;
    }
}
