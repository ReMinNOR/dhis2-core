package org.hisp.dhis.util;



import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.IdentifiableObjectUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class ContextUtils
{
    public static final String CONTENT_TYPE_PDF = "application/pdf";
    public static final String CONTENT_TYPE_ZIP = "application/zip";
    public static final String CONTENT_TYPE_GZIP = "application/gzip";
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CONTENT_TYPE_HTML = "text/html";
    public static final String CONTENT_TYPE_TEXT = "text/plain";
    public static final String CONTENT_TYPE_XML = "application/xml";
    public static final String CONTENT_TYPE_CSV = "application/csv";
    public static final String CONTENT_TYPE_PNG = "image/png";
    public static final String CONTENT_TYPE_JPG = "image/jpeg";
    public static final String CONTENT_TYPE_EXCEL = "application/vnd.ms-excel";
    public static final String CONTENT_TYPE_JAVASCRIPT = "application/javascript";

    public static final String HEADER_USER_AGENT = "User-Agent";
    public static final String HEADER_IF_NONE_MATCH = "If-None-Match";
    public static final String HEADER_ETAG = "ETag";

    private static final String QUOTE = "\"";
    
    private static final Map<String, String> CONTENT_TYPE_MAP = new HashMap<String, String>() 
    { {
        put( "pdf", CONTENT_TYPE_PDF );
        put( "zip", CONTENT_TYPE_ZIP );
        put( "json", CONTENT_TYPE_JSON );
        put( "html", CONTENT_TYPE_HTML );
        put( "txt", CONTENT_TYPE_TEXT );
        put( "xml", CONTENT_TYPE_XML );
        put( "csv", CONTENT_TYPE_CSV );
        put( "png", CONTENT_TYPE_PNG );
        put( "xls", CONTENT_TYPE_EXCEL );
    } };

    public static String getContentType( String type, String defaultType )
    {
        String contentType = CONTENT_TYPE_MAP.get( type );
        return contentType != null ? contentType : defaultType;
    }

    public static Map<String, String> getParameterMap( HttpServletRequest request )
    {
        Enumeration<String> enumeration = request.getParameterNames();

        Map<String, String> params = new HashMap<>();

        while ( enumeration.hasMoreElements() )
        {
            String name = enumeration.nextElement();

            params.put( name, request.getParameter( name ) );
        }

        return params;
    }

    public static void configureResponse( HttpServletResponse response, String contentType, boolean disallowCache,
        String filename, boolean attachment )
    {
        if ( contentType != null )
        {
            response.setContentType( contentType );
        }

        if ( disallowCache )
        {
            response.setHeader( "Cache-Control", "no-cache, no-store" );
        }

        if ( filename != null )
        {
            String type = attachment ? "attachment" : "inline";

            response.setHeader( "Content-Disposition", type + "; filename=\"" + filename + "\"" );
        }
    }

    public static String getCookieValue( HttpServletRequest request, String cookieName )
    {
        Cookie[] cookies = request.getCookies();

        if ( cookies != null )
        {
            for ( Cookie c : cookies )
            {
                if ( c.getName().equalsIgnoreCase( cookieName ) )
                {
                    return c.getValue();
                }
            }
        }
        
        return null;
    }
        
    /**
     * Clears the given collection if it is not modified according to the HTTP
     * cache validation. This method looks up the ETag sent in the request from 
     * the "If-None-Match" header value, generates an ETag based on the given 
     * collection of IdentifiableObjects and compares them for equality. If this
     * evaluates to true, it will set status code 304 Not Modified on the response
     * and remove all elements from the given list. It will also set the ETag header
     * on the response in any case.
     * 
     * @param request the HttpServletRequest.
     * @param response the HttpServletResponse.
     * @return true if the eTag values are equals, false otherwise.
     */
    public static boolean clearIfNotModified( HttpServletRequest request, HttpServletResponse response, Collection<? extends IdentifiableObject> objects )
    {
        String tag = QUOTE + IdentifiableObjectUtils.getLastUpdatedTag( objects ) + QUOTE;
        
        response.setHeader( HEADER_ETAG, tag );
        
        String inputTag = request.getHeader( HEADER_IF_NONE_MATCH );

        if ( objects != null && inputTag != null && inputTag.equals( tag ) )
        {
            response.setStatus( HttpServletResponse.SC_NOT_MODIFIED );
            
            objects.clear();
            
            return true;
        }
        
        return false;
    }

    /**
     * Returns true if the given object is not modified according to the HTTP
     * cache validation. This method looks up the ETag sent in the request from 
     * the "If-None-Match" header value, generates an ETag based on the given 
     * collection of IdentifiableObjects and compares them for equality. If this
     * evaluates to true, it will set status code 304 Not Modified on the response.
     * It will also set the ETag header on the response in any case.
     * 
     * @param request the HttpServletRequest.
     * @param response the HttpServletResponse.
     * @return true if the eTag values are equals, false otherwise.
     */
    public static boolean isNotModified( HttpServletRequest request, HttpServletResponse response, IdentifiableObject object )
    {
        String tag = IdentifiableObjectUtils.getLastUpdatedTag( object );
        
        response.setHeader( HEADER_ETAG, tag );
        
        String inputTag = request.getHeader( HEADER_IF_NONE_MATCH );

        if ( object != null && inputTag != null && tag != null && inputTag.equals( tag ) )
        {
            response.setStatus( HttpServletResponse.SC_NOT_MODIFIED );
            
            return true;
        }
        
        return false;
    }

    /**
     * Creates a ZipOutputStream based on the HttpServletResponse and puts a
     * new ZipEntry with the given filename to it.
     * 
     * @param out the output stream.
     * @param fileName the filename of the file inside the ZIP archive.
     * @return a ZipOutputStream
     * @throws IOException
     */
    public static ZipOutputStream getZipOut( OutputStream out, String fileName )
        throws IOException
    {
        ZipOutputStream zipOut = new ZipOutputStream( out );
        zipOut.putNextEntry( new ZipEntry( fileName ) );        
        return zipOut;
    }
}
