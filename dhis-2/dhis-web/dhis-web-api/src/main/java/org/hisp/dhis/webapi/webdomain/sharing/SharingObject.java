package org.hisp.dhis.webapi.webdomain.sharing;



import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class SharingObject
{
    private String id;

    private String name;

    private String displayName;

    private String publicAccess;

    private boolean externalAccess;

    private SharingUser user = new SharingUser();

    private List<SharingUserGroupAccess> userGroupAccesses = new ArrayList<>();

    private List<SharingUserAccess> userAccesses = new ArrayList<>();

    public SharingObject()
    {
    }

    @JsonProperty
    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    @JsonProperty
    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    @JsonProperty
    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName( String displayName )
    {
        this.displayName = displayName;
    }

    @JsonProperty
    public String getPublicAccess()
    {
        return publicAccess;
    }

    public void setPublicAccess( String publicAccess )
    {
        this.publicAccess = publicAccess;
    }

    @JsonProperty( "externalAccess" )
    public boolean hasExternalAccess()
    {
        return externalAccess;
    }

    @JsonProperty( "externalAccess" )
    public void setExternalAccess( boolean externalAccess )
    {
        this.externalAccess = externalAccess;
    }

    @JsonProperty
    public SharingUser getUser()
    {
        return user;
    }

    public void setUser( SharingUser user )
    {
        this.user = user;
    }

    @JsonProperty
    public List<SharingUserGroupAccess> getUserGroupAccesses()
    {
        return userGroupAccesses;
    }

    public void setUserGroupAccesses( List<SharingUserGroupAccess> userGroupAccesses )
    {
        this.userGroupAccesses = userGroupAccesses;
    }

    @JsonProperty
    public List<SharingUserAccess> getUserAccesses()
    {
        return userAccesses;
    }

    public void setUserAccesses( List<SharingUserAccess> userAccesses )
    {
        this.userAccesses = userAccesses;
    }
}
