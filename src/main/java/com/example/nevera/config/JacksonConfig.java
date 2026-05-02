package com.example.nevera.config;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.StdSerializer;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Configuration
public class JacksonConfig {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")
                    .withZone(ZoneId.of("Asia/Seoul"));

    @Bean
    public JsonMapperBuilderCustomizer offsetDateTimeCustomizer() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(OffsetDateTime.class, new StdSerializer<>(OffsetDateTime.class) {
            @Override
            public void serialize(OffsetDateTime value, JsonGenerator gen, SerializationContext ctx) {
                gen.writeString(FORMATTER.format(value.atZoneSameInstant(ZoneId.of("Asia/Seoul"))));
            }
        });
        return builder -> builder.addModule(module);
    }
}
