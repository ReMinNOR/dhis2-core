package org.hisp.dhis.webapi.mvc.messageconverter;



import com.google.common.collect.ImmutableList;
import org.hisp.dhis.render.RenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class RenderServiceMessageConverter extends AbstractHttpMessageConverter<Object>
{
    public static final ImmutableList<MediaType> SUPPORTED_MEDIA_TYPES = ImmutableList.<MediaType>builder()
        .add( new MediaType( "application", "json" ) )
        .add( new MediaType( "application", "xml" ) )
        .build();

    @Autowired
    private RenderService renderService;

    public RenderServiceMessageConverter()
    {
        setSupportedMediaTypes( SUPPORTED_MEDIA_TYPES );
    }

    @Override
    protected boolean supports( Class<?> clazz )
    {
        return Object.class.isAssignableFrom( clazz );
    }

    @Override
    protected Object readInternal( Class<?> clazz, HttpInputMessage inputMessage ) throws IOException, HttpMessageNotReadableException
    {
        MediaType mediaType = inputMessage.getHeaders().getContentType();

        if ( isJson( mediaType ) )
        {
            return renderService.fromJson( inputMessage.getBody(), clazz );
        }
        else if ( isXml( mediaType ) )
        {
            return renderService.fromXml( inputMessage.getBody(), clazz );
        }

        return null;
    }

    @Override
    protected void writeInternal( Object object, HttpOutputMessage outputMessage ) throws IOException, HttpMessageNotWritableException
    {
        MediaType mediaType = outputMessage.getHeaders().getContentType();

        if ( isJson( mediaType ) )
        {
            renderService.toJson( outputMessage.getBody(), object );
        }
        else if ( isXml( mediaType ) )
        {
            renderService.toXml( outputMessage.getBody(), object );
        }
    }

    private boolean isXml( MediaType mediaType )
    {
        return (mediaType.getType().equals( "application" ) || mediaType.getType().equals( "text" ))
            && mediaType.getSubtype().equals( "xml" );
    }

    private boolean isJson( MediaType mediaType )
    {
        return mediaType.getType().equals( "application" ) && mediaType.getSubtype().equals( "json" );
    }
}
