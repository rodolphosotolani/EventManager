package br.com.rts.eventmanager;

import br.com.rts.eventmanager.config.TimeZoneConfig;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import java.util.TimeZone;
import static org.assertj.core.api.Assertions.assertThat;

class EventManagerApplicationTests {

    @Test
    void contextLoads() {
        ApplicationModules.of(EventManagerApplication.class).verify();
    }

    @Test
    void timezoneShouldBeUtcMinusFour() {
        new TimeZoneConfig().init();
        TimeZone tz = TimeZone.getDefault();
        assertThat(tz.getRawOffset()).isEqualTo(-14400000);
    }

}
