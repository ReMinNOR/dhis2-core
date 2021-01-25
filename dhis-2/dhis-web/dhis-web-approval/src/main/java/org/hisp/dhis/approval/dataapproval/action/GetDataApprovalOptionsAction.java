package org.hisp.dhis.approval.dataapproval.action;



import static org.hisp.dhis.period.PeriodType.getAvailablePeriodTypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.commons.filter.Filter;
import org.hisp.dhis.commons.filter.FilterUtils;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.period.PeriodType;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

public class GetDataApprovalOptionsAction
    implements Action
{
    @Autowired
    private DataSetService dataSetService;
    
    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private List<DataSet> dataSets;

    public List<DataSet> getDataSets()
    {
        return dataSets;
    }

    private List<PeriodType> periodTypes;

    public List<PeriodType> getPeriodTypes()
    {
        return periodTypes;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        dataSets = new ArrayList<>( dataSetService.getAllDataSets() );
        periodTypes = getAvailablePeriodTypes();

        FilterUtils.filter( dataSets, new DataSetApproveDataFilter() );
        
        Collections.sort( dataSets );
        
        return SUCCESS;
    }

    class DataSetApproveDataFilter
        implements Filter<DataSet>
    {
        @Override
        public boolean retain( DataSet dataSet )
        {
            return dataSet != null && dataSet.isApproveData();
        }        
    }
}
