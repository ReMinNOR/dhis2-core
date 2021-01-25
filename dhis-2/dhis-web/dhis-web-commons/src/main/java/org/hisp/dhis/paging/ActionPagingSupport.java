package org.hisp.dhis.paging;



import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.system.paging.Paging;
import org.hisp.dhis.util.ContextUtils;

import com.opensymphony.xwork2.Action;

/**
 * @author Quang Nguyen
 */
public abstract class ActionPagingSupport<T>
    implements Action
{
    protected Integer currentPage;

    public Integer getCurrentPage()
    {
        return currentPage;
    }

    public void setCurrentPage( Integer currentPage )
    {
        this.currentPage = currentPage;
    }

    protected Integer pageSize;

    public void setPageSize( Integer pageSize )
    {
        this.pageSize = pageSize;
    }

    protected Paging paging;

    public Paging getPaging()
    {
        return paging;
    }

    protected boolean usePaging = false;

    public boolean isUsePaging()
    {
        return usePaging;
    }

    public void setUsePaging( boolean usePaging )
    {
        this.usePaging = usePaging;
    }

    protected Integer getDefaultPageSize()
    {
        String sessionPageSize = ContextUtils.getCookieValue( ServletActionContext.getRequest(), "pageSize" );

        if ( sessionPageSize != null )
        {
            return Integer.valueOf( sessionPageSize );
        }

        return Paging.DEFAULT_PAGE_SIZE;
    }

    private String getCurrentLink()
    {
        HttpServletRequest request = ServletActionContext.getRequest();

        String baseLink = request.getRequestURI() + "?";

        Enumeration<String> paramNames = request.getParameterNames();

        while ( paramNames.hasMoreElements() )
        {
            String paramName = paramNames.nextElement();
            if ( !paramName.equalsIgnoreCase( "pageSize" ) && !paramName.equalsIgnoreCase( "currentPage" ) )
            {
                String[] values = request.getParameterValues( paramName );
                for ( String value : values )
                {
                    baseLink += paramName + "=" + value + "&";
                }
            }
        }

        return baseLink.substring( 0, baseLink.length() - 1 );
    }

    protected Paging createPaging( Integer totalRecord )
    {
        Paging resultPaging = new Paging( getCurrentLink(), pageSize == null ? getDefaultPageSize() : pageSize );

        resultPaging.setCurrentPage( currentPage == null ? 0 : currentPage );

        resultPaging.setTotal( totalRecord );

        return resultPaging;
    }

    protected List<T> getBlockElement( List<T> elementList, int startPos, int pageSize )
    {
        List<T> returnList;

        int endPos = paging.getEndPos();

        returnList = elementList.subList( startPos, endPos );

        return returnList;
    }
}
