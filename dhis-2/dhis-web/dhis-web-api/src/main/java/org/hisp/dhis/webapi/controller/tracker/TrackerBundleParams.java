package org.hisp.dhis.webapi.controller.tracker;



import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.tracker.domain.Enrollment;
import org.hisp.dhis.tracker.domain.Event;
import org.hisp.dhis.tracker.domain.Relationship;
import org.hisp.dhis.tracker.domain.TrackedEntity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Maps the Tracker import payload
 *
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize( converter = TrackerBundleParamsConverter.class )
public class TrackerBundleParams
{
    /**
     * Tracked entities to import.
     */
    @JsonProperty
    @Builder.Default
    private List<TrackedEntity> trackedEntities = new ArrayList<>();

    /**
     * Enrollments to import.
     */
    @JsonProperty
    @Builder.Default
    private List<Enrollment> enrollments = new ArrayList<>();

    /**
     * Events to import.
     */
    @JsonProperty
    @Builder.Default
    private List<Event> events = new ArrayList<>();

    /**
     * Relationships to import.
     */
    @JsonProperty
    @Builder.Default
    private List<Relationship> relationships = new ArrayList<>();
}
