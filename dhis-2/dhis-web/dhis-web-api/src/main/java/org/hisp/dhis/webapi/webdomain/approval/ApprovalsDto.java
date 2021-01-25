package org.hisp.dhis.webapi.webdomain.approval;



import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.common.DxfNamespaces;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement( localName = "approvals", namespace = DxfNamespaces.DXF_2_0 )
public class ApprovalsDto
{
    private List<String> wf = new ArrayList<>();

    private List<String> ds = new ArrayList<>();

    private List<String> pe = new ArrayList<>();
    
    private List<ApprovalDto> approvals = new ArrayList<>();

    public ApprovalsDto()
    {
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public List<String> getWf()
    {
        return wf;
    }

    public void setWf( List<String> wf )
    {
        this.wf = wf;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public List<String> getDs()
    {
        return ds;
    }

    public void setDs( List<String> ds )
    {
        this.ds = ds;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public List<String> getPe()
    {
        return pe;
    }

    public void setPe( List<String> pe )
    {
        this.pe = pe;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public List<ApprovalDto> getApprovals()
    {
        return approvals;
    }

    public void setApprovals( List<ApprovalDto> approvals )
    {
        this.approvals = approvals;
    }
}
