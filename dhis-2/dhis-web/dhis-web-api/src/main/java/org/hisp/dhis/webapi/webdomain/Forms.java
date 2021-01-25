package org.hisp.dhis.webapi.webdomain;



import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.webapi.webdomain.form.Form;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@JacksonXmlRootElement( localName = "forms", namespace = DxfNamespaces.DXF_2_0 )
public class Forms
{
    /**
     * Maps ou.uid => org unit.
     */
    private Map<String, FormOrganisationUnit> organisationUnits = new HashMap<>();

    /**
     * Maps dataSet.uid => form instance.
     */
    private Map<String, Form> forms = new HashMap<>();

    private Map<String, List<String>> optionSets = Maps.newHashMap();

    public Forms()
    {
    }

    @JsonProperty( value = "organisationUnits" )
    @JacksonXmlElementWrapper( localName = "organisationUnits", namespace = DxfNamespaces.DXF_2_0 )
    @JacksonXmlProperty( localName = "organisationUnit", namespace = DxfNamespaces.DXF_2_0 )
    public Map<String, FormOrganisationUnit> getOrganisationUnits()
    {
        return organisationUnits;
    }

    public void setOrganisationUnits( Map<String, FormOrganisationUnit> organisationUnits )
    {
        this.organisationUnits = organisationUnits;
    }

    @JsonProperty( value = "forms" )
    @JacksonXmlElementWrapper( localName = "forms", namespace = DxfNamespaces.DXF_2_0 )
    @JacksonXmlProperty( localName = "form", namespace = DxfNamespaces.DXF_2_0 )
    public Map<String, Form> getForms()
    {
        return forms;
    }

    public void setForms( Map<String, Form> forms )
    {
        this.forms = forms;
    }

    @JsonProperty( value = "optionSets" )
    @JacksonXmlElementWrapper( localName = "optionSets", namespace = DxfNamespaces.DXF_2_0 )
    @JacksonXmlProperty( localName = "optionSet", namespace = DxfNamespaces.DXF_2_0 )
    public Map<String, List<String>> getOptionSets()
    {
        return optionSets;
    }

    public void setOptionSets( Map<String, List<String>> optionSets )
    {
        this.optionSets = optionSets;
    }
}
