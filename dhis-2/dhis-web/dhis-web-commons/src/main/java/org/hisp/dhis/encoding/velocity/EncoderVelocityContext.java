package org.hisp.dhis.encoding.velocity;



import org.apache.velocity.VelocityContext;
import org.hisp.dhis.commons.util.Encoder;

/**
 * @author Torgeir Lorange Ostby
 */
public class EncoderVelocityContext
    extends VelocityContext
{
    public static final String KEY = "encoder";

    private static final Encoder ENCODER = new Encoder();

    // -------------------------------------------------------------------------
    // Override VelocityContext methods
    // -------------------------------------------------------------------------

    @Override
    public Object internalGet( String key )
    {
        if ( KEY.equals( key ) )
        {
            return ENCODER;
        }

        return super.internalGet( key );
    }

    @Override
    public boolean containsKey( Object key )
    {
        return KEY.equals( key ) || super.containsKey( key );
    }
}
