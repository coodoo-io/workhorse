package io.coodoo.workhorse.jobengine.control;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

public class MemoryCount {

    public int size = 60; // TODO jobengineconfig
    public AtomicInteger index;
    public LocalDateTime[] time;
    public AtomicInteger[] queued;
    public AtomicInteger[] finished;
    public AtomicInteger[] failed;

    public MemoryCount() {

        this.index = new AtomicInteger(0);
        this.time = new LocalDateTime[size];
        this.queued = new AtomicInteger[size];
        this.finished = new AtomicInteger[size];
        this.failed = new AtomicInteger[size];

        for (int i = 0; i < size; i++) {
            this.queued[i] = new AtomicInteger(0);
            this.finished[i] = new AtomicInteger(0);
            this.failed[i] = new AtomicInteger(0);
        }
    }

    public void iterate(int queued) {

        // set timestamp before leaving this position
        this.time[this.index.get()] = JobEngineUtil.timestamp();
        this.queued[this.index.get()].set(queued);

        // iterate
        this.index.incrementAndGet();
        if (this.size == this.index.get()) {
            this.index.set(0);
        }

        this.time[this.index.get()] = null;
        this.queued[this.index.get()].set(0);
        this.finished[this.index.get()].set(0);
        this.failed[this.index.get()].set(0);
    }

    public void incrementFinished() {
        if (this.finished[this.index.get()] == null) {
            this.finished[this.index.get()].set(0);
        }
        this.finished[this.index.get()].incrementAndGet();
    }

    public void incrementFailed() {
        if (this.failed[this.index.get()] == null) {
            this.failed[this.index.get()].set(0);
        }
        this.failed[this.index.get()].incrementAndGet();
    }
}
