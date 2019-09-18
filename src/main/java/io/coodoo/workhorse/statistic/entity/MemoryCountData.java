package io.coodoo.workhorse.statistic.entity;

import java.time.LocalDateTime;

public class MemoryCountData {

    private LocalDateTime time;
    private int queued;
    private int finished;
    private int failed;

    public MemoryCountData() {}

    public MemoryCountData(LocalDateTime time, int queued, int finished, int failed) {
        this.time = time;
        this.queued = queued;
        this.finished = finished;
        this.failed = failed;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public int getQueued() {
        return queued;
    }

    public void setQueued(int queued) {
        this.queued = queued;
    }

    public int getFinished() {
        return finished;
    }

    public void setFinished(int finished) {
        this.finished = finished;
    }

    public int getFailed() {
        return failed;
    }

    public void setFailed(int failed) {
        this.failed = failed;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("MemoryCountData [time=");
        builder.append(time);
        builder.append(", queued=");
        builder.append(queued);
        builder.append(", finished=");
        builder.append(finished);
        builder.append(", failed=");
        builder.append(failed);
        builder.append("]");
        return builder.toString();
    }

}
