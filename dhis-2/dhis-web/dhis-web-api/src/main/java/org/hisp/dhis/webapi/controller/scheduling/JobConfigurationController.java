package org.hisp.dhis.webapi.controller.scheduling;



import org.hisp.dhis.feedback.ErrorCode;
import org.hisp.dhis.feedback.ErrorMessage;
import org.hisp.dhis.feedback.ErrorReport;
import org.hisp.dhis.feedback.ObjectReport;
import org.hisp.dhis.scheduling.JobConfiguration;
import org.hisp.dhis.scheduling.JobConfigurationService;
import org.hisp.dhis.scheduling.JobStatus;
import org.hisp.dhis.scheduling.SchedulingManager;
import org.hisp.dhis.schema.Property;
import org.hisp.dhis.schema.descriptors.JobConfigurationSchemaDescriptor;
import org.hisp.dhis.webapi.controller.AbstractCrudController;
import org.hisp.dhis.webapi.webdomain.JobTypes;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Simple controller for API endpoints
 *
 * @author Henning Håkonsen
 */
@RestController
@RequestMapping( value = JobConfigurationSchemaDescriptor.API_ENDPOINT )
public class JobConfigurationController
    extends AbstractCrudController<JobConfiguration>
{
    private final JobConfigurationService jobConfigurationService;

    private final SchedulingManager schedulingManager;

    public JobConfigurationController( JobConfigurationService jobConfigurationService,
        SchedulingManager schedulingManager )
    {
        this.jobConfigurationService = jobConfigurationService;
        this.schedulingManager = schedulingManager;
    }

    @RequestMapping( value = "/jobTypesExtended", method = RequestMethod.GET, produces = { "application/json", "application/javascript" } )
    public @ResponseBody Map<String, Map<String, Property>> getJobTypesExtended()
    {
        return jobConfigurationService.getJobParametersSchema();
    }

    @GetMapping( value = "/jobTypes", produces = "application/json" )
    public JobTypes getJobTypeInfo()
    {
        return new JobTypes( jobConfigurationService.getJobTypeInfo() );
    }

    @RequestMapping( value = "{uid}/execute", method = RequestMethod.GET, produces = { "application/json", "application/javascript" } )
    public ObjectReport executeJobConfiguration( @PathVariable( "uid" ) String uid )
    {
        JobConfiguration jobConfiguration = jobConfigurationService.getJobConfigurationByUid( uid );

        ObjectReport objectReport = new ObjectReport( JobConfiguration.class, 0 );

        boolean success = schedulingManager.executeJob( jobConfiguration );

        if ( !success )
        {
            objectReport.addErrorReport( new ErrorReport( JobConfiguration.class, new ErrorMessage( ErrorCode.E7006, jobConfiguration.getName() ) ) );
        }

        return objectReport;
    }

    @Override
    protected void postPatchEntity( JobConfiguration jobConfiguration )
    {
        if ( !jobConfiguration.isEnabled() )
        {
            jobConfiguration.setJobStatus( JobStatus.DISABLED );
        }

        jobConfigurationService.addJobConfiguration( jobConfiguration );

        jobConfigurationService.refreshScheduling( jobConfiguration );
    }
}
