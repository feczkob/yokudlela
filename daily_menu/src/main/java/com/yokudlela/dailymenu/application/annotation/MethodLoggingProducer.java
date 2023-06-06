package com.yokudlela.dailymenu.application.annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

public class MethodLoggingProducer {

    @Produces
    public Logger producer(InjectionPoint ip){
        return LoggerFactory.getLogger(ip.getMember().getDeclaringClass().getName());
    }
}
