package org.hisp.dhis.webapi.webdomain.approval;



import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.dataapproval.DataApprovalPermissions;
import org.hisp.dhis.dataapproval.DataApprovalState;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement( localName = "approvalStatus", namespace = DxfNamespaces.DXF_2_0 )
public class ApprovalStatusDto
{
    private String wf;

    private String pe;

    private String ou;

    private String ouName;

    private String aoc;

    private DataApprovalState state;

    private String level;

    private DataApprovalPermissions permissions;

    public ApprovalStatusDto()
    {
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getWf()
    {
        return wf;
    }

    public void setWf( String wf )
    {
        this.wf = wf;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getPe()
    {
        return pe;
    }

    public void setPe( String pe )
    {
        this.pe = pe;
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
    public String getOuName()
    {
        return ouName;
    }

    public void setOuName( String ouName )
    {
        this.ouName = ouName;
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

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public DataApprovalState getState()
    {
        return state;
    }

    public void setState( DataApprovalState state )
    {
        this.state = state;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getLevel()
    {
        return level;
    }

    public void setLevel( String level )
    {
        this.level = level;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public DataApprovalPermissions getPermissions()
    {
        return permissions;
    }

    public void setPermissions( DataApprovalPermissions permissions )
    {
        this.permissions = permissions;
    }
}
