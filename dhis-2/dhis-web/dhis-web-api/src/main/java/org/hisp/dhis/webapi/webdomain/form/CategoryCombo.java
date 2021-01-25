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
@JacksonXmlRootElement( localName = "categoryCombo", namespace = DxfNamespaces.DXF_2_0 )
public class CategoryCombo
{
    private String id;

    private List<Category> categories = new ArrayList<>();

    @JsonProperty( value = "categories" )
    @JacksonXmlElementWrapper( localName = "categories", namespace = DxfNamespaces.DXF_2_0 )
    @JacksonXmlProperty( localName = "category", namespace = DxfNamespaces.DXF_2_0 )
    public List<Category> getCategories()
    {
        return categories;
    }

    public void setCategories( List<Category> categories )
    {
        this.categories = categories;
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
}
