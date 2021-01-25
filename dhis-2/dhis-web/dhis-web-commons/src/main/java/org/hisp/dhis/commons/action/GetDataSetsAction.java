package org.hisp.dhis.commons.action;



import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.paging.ActionPagingSupport;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.util.ContextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Lars Helge Overland
 */
public class GetDataSetsAction
    extends ActionPagingSupport<DataSet>
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }

    private String name;

    public void setName( String name )
    {
        this.name = name;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private List<DataSet> dataSets;

    public List<DataSet> getDataSets()
    {
        return dataSets;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        if ( id != null )
        {
            dataSets = new ArrayList<>(
                dataSetService.getDataSetsByPeriodType( periodService.getPeriodType( id ) ) );
        }
        else if ( name != null )
        {
            dataSets = new ArrayList<>( dataSetService.getDataSetsByPeriodType( periodService
                .getPeriodTypeByName( name ) ) );
        }
        else
        {
            dataSets = new ArrayList<>( dataSetService.getAllDataSets() );
            
            ContextUtils.clearIfNotModified( ServletActionContext.getRequest(), ServletActionContext.getResponse(), dataSets );
        }

        if ( !currentUserService.currentUserIsSuper() )
        {
            User user = currentUserService.getCurrentUser();
            
            if ( user != null && user.getUserCredentials() != null )
            {
                dataSets.retainAll( dataSetService.getUserDataWrite( user ) );
            }            
        }

        Collections.sort( dataSets );

        if ( usePaging )
        {
            this.paging = createPaging( dataSets.size() );

            dataSets = dataSets.subList( paging.getStartPos(), paging.getEndPos() );
        }

        return SUCCESS;
    }
}
