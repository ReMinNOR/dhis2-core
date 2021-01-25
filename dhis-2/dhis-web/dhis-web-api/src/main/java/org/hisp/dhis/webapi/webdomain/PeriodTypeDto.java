package org.hisp.dhis.webapi.webdomain;



import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.period.PeriodType;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@JacksonXmlRootElement( localName = "periodType", namespace = DxfNamespaces.DXF_2_0 )
public class PeriodTypeDto
{
    private final String name;
    private final String isoDuration;
    private final String isoFormat;
    private final int frequencyOrder;

    public PeriodTypeDto( PeriodType periodType )
    {
        this.name = periodType.getName();
        this.frequencyOrder = periodType.getFrequencyOrder();
        this.isoDuration = periodType.getIso8601Duration();
        this.isoFormat = periodType.getIsoFormat();
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getName()
    {
        return name;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getIsoDuration()
    {
        return isoDuration;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getIsoFormat()
    {
        return isoFormat;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public int getFrequencyOrder()
    {
        return frequencyOrder;
    }
}
