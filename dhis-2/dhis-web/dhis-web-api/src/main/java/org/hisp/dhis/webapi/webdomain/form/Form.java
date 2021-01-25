package org.hisp.dhis.webapi.webdomain.form;



import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hisp.dhis.common.DxfNamespaces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@JacksonXmlRootElement( localName = "form", namespace = DxfNamespaces.DXF_2_0 )
public class Form
{
    private String label;

    private String subtitle;

    private List<Group> groups = new ArrayList<>();

    private Map<String, Object> options = new HashMap<>();

    private CategoryCombo categoryCombo;

    public Form()
    {
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

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getSubtitle()
    {
        return subtitle;
    }

    public void setSubtitle( String subtitle )
    {
        this.subtitle = subtitle;
    }

    @JsonProperty( value = "groups" )
    @JacksonXmlElementWrapper( localName = "groups", namespace = DxfNamespaces.DXF_2_0 )
    @JacksonXmlProperty( localName = "group", namespace = DxfNamespaces.DXF_2_0 )
    public List<Group> getGroups()
    {
        return groups;
    }

    public void setGroups( List<Group> groups )
    {
        this.groups = groups;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Map<String, Object> getOptions()
    {
        return options;
    }

    public void setOptions( Map<String, Object> options )
    {
        this.options = options;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public CategoryCombo getCategoryCombo()
    {
        return categoryCombo;
    }

    public void setCategoryCombo( CategoryCombo categoryCombo )
    {
        this.categoryCombo = categoryCombo;
    }


    @Override
    public String toString()
    {
        return "Form{" +
            "label='" + label + '\'' +
            ", groups=" + groups +
            '}';
    }
}
