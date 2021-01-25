package org.hisp.dhis.webapi.webdomain;



import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashSet;
import java.util.Set;

/**
 * Simplified organisation unit class, to be used where all you need
 * is a label + dataSets.
 *
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class FormOrganisationUnit
{
    private String id;

    private String label;

    private Integer level;

    private String parent;

    private Set<FormDataSet> dataSets = new HashSet<>();

    private Set<FormProgram> programs = new HashSet<>();

    public FormOrganisationUnit()
    {
    }

    @JsonProperty
    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    @JsonProperty
    public String getLabel()
    {
        return label;
    }

    public void setLabel( String label )
    {
        this.label = label;
    }

    @JsonProperty
    public Integer getLevel()
    {
        return level;
    }

    public void setLevel( Integer level )
    {
        this.level = level;
    }

    @JsonProperty
    public String getParent()
    {
        return parent;
    }

    public void setParent( String parent )
    {
        this.parent = parent;
    }

    @JsonProperty
    public Set<FormDataSet> getDataSets()
    {
        return dataSets;
    }

    public void setDataSets( Set<FormDataSet> dataSets )
    {
        this.dataSets = dataSets;
    }

    @JsonProperty
    public Set<FormProgram> getPrograms()
    {
        return programs;
    }

    public void setPrograms( Set<FormProgram> programs )
    {
        this.programs = programs;
    }
}
