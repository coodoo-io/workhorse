package io.coodoo.workhorse.statistic.entity;

import java.time.LocalDateTime;

/**
 * see https://www.npmjs.com/package/angular2-calendar-heatmap#example-data
 */
public class DurationHeatmapDetail {

    private String name;

    private LocalDateTime date;

    private long value = 0;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

}
