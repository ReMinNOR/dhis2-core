package org.hisp.dhis.webapi.mvc.messageconverter;



import org.apache.commons.lang3.StringUtils;
import org.hisp.dhis.common.Compression;
import org.hisp.dhis.node.NodeService;
import org.hisp.dhis.node.types.RootNode;
import org.hisp.dhis.webapi.utils.ContextUtils;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotWritableException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Abstract base class for HTTP message converters that convert root nodes.
 *
 * @author Volker Schmidt <volker@dhis2.org>
 */
public abstract class AbstractRootNodeMessageConverter extends AbstractHttpMessageConverter<RootNode>
{
    /**
     * File name that will get a media type related suffix when included as an attachment file name.
     */
    private static final Set<String> EXTENSIBLE_ATTACHMENT_FILENAMES = Collections.unmodifiableSet( new HashSet<>( Collections.singleton( "metadata" ) ) );

    private final NodeService nodeService;

    private final String contentType;

    private final String fileExtension;

    private final Compression compression;

    protected AbstractRootNodeMessageConverter( @Nonnull NodeService nodeService, @Nonnull String contentType, @Nonnull String fileExtension, Compression compression )
    {
        this.nodeService = nodeService;
        this.contentType = contentType;
        this.fileExtension = fileExtension;
        this.compression = compression;
    }

    protected Compression getCompression()
    {
        return compression;
    }

    @Override
    protected boolean supports( Class<?> clazz )
    {
        return RootNode.class.equals( clazz );
    }

    @Override
    protected boolean canRead( MediaType mediaType )
    {
        return false;
    }

    @Override
    protected RootNode readInternal( Class<? extends RootNode> clazz, HttpInputMessage inputMessage )
    {
        return null;
    }

    @Override
    protected void writeInternal( RootNode rootNode, HttpOutputMessage outputMessage ) throws IOException, HttpMessageNotWritableException
    {
        final String contentDisposition = outputMessage.getHeaders().getFirst( ContextUtils.HEADER_CONTENT_DISPOSITION );
        final boolean attachment = isAttachment( contentDisposition );
        final String extensibleAttachmentFilename = getExtensibleAttachmentFilename( contentDisposition );
        if ( Compression.GZIP == compression )
        {
            if ( !attachment || (extensibleAttachmentFilename != null) )
            {
                outputMessage.getHeaders().set( ContextUtils.HEADER_CONTENT_DISPOSITION, getContentDispositionHeaderValue( extensibleAttachmentFilename, "gz" ) );
                outputMessage.getHeaders().set( ContextUtils.HEADER_CONTENT_TRANSFER_ENCODING, "binary" );
            }

            GZIPOutputStream outputStream = new GZIPOutputStream( outputMessage.getBody() );
            nodeService.serialize( rootNode, contentType, outputStream );
            outputStream.close();
        }
        else if ( Compression.ZIP == compression )
        {
            if ( !attachment || (extensibleAttachmentFilename != null) )
            {
                outputMessage.getHeaders().set( ContextUtils.HEADER_CONTENT_DISPOSITION, getContentDispositionHeaderValue( extensibleAttachmentFilename, "zip" ) );
                outputMessage.getHeaders().set( ContextUtils.HEADER_CONTENT_TRANSFER_ENCODING, "binary" );
            }

            ZipOutputStream outputStream = new ZipOutputStream( outputMessage.getBody() );
            outputStream.putNextEntry( new ZipEntry( "metadata." + fileExtension ) );
            nodeService.serialize( rootNode, contentType, outputStream );
            outputStream.close();
        }
        else
        {
            if ( extensibleAttachmentFilename != null )
            {
                outputMessage.getHeaders().set( ContextUtils.HEADER_CONTENT_DISPOSITION, getContentDispositionHeaderValue( extensibleAttachmentFilename, null ) );
            }

            nodeService.serialize( rootNode, contentType, outputMessage.getBody() );
            outputMessage.getBody().close();
        }
    }

    @Nonnull
    protected String getContentDispositionHeaderValue( @Nullable String extensibleFilename, @Nullable String compressionExtension )
    {
        final String suffix = (compressionExtension == null) ? "" : "." + compressionExtension;
        return "attachment; filename=" + StringUtils.defaultString( extensibleFilename, "metadata" ) + "." + fileExtension + suffix;
    }

    protected boolean isAttachment( @Nullable String contentDispositionHeaderValue )
    {
        return (contentDispositionHeaderValue != null) && contentDispositionHeaderValue.contains( "attachment" );
    }

    @Nullable
    protected String getExtensibleAttachmentFilename( @Nullable String contentDispositionHeaderValue )
    {
        final String filename = ContextUtils.getAttachmentFileName( contentDispositionHeaderValue );
        return (filename != null) && EXTENSIBLE_ATTACHMENT_FILENAMES.contains( filename ) ? filename : null;
    }
}

