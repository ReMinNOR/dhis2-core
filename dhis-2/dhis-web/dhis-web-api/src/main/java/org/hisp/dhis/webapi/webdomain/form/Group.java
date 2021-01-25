package org.hisp.dhis.webapi.webdomain.form;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.common.DxfNamespaces;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@JacksonXmlRootElement( localName = "group", namespace = DxfNamespaces.DXF_2_0 )
public class Group
{
    private String label;
    
    private String description;
    
    private int dataElementCount;

    private List<Field> fields = new ArrayList<>();

    private Map<Object, Object> metaData = new HashMap<>();

    public Group()
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
    public String getDescription()
    {
        return description;
    }
    
    public void setDescription( String description )
    {
        this.description = description;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public int getDataElementCount()
    {
        return dataElementCount;
    }
    
    public void setDataElementCount( int dataElementCount )
    {
        this.dataElementCount = dataElementCount;
    }
    
    @JsonProperty( value = "fields" )
    @JacksonXmlElementWrapper( localName = "fields", namespace = DxfNamespaces.DXF_2_0 )
    @JacksonXmlProperty( localName = "field", namespace = DxfNamespaces.DXF_2_0 )
    public List<Field> getFields()
    {
        return fields;
    }

    public void setFields( List<Field> fields )
    {
        this.fields = fields;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "metaData", namespace = DxfNamespaces.DXF_2_0 )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Map<Object, Object> getMetaData()
    {
        return metaData;
    }
    
    public void setMetaData( Map<Object, Object> metaData )
    {
        this.metaData = metaData;
    }
}
