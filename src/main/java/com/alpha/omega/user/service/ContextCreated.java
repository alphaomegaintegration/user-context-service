package com.alpha.omega.user.service;

import com.alpha.omega.user.model.Context;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;

@Getter
public class ContextCreated extends ApplicationEvent {

    Context context;

    public ContextCreated(Object source, Context context) {
        super(source);
        this.context = context;
    }

    public ContextCreated(Object source, Clock clock, Context context) {
        super(source, clock);
        this.context = context;
    }
}
