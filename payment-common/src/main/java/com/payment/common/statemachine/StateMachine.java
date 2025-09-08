package com.payment.common.statemachine;

import java.util.HashMap;
import java.util.Map;

/**
 * 状态机
 *
 * @author Payment Platform Team
 * @since 1.0.0
 */
public class StateMachine<S extends BaseStatus, E extends BaseEvent> {
    
    private final Map<StatusEventPair<S, E>, S> statusEventMap = new HashMap<>();

    /**
     * 只接受指定的当前状态下，指定的事件触发，可以到达的指定目标状态
     */
    public void accept(S sourceStatus, E event, S targetStatus) {
        if (sourceStatus == null || event == null || targetStatus == null) {
            throw new IllegalArgumentException("状态和事件不能为空");
        }
        statusEventMap.put(new StatusEventPair<>(sourceStatus, event), targetStatus);
    }

    /**
     * 通过源状态和事件，获取目标状态
     */
    public S getTargetStatus(S sourceStatus, E event) {
        if (sourceStatus == null || event == null) {
            return null;
        }
        return statusEventMap.get(new StatusEventPair<>(sourceStatus, event));
    }

    /**
     * 检查状态转换是否有效
     */
    public boolean isValidTransition(S sourceStatus, E event) {
        return getTargetStatus(sourceStatus, event) != null;
    }

    /**
     * 获取所有可能的状态转换
     */
    public Map<StatusEventPair<S, E>, S> getAllTransitions() {
        return new HashMap<>(statusEventMap);
    }

    /**
     * 清空所有状态转换规则
     */
    public void clear() {
        statusEventMap.clear();
    }

    /**
     * 获取状态转换规则数量
     */
    public int size() {
        return statusEventMap.size();
    }
}
