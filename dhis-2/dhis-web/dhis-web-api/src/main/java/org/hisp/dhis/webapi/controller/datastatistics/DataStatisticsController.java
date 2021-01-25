package org.hisp.dhis.webapi.controller.datastatistics;



import org.hisp.dhis.analytics.SortOrder;
import org.hisp.dhis.datastatistics.AggregatedStatistics;
import org.hisp.dhis.datastatistics.DataStatisticsEvent;
import org.hisp.dhis.datastatistics.DataStatisticsEventType;
import org.hisp.dhis.datastatistics.DataStatisticsService;
import org.hisp.dhis.datastatistics.EventInterval;
import org.hisp.dhis.datastatistics.FavoriteStatistics;
import org.hisp.dhis.dxf2.webmessage.WebMessageException;
import org.hisp.dhis.dxf2.webmessage.WebMessageUtils;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.util.DateUtils;
import org.hisp.dhis.util.ObjectUtils;
import org.hisp.dhis.webapi.mvc.annotation.ApiVersion;
import org.hisp.dhis.common.DhisApiVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletResponse;

import static java.util.Calendar.DATE;
import static java.util.Calendar.MILLISECOND;
import static org.hisp.dhis.webapi.utils.ContextUtils.setNoStore;

import java.util.Date;
import java.util.List;

/**
 * @author Yrjan A. F. Fraschetti
 * @author Julie Hill Roa
 */
@Controller
@ApiVersion( { DhisApiVersion.DEFAULT, DhisApiVersion.ALL } )
@RequestMapping( DataStatisticsController.RESOURCE_PATH )
public class DataStatisticsController
{
    public static final String RESOURCE_PATH = "/dataStatistics";

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private DataStatisticsService dataStatisticsService;

    @PostMapping
    @ResponseStatus( HttpStatus.CREATED )
    public void saveEvent( @RequestParam DataStatisticsEventType eventType, String favorite )
    {
        Date timestamp = new Date();
        String username = currentUserService.getCurrentUsername();

        DataStatisticsEvent event = new DataStatisticsEvent( eventType, timestamp, username, favorite );
        dataStatisticsService.addEvent( event );
    }

    @GetMapping
    public @ResponseBody List<AggregatedStatistics> getReports( @RequestParam Date startDate,
        @RequestParam Date endDate, @RequestParam EventInterval interval, HttpServletResponse response ) throws WebMessageException
    {
        if ( startDate.after( endDate ) )
        {
            throw new WebMessageException( WebMessageUtils.conflict( "Start date is after end date" ) );
        }

        // The endDate is arriving as: "2019-09-28". After the conversion below it will become: "2019-09-28 23:59:59.999"
        endDate = DateUtils.calculateDateFrom( endDate, 1, DATE );
        endDate = DateUtils.calculateDateFrom( endDate, -1, MILLISECOND );

        setNoStore( response );
        return dataStatisticsService.getReports( startDate, endDate, interval );
    }

    @GetMapping( "/favorites" )
    public @ResponseBody List<FavoriteStatistics> getTopFavorites( @RequestParam DataStatisticsEventType eventType,
        @RequestParam( required = false ) Integer pageSize, @RequestParam( required = false ) SortOrder sortOrder,
        @RequestParam( required = false ) String username, HttpServletResponse response )
        throws WebMessageException
    {
        pageSize = ObjectUtils.firstNonNull( pageSize, 20 );
        sortOrder = ObjectUtils.firstNonNull( sortOrder, SortOrder.DESC );

        setNoStore( response );
        return dataStatisticsService.getTopFavorites( eventType, pageSize, sortOrder, username );
    }

    @GetMapping( "/favorites/{uid}" )
    public @ResponseBody FavoriteStatistics getFavoriteStatistics( @PathVariable( "uid" ) String uid )
    {
        return dataStatisticsService.getFavoriteStatistics( uid );
    }
    
    @PreAuthorize( "hasRole('ALL')" )
    @ResponseStatus( HttpStatus.CREATED )
    @PostMapping( "/snapshot" )
    public void saveSnapshot()
    {
        dataStatisticsService.saveDataStatisticsSnapshot();
    }
}
