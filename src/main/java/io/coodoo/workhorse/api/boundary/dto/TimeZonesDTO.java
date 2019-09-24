package io.coodoo.workhorse.api.boundary.dto;

import java.util.List;

public class TimeZonesDTO {

    private List<String> timeZones;

    private String systemDefaultTimeZone;

    public List<String> getTimeZones() {
        return timeZones;
    }

    public void setTimeZones(List<String> timeZones) {
        this.timeZones = timeZones;
    }

    public String getSystemDefaultTimeZone() {
        return systemDefaultTimeZone;
    }

    public void setSystemDefaultTimeZone(String systemDefaultTimeZone) {
        this.systemDefaultTimeZone = systemDefaultTimeZone;
    }

}
