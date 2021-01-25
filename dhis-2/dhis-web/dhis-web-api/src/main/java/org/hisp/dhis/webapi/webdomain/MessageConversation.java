package org.hisp.dhis.webapi.webdomain;



import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.fileresource.FileResource;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserGroup;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@JacksonXmlRootElement( localName = "message", namespace = DxfNamespaces.DXF_2_0 )
public class MessageConversation
{
    private String subject;

    private String text;

    private Set<OrganisationUnit> organisationUnits = new HashSet<>();

    private Set<User> users = new HashSet<>();

    private Set<UserGroup> userGroups = new HashSet<>();

    private Set<FileResource> attachments = new HashSet<>();

    public MessageConversation()
    {
    }

    public MessageConversation( String subject, String text )
    {
        this.subject = subject;
        this.text = text;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getSubject()
    {
        return subject;
    }

    public void setSubject( String subject )
    {
        this.subject = subject;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getText()
    {
        return text;
    }

    public void setText( String text )
    {
        this.text = text;
    }

    @JsonProperty
    @JsonSerialize( contentAs = BaseIdentifiableObject.class )
    @JacksonXmlElementWrapper( localName = "organisationUnits", namespace = DxfNamespaces.DXF_2_0 )
    @JacksonXmlProperty( localName = "organisationUnit", namespace = DxfNamespaces.DXF_2_0 )
    public Set<OrganisationUnit> getOrganisationUnits()
    {
        return organisationUnits;
    }

    public void setOrganisationUnits( Set<OrganisationUnit> organisationUnits )
    {
        this.organisationUnits = organisationUnits;
    }

    @JsonProperty
    @JsonSerialize( contentAs = BaseIdentifiableObject.class )
    @JacksonXmlElementWrapper( localName = "users", namespace = DxfNamespaces.DXF_2_0 )
    @JacksonXmlProperty( localName = "user", namespace = DxfNamespaces.DXF_2_0 )
    public Set<User> getUsers()
    {
        return users;
    }

    public void setUsers( Set<User> users )
    {
        this.users = users;
    }

    @JsonProperty
    @JsonSerialize( contentAs = BaseIdentifiableObject.class )
    @JacksonXmlElementWrapper( localName = "userGroups", namespace = DxfNamespaces.DXF_2_0 )
    @JacksonXmlProperty( localName = "userGroup", namespace = DxfNamespaces.DXF_2_0 )
    public Set<UserGroup> getUserGroups()
    {
        return userGroups;
    }

    public void setUserGroups( Set<UserGroup> userGroups )
    {
        this.userGroups = userGroups;
    }

    @Override
    public String toString()
    {
        return "Message{" +
            "subject='" + subject + '\'' +
            ", text='" + text + '\'' +
            ", organisationUnits=" + organisationUnits +
            ", users=" + users +
            ", userGroups=" + userGroups +
            '}';
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Set<FileResource> getAttachments()
    {
        return attachments;
    }

    public void setAttachments( Set<FileResource> attachments )
    {
        this.attachments = attachments;
    }
}
