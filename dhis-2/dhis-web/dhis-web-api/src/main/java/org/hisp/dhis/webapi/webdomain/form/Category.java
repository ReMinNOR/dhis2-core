package org.hisp.dhis.webapi.webdomain.form;



import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hisp.dhis.common.DxfNamespaces;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Viet Nguyen <viet@dhis2.org>
 */
@JacksonXmlRootElement( localName = "category", namespace = DxfNamespaces.DXF_2_0 )
public class Category
{
    private String id;

    private String label;

    private List<Option> options = new ArrayList<>();

    @JsonProperty( value = "categoryOptions" )
    @JacksonXmlElementWrapper( localName = "categoryOptions", namespace = DxfNamespaces.DXF_2_0 )
    @JacksonXmlProperty( localName = "categoryOption", namespace = DxfNamespaces.DXF_2_0 )
    public List<Option> getOptions()
    {
        return options;
    }

    public void setOptions( List<Option> options )
    {
        this.options = options;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getLabel()
    {
        return label;
    }

    public void setLabel( String label )
    {
        this.label = label;
    }
}
