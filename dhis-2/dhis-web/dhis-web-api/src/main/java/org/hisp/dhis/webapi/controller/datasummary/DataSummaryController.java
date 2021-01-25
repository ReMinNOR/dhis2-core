package org.hisp.dhis.webapi.controller.datasummary;



import org.hisp.dhis.common.DhisApiVersion;
import org.hisp.dhis.datastatistics.DataStatisticsService;
import org.hisp.dhis.datasummary.DataSummary;
import org.hisp.dhis.webapi.mvc.annotation.ApiVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * dataSummary endpoint to access System Statistics
 *
 * @author Joao Antunes
 */
@Controller
@RequestMapping( value = DataSummaryController.RESOURCE_PATH )
@ApiVersion( { DhisApiVersion.DEFAULT, DhisApiVersion.ALL } )
public class DataSummaryController
{
    public static final String RESOURCE_PATH = "/dataSummary";

    @Autowired
    private DataStatisticsService dataStatisticsService;

    @GetMapping
    @PreAuthorize( "hasRole('ALL') or hasRole('F_PERFORM_MAINTENANCE')" )
    public @ResponseBody DataSummary getStatistics()
    {
        return dataStatisticsService.getSystemStatisticsSummary();
    }
}
