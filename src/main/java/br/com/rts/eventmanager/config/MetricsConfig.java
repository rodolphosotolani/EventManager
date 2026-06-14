package br.com.rts.eventmanager.config;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.config.MeterFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class MetricsConfig {

    @Bean
    public MeterFilter prometheusTagsMeterFilter() {
        return new MeterFilter() {
            @Override
            public Meter.Id map(Meter.Id id) {
                if ("spring.security.authorizations".equals(id.getName())) {
                    List<Tag> tagsWithoutModuleKey = id.getTags().stream()
                            .filter(tag -> !tag.getKey().equals("module.key") && !tag.getKey().equals("module_key"))
                            .toList();
                    return id.withTags(tagsWithoutModuleKey);
                }
                return id;
            }
        };
    }
}
