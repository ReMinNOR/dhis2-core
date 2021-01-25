package org.hisp.dhis.webapi.mvc.messageconverter;



import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.hisp.dhis.common.Compression;
import org.hisp.dhis.node.NodeService;
import org.hisp.dhis.node.serializers.Jackson2JsonNodeSerializer;
import org.hisp.dhis.node.types.RootNode;
import org.hisp.dhis.webapi.service.ContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.List;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class JsonPMessageConverter extends AbstractRootNodeMessageConverter
{
    public static final String DEFAULT_CALLBACK_PARAMETER = "callback";

    public static final ImmutableList<MediaType> SUPPORTED_MEDIA_TYPES = ImmutableList.<MediaType>builder()
        .add( new MediaType( "application", "javascript" ) )
        .add( new MediaType( "application", "x-javascript" ) )
        .add( new MediaType( "text", "javascript" ) )
        .build();

    private ContextService contextService;

    public JsonPMessageConverter( @Autowired @Nonnull NodeService nodeService, @Autowired @Nonnull ContextService contextService )
    {
        super( nodeService, "application/json", "jsonp", Compression.NONE );
        this.contextService = contextService;
        setSupportedMediaTypes( SUPPORTED_MEDIA_TYPES );
    }

    @Override
    protected void writeInternal( RootNode rootNode, HttpOutputMessage outputMessage ) throws IOException, HttpMessageNotWritableException
    {
        List<String> callbacks = Lists.newArrayList( contextService.getParameterValues( DEFAULT_CALLBACK_PARAMETER ) );

        String callbackParam;

        if ( callbacks.isEmpty() )
        {
            callbackParam = DEFAULT_CALLBACK_PARAMETER;
        }
        else
        {
            callbackParam = callbacks.get( 0 );
        }

        rootNode.getConfig().getProperties().put( Jackson2JsonNodeSerializer.JSONP_CALLBACK, callbackParam );

        super.writeInternal( rootNode, outputMessage );
    }
}
