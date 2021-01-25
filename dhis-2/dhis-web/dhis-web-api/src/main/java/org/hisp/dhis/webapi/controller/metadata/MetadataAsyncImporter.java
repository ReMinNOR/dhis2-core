package org.hisp.dhis.webapi.controller.metadata;



import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.commons.util.DebugUtils;
import org.hisp.dhis.dbms.DbmsUtils;
import org.hisp.dhis.dxf2.metadata.MetadataImportParams;
import org.hisp.dhis.dxf2.metadata.MetadataImportService;
import org.hisp.dhis.security.SecurityContextRunnable;
import org.hisp.dhis.system.notification.NotificationLevel;
import org.hisp.dhis.system.notification.Notifier;
import org.hisp.dhis.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author Viet Nguyen <viet@dhis2.org>
 */
@Component
@Scope( "prototype" )
@Slf4j
public class MetadataAsyncImporter extends SecurityContextRunnable
{
    @Autowired
    private MetadataImportService metadataImportService;

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private IdentifiableObjectManager manager;

    @Autowired
    private Notifier notifier;

    private MetadataImportParams params;

    @Override
    public void call()
    {
        // This is to fix LazyInitializationException
        if ( params.getUser() != null )
        {
            params.setUser( manager.get( User.class, params.getUser().getUid() ) );
        }

        if ( params.getOverrideUser() != null )
        {
            params.setOverrideUser( manager.get( User.class, params.getOverrideUser().getUid() ) );
        }

        metadataImportService.importMetadata( params );
    }

    @Override
    public void before()
    {
        DbmsUtils.bindSessionToThread( sessionFactory );
    }

    @Override
    public void after()
    {
        DbmsUtils.unbindSessionFromThread( sessionFactory );
    }

    @Override
    public void handleError( Throwable ex )
    {
        log.error( DebugUtils.getStackTrace( ex ) );
        notifier.notify( params.getId(), NotificationLevel.ERROR, "Process failed: " + ex.getMessage(), true );
    }

    public void setParams( MetadataImportParams params )
    {
        this.params = params;
    }
}
