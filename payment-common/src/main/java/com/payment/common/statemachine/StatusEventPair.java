package com.payment.common.statemachine;

import java.util.Objects;

/**
 * 状态事件对，指定的状态只能接受指定的事件
 *
 * @author Payment Platform Team
 * @since 1.0.0
 */
public class StatusEventPair<S extends BaseStatus, E extends BaseEvent> {
    
    /**
     * 指定的状态
     */
    private final S status;
    
    /**
     * 可接受的事件
     */
    private final E event;

    public StatusEventPair(S status, E event) {
        this.status = status;
        this.event = event;
    }

    public S getStatus() {
        return status;
    }

    public E getEvent() {
        return event;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        StatusEventPair<?, ?> that = (StatusEventPair<?, ?>) obj;
        return Objects.equals(status, that.status) && Objects.equals(event, that.event);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, event);
    }

    @Override
    public String toString() {
        return "StatusEventPair{" +
                "status=" + status +
                ", event=" + event +
                '}';
    }
}
