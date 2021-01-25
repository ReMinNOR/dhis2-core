package org.hisp.dhis.webapi.webdomain;



import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Lars Helge Overland
 */
public class GeoFeature
{
    public static final int TYPE_POINT = 1;
    public static final int TYPE_POLYGON = 2;
    
    /**
     * Identifier.
     */
    private String id;

    /**
     * Code identifier.
     */
    private String code;

    /**
     * Name.
     */
    private String na;
    
    /**
     * Has coordinates down.
     */
    private boolean hcd;
    
    /**
     * Has coordinates up.
     */
    private boolean hcu;
    
    /**
     * Level.
     */
    private int le;
    
    /**
     * Parent graph.
     */
    private String pg;
    
    /**
     * Parent identifier.
     */
    private String pi;
    
    /**
     * Parent name.
     */
    private String pn;
    
    /**
     * Feature type.
     */
    private int ty;
    
    /**
     * Coordinates.
     */
    private String co;

    /**
     * Dimensions and dimension items.
     */
    private Map<String, String> dimensions = new HashMap<>();
    
    public GeoFeature()
    {
    }

    //--------------------------------------------------------------------------
    // Getters and setters
    //--------------------------------------------------------------------------

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
    public String getCode()
    {
        return code;
    }

    public void setCode( String code )
    {
        this.code = code;
    }

    @JsonProperty
    public String getNa()
    {
        return na;
    }

    public void setNa( String na )
    {
        this.na = na;
    }

    @JsonProperty
    public boolean isHcd()
    {
        return hcd;
    }

    public void setHcd( boolean hcd )
    {
        this.hcd = hcd;
    }

    @JsonProperty
    public boolean isHcu()
    {
        return hcu;
    }

    public void setHcu( boolean hcu )
    {
        this.hcu = hcu;
    }

    @JsonProperty
    public int getLe()
    {
        return le;
    }

    public void setLe( int le )
    {
        this.le = le;
    }

    @JsonProperty
    public String getPg()
    {
        return pg;
    }

    public void setPg( String pg )
    {
        this.pg = pg;
    }

    @JsonProperty
    public String getPi()
    {
        return pi;
    }

    public void setPi( String pi )
    {
        this.pi = pi;
    }

    @JsonProperty
    public String getPn()
    {
        return pn;
    }

    public void setPn( String pn )
    {
        this.pn = pn;
    }

    @JsonProperty
    public int getTy()
    {
        return ty;
    }

    public void setTy( int ty )
    {
        this.ty = ty;
    }

    @JsonProperty
    public String getCo()
    {
        return co;
    }

    public void setCo( String co )
    {
        this.co = co;
    }

    @JsonProperty
    public Map<String, String> getDimensions()
    {
        return dimensions;
    }

    public void setDimensions( Map<String, String> dimensions )
    {
        this.dimensions = dimensions;
    }
}
