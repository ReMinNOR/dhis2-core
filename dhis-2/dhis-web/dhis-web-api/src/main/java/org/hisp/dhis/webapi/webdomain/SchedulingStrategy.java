package org.hisp.dhis.webapi.webdomain;



import org.hisp.dhis.common.DxfNamespaces;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * @author Lars Helge Overland
 */
@JacksonXmlRootElement( localName = "scheduling", namespace = DxfNamespaces.DXF_2_0 )
public class SchedulingStrategy
{
    private String resourceTableStrategy;
    
    private String analyticsStrategy;
    
    private String dataMartStrategy;
    
    private String monitoringStrategy;
    
    private String dataSynchStrategy;

    private String dataStatisticsStrategy;
    
    public SchedulingStrategy()
    {
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getResourceTableStrategy()
    {
       return resourceTableStrategy;
    }

    public void setResourceTableStrategy( String resourceTableStrategy )
    {
        this.resourceTableStrategy = resourceTableStrategy;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getAnalyticsStrategy()
    {
        return analyticsStrategy;
    }

    public void setAnalyticsStrategy( String analyticsStrategy )
    {
        this.analyticsStrategy = analyticsStrategy;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getDataMartStrategy()
    {
        return dataMartStrategy;
    }

    public void setDataMartStrategy( String dataMartStrategy )
    {
        this.dataMartStrategy = dataMartStrategy;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getMonitoringStrategy()
    {
        return monitoringStrategy;
    }

    public void setMonitoringStrategy( String monitoringStrategy )
    {
        this.monitoringStrategy = monitoringStrategy;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getDataSynchStrategy()
    {
        return dataSynchStrategy;
    }

    public void setDataSynchStrategy( String dataSynchStrategy )
    {
        this.dataSynchStrategy = dataSynchStrategy;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getDataStatisticsStrategy()
    {
        return dataStatisticsStrategy;
    }

    public void setDataStatisticsStrategy( String dataStatisticsStrategy )
    {
        this.dataStatisticsStrategy = dataStatisticsStrategy;
    }
}
