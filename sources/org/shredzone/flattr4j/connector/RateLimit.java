package org.shredzone.flattr4j.connector;

import java.io.Serializable;
import java.util.Date;

public class RateLimit implements Serializable {
    private static final long serialVersionUID = -4217480425094824497L;
    private Long current;
    private Long limit;
    private Long remaining;
    private Date reset;

    public RateLimit(RateLimit limit) {
        this.limit = limit.limit;
        this.remaining = limit.remaining;
        this.current = limit.current;
        this.reset = limit.reset;
    }

    public RateLimit(FlattrObject data) {
        setLimit(Long.valueOf(data.getLong("hourly_limit")));
        setRemaining(Long.valueOf(data.getLong("remaining_hits")));
        setCurrent(Long.valueOf(data.getLong("current_hits")));
        setReset(data.getDate("reset_time_in_seconds"));
    }

    public Long getLimit() {
        return this.limit;
    }

    public void setLimit(Long limit) {
        this.limit = limit;
    }

    public Long getRemaining() {
        return this.remaining;
    }

    public void setRemaining(Long remaining) {
        this.remaining = remaining;
    }

    public Long getCurrent() {
        return this.current;
    }

    public void setCurrent(Long current) {
        this.current = current;
    }

    public Date getReset() {
        return this.reset;
    }

    public void setReset(Date reset) {
        this.reset = reset;
    }
}
