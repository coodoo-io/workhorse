package io.coodoo.workhorse.api.boundary;

import java.time.ZoneId;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.coodoo.workhorse.api.boundary.dto.TimeZonesDTO;
import io.coodoo.workhorse.config.boundary.JobEngineConfigService;
import io.coodoo.workhorse.config.entity.Config;

/**
 * Rest interface for the workhorse configuration
 * 
 * @author coodoo GmbH (coodoo.io)
 */
@Path("/workhorse/config")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class JobEngineConfigResource {

    @Inject
    JobEngineConfigService jobEngineConfigService;

    @GET
    @Path("/")
    public Config getConfig() {
        return jobEngineConfigService.getConfig();
    }

    @GET
    @Path("/timezones")
    public TimeZonesDTO getTimeZones() {
        TimeZonesDTO timeZonesDTO = new TimeZonesDTO();
        timeZonesDTO.setSystemDefaultTimeZone(ZoneId.systemDefault().getId());
        timeZonesDTO.setTimeZones(ZoneId.getAvailableZoneIds().stream().sorted().collect(Collectors.toList()));
        return timeZonesDTO;
    }

    @PUT
    @Path("/")
    public Config updateConfig(Config config) {
        return jobEngineConfigService.updateConfig(config.getTimeZone(), config.getJobQueuePollerInterval(), config.getJobQueueMax(), config.getJobQueueMin(),
                        config.getZombieRecognitionTime(), config.getZombieCureStatus(), config.getDaysUntilStatisticMinutesDeletion(),
                        config.getDaysUntilStatisticHoursDeletion(), config.getLogChange(), config.getLogTimeFormatter(), config.getLogInfoMarker(),
                        config.getLogWarnMarker(), config.getLogErrorMarker());
    }

}
