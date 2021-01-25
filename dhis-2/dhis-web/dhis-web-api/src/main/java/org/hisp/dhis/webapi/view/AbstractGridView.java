package org.hisp.dhis.webapi.view;



import org.hisp.dhis.common.Grid;
import org.hisp.dhis.common.GridHeader;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.NameableObject;
import org.hisp.dhis.hibernate.HibernateProxyUtils;
import org.hisp.dhis.system.grid.ListGrid;
import org.hisp.dhis.system.util.PredicateUtils;
import org.hisp.dhis.system.util.ReflectionUtils;
import org.hisp.dhis.webapi.webdomain.WebMetadata;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public abstract class AbstractGridView extends AbstractView
{
    protected abstract void renderGrids( List<Grid> grids, HttpServletResponse response ) throws Exception;

    @Override
    protected void renderMergedOutputModel( Map<String, Object> model, HttpServletRequest request, HttpServletResponse response ) throws Exception
    {
        Object object = model.get( "model" );

        List<Grid> grids = new ArrayList<>();

        if ( WebMetadata.class.isAssignableFrom( object.getClass() ) )
        {
            WebMetadata metadata = (WebMetadata) object;
            Collection<Field> fields = ReflectionUtils.collectFields( WebMetadata.class, PredicateUtils.idObjectCollections );

            for ( Field field : fields )
            {
                List<IdentifiableObject> identifiableObjects = ReflectionUtils.invokeGetterMethod( field.getName(), metadata );

                if ( identifiableObjects == null || identifiableObjects.isEmpty() )
                {
                    continue;
                }

                Grid grid = new ListGrid();
                grid.setTitle( HibernateProxyUtils.getRealClass( identifiableObjects.get( 0 ) ).getSimpleName() + "s" );

                boolean nameable = false;

                grid.addHeader( new GridHeader( "UID", false, false ) );
                grid.addHeader( new GridHeader( "Name", false, false ) );

                if ( NameableObject.class.isAssignableFrom( HibernateProxyUtils.getRealClass( HibernateProxyUtils.getRealClass( object ) ) ) )
                {
                    grid.addHeader( new GridHeader( "ShortName", false, false ) );
                    nameable = true;
                }

                grid.addHeader( new GridHeader( "Code", false, false ) );

                for ( IdentifiableObject identifiableObject : identifiableObjects )
                {
                    grid.addRow();
                    grid.addValue( identifiableObject.getUid() );
                    grid.addValue( identifiableObject.getName() );

                    if ( nameable )
                    {
                        grid.addValue( ((NameableObject) identifiableObject).getShortName() );
                    }

                    grid.addValue( identifiableObject.getCode() );
                }

                grids.add( grid );
            }
        }
        else
        {
            IdentifiableObject identifiableObject = (IdentifiableObject) object;

            Grid grid = new ListGrid();
            grid.setTitle( HibernateProxyUtils.getRealClass( identifiableObject ).getSimpleName() );
            grid.addEmptyHeaders( 2 );

            grid.addRow().addValue( "UID" ).addValue( identifiableObject.getUid() );
            grid.addRow().addValue( "Name" ).addValue( identifiableObject.getName() );

            if ( NameableObject.class.isAssignableFrom( HibernateProxyUtils.getRealClass( identifiableObject ) ) )
            {
                grid.addRow().addValue( "ShortName" ).addValue( ((NameableObject) identifiableObject).getShortName() );
                grid.addRow().addValue( "Description" ).addValue( ((NameableObject) identifiableObject).getDescription() );
            }

            grid.addRow().addValue( "Code" ).addValue( identifiableObject.getCode() );

            grids.add( grid );
        }

        renderGrids( grids, response );
    }
}
