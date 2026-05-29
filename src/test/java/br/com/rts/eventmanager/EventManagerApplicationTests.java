package br.com.rts.eventmanager;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class EventManagerApplicationTests {

    @Test
    void contextLoads() {
        ApplicationModules.of(EventManagerApplication.class).verify();
    }

}
