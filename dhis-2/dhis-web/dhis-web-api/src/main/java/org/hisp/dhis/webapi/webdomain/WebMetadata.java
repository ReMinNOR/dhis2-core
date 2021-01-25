package org.hisp.dhis.webapi.webdomain;



import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.common.Pager;
import org.hisp.dhis.dxf2.metadata.Metadata;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class WebMetadata
    extends Metadata
{
    private Pager pager;

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Pager getPager()
    {
        return pager;
    }

    public void setPager( Pager pager )
    {
        this.pager = pager;
    }
}
