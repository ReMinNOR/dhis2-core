package org.hisp.dhis.appcache;



import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContext;

import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.i18n.locale.LocaleManager;
import org.hisp.dhis.system.SystemInfo;
import org.hisp.dhis.system.SystemService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Abyot Asalefew Gizaw <abyota@gmail.com>
 *
 */
public class CacheManifestAction
    implements Action
{
    @Autowired
    private SystemService systemService;
    
    @Autowired
    private LocaleManager localeManager;

    private String appPath;

    public void setAppPath( String appPath )
    {
        this.appPath = appPath;
    }

    private String i18nPath;

    public void setI18nPath( String i18nPath )
    {
        this.i18nPath = i18nPath;
    }

    private String appCache;

    public void setAppCache( String appCache )
    {
        this.appCache = appCache;
    }

    private InputStream inputStream;

    public InputStream getInputStream()
    {
        return inputStream;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
    {
        File cacheManifest = null;
        File i18nFolder = null;
        StringBuilder builder = null;
        
        String locale = localeManager.getCurrentLocale().toString();
        
        SystemInfo info = systemService.getSystemInfo();
        String revisionTag = "#Revision:" + info.getRevision();

        String defaultTranslationFile = "i18n_app.properties";
        String translationFile = "";
        if ( locale.equalsIgnoreCase( "en" ) )
        {
            translationFile = defaultTranslationFile;
        }
        else
        {
            translationFile = "i18n_app_" + locale + ".properties";
        }

        if ( appPath != null && appCache != null )
        {

            ServletContext servletContext = ServletActionContext.getServletContext();
            String fullPath = servletContext.getRealPath( appPath );

            File folder = new File( fullPath );
            File[] files = folder.listFiles();

            if ( files != null )
            {
                for ( int i = 0; i < files.length; i++ )
                {
                    if ( files[i].isFile() && files[i].getName().equalsIgnoreCase( appCache ) )
                    {
                        cacheManifest = new File( files[i].getAbsolutePath() );
                    }
    
                    if ( i18nPath != null && files[i].isDirectory() && files[i].getName().equalsIgnoreCase( i18nPath ) )
                    {
                        i18nFolder = new File( files[i].getAbsolutePath() );
                    }
                }
            }
        }

        if ( cacheManifest != null )
        {
            try ( BufferedReader bufferedReader = new BufferedReader( new FileReader( cacheManifest ) ) )
            {
                builder = new StringBuilder();
                String line;
                
                while ( (line = bufferedReader.readLine()) != null )
                {
                    builder.append( line );
                    builder.append( "\n" );
                }
                
                builder.append( revisionTag );
                builder.append( "\n" );

                if ( i18nFolder != null )
                {
                    Boolean fileExists = false;
                    
                    File[] files = i18nFolder.listFiles();
                    
                    if ( files != null )
                    {
                        for ( int i = 0; i < files.length; i++ )
                        {
                            if ( files[i].isFile() && files[i].getName().equalsIgnoreCase( translationFile ) )
                            {
                                fileExists = true;
                                builder.append( i18nPath + "/" + translationFile );
                                builder.append( "\n" );
                                break;
                            }
                        }
                    }

                    if ( !fileExists )
                    {
                        builder.append( i18nPath + "/" + defaultTranslationFile );
                        builder.append( "\n" );
                    }
                }

                inputStream = new ByteArrayInputStream( builder.toString().getBytes() );

                return SUCCESS;

            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
        }

        builder = new StringBuilder();
        builder.append( "CACHE MANIFEST" );
        builder.append( revisionTag );
        builder.append( "\n" );
        builder.append( "NETWORK:" );
        builder.append( "\n" );
        builder.append( "*" );

        inputStream = new ByteArrayInputStream( builder.toString().getBytes() );

        return SUCCESS;
    }
}
