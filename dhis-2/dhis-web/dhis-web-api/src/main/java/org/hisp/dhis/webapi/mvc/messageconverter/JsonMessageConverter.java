package org.hisp.dhis.webapi.mvc.messageconverter;



import com.google.common.collect.ImmutableList;
import org.hisp.dhis.common.Compression;
import org.hisp.dhis.node.NodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import javax.annotation.Nonnull;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class JsonMessageConverter extends AbstractRootNodeMessageConverter
{
    public static final ImmutableList<MediaType> SUPPORTED_MEDIA_TYPES = ImmutableList.<MediaType>builder()
        .add( new MediaType( "application", "json" ) )
        .build();

    public static final ImmutableList<MediaType> GZIP_SUPPORTED_MEDIA_TYPES = ImmutableList.<MediaType>builder()
        .add( new MediaType( "application", "json+gzip" ) )
        .build();

    public static final ImmutableList<MediaType> ZIP_SUPPORTED_MEDIA_TYPES = ImmutableList.<MediaType>builder()
        .add( new MediaType( "application", "json+zip" ) )
        .build();

    public JsonMessageConverter( @Autowired @Nonnull NodeService nodeService, Compression compression )
    {
        super( nodeService, "application/json", "json", compression );
        switch ( getCompression() )
        {
            case NONE:
                setSupportedMediaTypes( SUPPORTED_MEDIA_TYPES );
                break;
            case GZIP:
                setSupportedMediaTypes( GZIP_SUPPORTED_MEDIA_TYPES );
                break;
            case ZIP:
                setSupportedMediaTypes( ZIP_SUPPORTED_MEDIA_TYPES );
        }
    }
}
