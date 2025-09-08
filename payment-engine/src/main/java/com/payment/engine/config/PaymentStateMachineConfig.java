package com.payment.engine.config;

import com.payment.common.statemachine.StateMachine;
import com.payment.engine.domain.PaymentEvents;
import com.payment.engine.domain.PaymentStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentStateMachineConfig {

    @Bean
    public StateMachine<PaymentStatus, PaymentEvents> paymentStateMachine() {
        StateMachine<PaymentStatus, PaymentEvents> sm = new StateMachine<>();
        // INIT --START_PAY--> PAYING
        sm.accept(PaymentStatus.INIT, PaymentEvents.START_PAY, PaymentStatus.PAYING);
        // PAYING --CHANNEL_SUCCESS--> PAID (先简化：渠道成功即成功)
        sm.accept(PaymentStatus.PAYING, PaymentEvents.CHANNEL_SUCCESS, PaymentStatus.PAID);
        // PAYING --CHANNEL_FAIL--> FAILED
        sm.accept(PaymentStatus.PAYING, PaymentEvents.CHANNEL_FAIL, PaymentStatus.FAILED);
        return sm;
    }
}


