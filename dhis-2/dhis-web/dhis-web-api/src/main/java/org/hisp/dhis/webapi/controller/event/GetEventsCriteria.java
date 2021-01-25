package org.hisp.dhis.webapi.controller.event;



import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.common.AssignedUserSelectionMode;
import org.hisp.dhis.common.OrganisationUnitSelectionMode;
import org.hisp.dhis.event.EventStatus;
import org.hisp.dhis.program.ProgramStatus;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Luciano Fiandesio
 */
@Getter
@Setter
public class GetEventsCriteria
{
    private String program;

    private String programStage;

    private ProgramStatus programStatus;

    private Boolean followUp;

    private String trackedEntityInstance;

    private String orgUnit;

    private OrganisationUnitSelectionMode ouMode;

    private AssignedUserSelectionMode assignedUserMode;

    private String assignedUser;

    private Date startDate;

    private Date endDate;

    private Date dueDateStart;

    private Date dueDateEnd;

    private Date lastUpdated;

    private Date lastUpdatedStartDate;

    private Date lastUpdatedEndDate;

    private String lastUpdatedDuration;

    private EventStatus status;

    private String attributeCc;

    private String attributeCos;

    private boolean skipMeta;

    private Integer page;

    private Integer pageSize;

    private boolean totalPages;

    private Boolean skipPaging;

    private boolean skipEventId;

    private boolean skipHeader = false;

    private Boolean paging;

    private String order;

    private String attachment;

    private boolean includeDeleted = false;

    private String event;

    private Set<String> filter;

    private Set<String> dataElement;

    private boolean includeAllDataElements = false;

    private Map<String, String> parameters;
}
