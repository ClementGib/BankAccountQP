package com.cdx.bas.application.config;

import com.cdx.bas.domain.testing.Generated;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import jakarta.ws.rs.Produces;

import java.time.Clock;

@Generated
@ApplicationScoped
public class Producers {

    @Produces
    @Default
    public Clock produceClock(){
        return Clock.systemDefaultZone();
    }

}
