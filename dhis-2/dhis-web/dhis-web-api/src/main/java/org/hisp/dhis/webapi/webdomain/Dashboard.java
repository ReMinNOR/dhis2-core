package org.hisp.dhis.webapi.webdomain;



import org.hisp.dhis.common.DxfNamespaces;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@JacksonXmlRootElement( localName = "dashboard", namespace = DxfNamespaces.DXF_2_0 )
public class Dashboard
{
    private long unreadMessageConversation;

    private long unreadInterpretations;

    public Dashboard()
    {
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public long getUnreadMessageConversations()
    {
        return unreadMessageConversation;
    }

    public void setUnreadMessageConversations( long unreadMessageConversation )
    {
        this.unreadMessageConversation = unreadMessageConversation;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public long getUnreadInterpretations()
    {
        return unreadInterpretations;
    }

    public void setUnreadInterpretations( long unreadInterpretations )
    {
        this.unreadInterpretations = unreadInterpretations;
    }
}
