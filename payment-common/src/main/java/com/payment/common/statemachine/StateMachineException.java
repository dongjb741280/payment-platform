package com.payment.common.statemachine;

/**
 * 状态机异常
 *
 * @author Payment Platform Team
 * @since 1.0.0
 */
public class StateMachineException extends RuntimeException {

    private final BaseStatus currentStatus;
    private final BaseEvent event;
    private final String message;

    public StateMachineException(BaseStatus currentStatus, BaseEvent event, String message) {
        super(String.format("状态转换失败: 当前状态=%s, 事件=%s, 原因=%s", 
                currentStatus != null ? currentStatus.getCode() : "null",
                event != null ? event.getCode() : "null", 
                message));
        this.currentStatus = currentStatus;
        this.event = event;
        this.message = message;
    }

    public StateMachineException(BaseStatus currentStatus, BaseEvent event, String message, Throwable cause) {
        super(String.format("状态转换失败: 当前状态=%s, 事件=%s, 原因=%s", 
                currentStatus != null ? currentStatus.getCode() : "null",
                event != null ? event.getCode() : "null", 
                message), cause);
        this.currentStatus = currentStatus;
        this.event = event;
        this.message = message;
    }

    public BaseStatus getCurrentStatus() {
        return currentStatus;
    }

    public BaseEvent getEvent() {
        return event;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
