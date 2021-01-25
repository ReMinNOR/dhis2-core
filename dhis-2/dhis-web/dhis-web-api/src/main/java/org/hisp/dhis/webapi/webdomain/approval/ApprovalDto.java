package org.hisp.dhis.webapi.webdomain.approval;



import org.hisp.dhis.common.DxfNamespaces;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement( localName = "approval", namespace = DxfNamespaces.DXF_2_0 )
public class ApprovalDto
{
    private String ou;
    
    private String aoc;
    
    public ApprovalDto()
    {
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getOu()
    {
        return ou;
    }

    public void setOu( String ou )
    {
        this.ou = ou;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getAoc()
    {
        return aoc;
    }

    public void setAoc( String aoc )
    {
        this.aoc = aoc;
    }
}
