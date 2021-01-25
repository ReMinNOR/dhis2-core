package org.hisp.dhis.webapi.view;



import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.accept.PathExtensionContentNegotiationStrategy;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.Map;

/**
 * Custom PathExtensionContentNegotiationStrategy that handles multiple dots in filename.
 * Based on:
 * org.springframework.web.accept.PathExtensionContentNegotiationStrategy
 * org.springframework.util.StringUtils
 *
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class CustomPathExtensionContentNegotiationStrategy extends PathExtensionContentNegotiationStrategy
{
    private static final UrlPathHelper URL_PATH_HELPER = new UrlPathHelper();

    private static final char EXTENSION_SEPARATOR = '.';

    private static final String FOLDER_SEPARATOR = "/";

    static
    {
        URL_PATH_HELPER.setUrlDecode( false );
    }

    public CustomPathExtensionContentNegotiationStrategy( Map<String, MediaType> mediaTypes )
    {
        super( mediaTypes );
    }

    @Override
    protected String getMediaTypeKey( NativeWebRequest webRequest )
    {
        HttpServletRequest servletRequest = webRequest.getNativeRequest( HttpServletRequest.class );

        if ( servletRequest == null )
        {
            return null;
        }

        String path = URL_PATH_HELPER.getLookupPathForRequest( servletRequest );
        String extension = getFilenameExtension( path );

        return !StringUtils.isBlank( extension ) ? extension.toLowerCase( Locale.ENGLISH ) : null;
    }

    private static String getFilenameExtension( String path )
    {
        if ( path == null )
        {
            return null;
        }
        int extIndex = path.indexOf( EXTENSION_SEPARATOR );

        if ( extIndex == -1 )
        {
            return null;
        }

        int folderIndex = path.indexOf( FOLDER_SEPARATOR );

        if ( folderIndex > extIndex )
        {
            return null;
        }

        return path.substring( extIndex + 1 );
    }
}
