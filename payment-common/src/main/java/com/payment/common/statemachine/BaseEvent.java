package com.payment.common.statemachine;

/**
 * 事件基类接口
 *
 * @author Payment Platform Team
 * @since 1.0.0
 */
public interface BaseEvent {
    /**
     * 获取事件码
     */
    String getCode();

    /**
     * 获取事件描述
     */
    String getDescription();
}
