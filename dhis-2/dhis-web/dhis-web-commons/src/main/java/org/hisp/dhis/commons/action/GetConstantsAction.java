package org.hisp.dhis.commons.action;



import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.paging.ActionPagingSupport;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */
public class GetConstantsAction
    extends ActionPagingSupport<Constant>
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ConstantService constantService;

    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    public void setKey( String key )
    {
        this.key = key;
    }

    private List<Constant> constants;

    public List<Constant> getConstants()
    {
        return constants;
    }

    private String key;

    public String getKey()
    {
        return key;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
    {
        if ( isNotBlank( key ) ) // Filter on key only if set
        {
            this.paging = createPaging( constantService.getConstantCountByName( key ) );

            constants = new ArrayList<>( constantService.getConstantsBetweenByName( key, paging.getStartPos(),
                paging.getPageSize() ) );
        }
        else
        {
            this.paging = createPaging( constantService.getConstantCount() );

            constants = new ArrayList<>( constantService.getConstantsBetween( paging.getStartPos(),
                paging.getPageSize() ) );
        }

        Collections.sort( constants );

        return SUCCESS;
    }

}
