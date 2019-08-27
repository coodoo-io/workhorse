package io.coodoo.workhorse.statistic.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * see https://www.npmjs.com/package/angular2-calendar-heatmap#example-data
 */
public class DurationHeatmap {

    private String date;

    private long total = 0;

    private List<DurationHeatmapDetail> details = new ArrayList<>();

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<DurationHeatmapDetail> getDetails() {
        return details;
    }

    public void setDetails(List<DurationHeatmapDetail> details) {
        this.details = details;
    }

}
