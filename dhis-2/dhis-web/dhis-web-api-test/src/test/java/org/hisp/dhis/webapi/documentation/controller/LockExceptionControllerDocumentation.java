package org.hisp.dhis.webapi.documentation.controller;



import com.google.common.collect.Lists;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.dataset.LockException;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.webapi.DhisWebSpringTest;
import org.hisp.dhis.webapi.documentation.common.TestUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Viet Nguyen <viet@dhis2.org>
 */
public class LockExceptionControllerDocumentation
    extends DhisWebSpringTest
{
    @Autowired
    private PeriodService periodService;

    @Autowired
    private DataSetService dataSetService;

    @Test
    public void testAddLockException() throws Exception
    {
        MockHttpSession session = getSession( "ALL" );
        PeriodType periodType = periodService.getPeriodTypeByName( "Monthly" );

        Period period = createPeriod( periodType, getDate( 2016, 12, 1 ), getDate( 2016, 12, 31 ) );
        manager.save( period );

        OrganisationUnit orgUnit = createOrganisationUnit( 'B' );
        manager.save( orgUnit );

        DataSet dataSet = createDataSet( 'A', periodType );
        dataSet.addOrganisationUnit( orgUnit );
        manager.save( dataSet );

        String postUrl = "/lockExceptions?ou=" + orgUnit.getUid() + "&pe=201612&ds=" + dataSet.getUid();

        mvc.perform( post( postUrl ).session( session ).accept( TestUtils.APPLICATION_JSON_UTF8 ) )
            .andExpect( status().is( 201 ) )
            .andExpect( content().contentTypeCompatibleWith( TestUtils.APPLICATION_JSON_UTF8 ) )
            .andDo( documentPrettyPrint( "lockExceptions/add" ) );
    }

    @Test
    public void testDeleteLockException() throws Exception
    {
        MockHttpSession session = getSession( "ALL" );
        PeriodType periodType = periodService.getPeriodTypeByName( "Monthly" );

        Period period = createPeriod( periodType, getDate( 2016, 12, 1 ), getDate( 2016, 12, 31 ) );
        manager.save( period );

        OrganisationUnit orgUnit = createOrganisationUnit( 'B' );
        manager.save( orgUnit );

        DataSet dataSet = createDataSet( 'A', periodType );
        dataSet.addOrganisationUnit( orgUnit );
        manager.save( dataSet );

        LockException lockException = new LockException( period, orgUnit, dataSet );
        dataSetService.addLockException( lockException );

        String deleteUrl = "/lockExceptions?ou=" + orgUnit.getUid() + "&pe=201612&ds=" + dataSet.getUid();

        mvc.perform( delete( deleteUrl ).session( session ).accept( TestUtils.APPLICATION_JSON_UTF8 ) )
            .andExpect( status().isNoContent() )
            .andDo( documentPrettyPrint( "lockExceptions/delete" )
            );
    }

    @Test
    public void testGetLockException() throws Exception
    {
        MockHttpSession session = getSession( "ALL" );
        PeriodType periodType = periodService.getPeriodTypeByName( "Monthly" );

        Period period = createPeriod( periodType, getDate( 2016, 12, 1 ), getDate( 2016, 12, 31 ) );
        manager.save( period );

        OrganisationUnit orgUnit = createOrganisationUnit( 'B' );
        manager.save( orgUnit );

        DataSet dataSet = createDataSet( 'A', periodType );
        dataSet.addOrganisationUnit( orgUnit );
        manager.save( dataSet );

        LockException lockException = new LockException( period, orgUnit, dataSet );
        dataSetService.addLockException( lockException );

        String getUrl = "/lockExceptions?filter=organisationUnit.id:eq:" + orgUnit.getUid() + "&filter=period:eq:201612&filter=dataSet.id:eq:" + dataSet.getUid();

        Lists.newArrayList(
            fieldWithPath( "period" ).description( "Property" ),
            fieldWithPath( "organisationUnit" ).description( "Property" ),
            fieldWithPath( "dataSet" ).description( "Property" )
        );

        mvc.perform( get( getUrl ).session( session ).accept( TestUtils.APPLICATION_JSON_UTF8 ) )
            .andExpect( status().is( 200 ) )
            .andDo( documentPrettyPrint( "lockExceptions/get" )
            ).andReturn();
    }
}
