package be.vlaanderen.vip.mock.magdaservice.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;

@Configuration
public class JacksonConfig {

    @Bean
    public Module javaTimeModule() {
        return new JavaTimeModule()
                .addSerializer(LocalDate.class, new LocalDateSerializer(ISO_LOCAL_DATE))
                .addDeserializer(LocalDate.class, new LocalDateDeserializer(ISO_LOCAL_DATE))
                .addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(ISO_LOCAL_DATE_TIME))
                .addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(ISO_LOCAL_DATE_TIME))
                .addSerializer(OffsetDateTime.class, new JsonSerializer<>() {
                    @Override
                    public void serialize(OffsetDateTime offsetDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                        jsonGenerator.writeString(offsetDateTime.format(ISO_OFFSET_DATE_TIME));
                    }
                })
                .addDeserializer(OffsetDateTime.class, new JsonDeserializer<>() {
                    @Override
                    public OffsetDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
                        return OffsetDateTime.parse(jsonParser.getText(), ISO_OFFSET_DATE_TIME);
                    }
                });
    }

    @Bean
    public Module jdk8Module() {
        return new Jdk8Module();
    }

    @Bean
    public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder(List<Module> modules) {
        return new Jackson2ObjectMapperBuilder()
                .modules(modules)
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .featuresToEnable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
                .featuresToEnable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
    }

    @Bean
    public MappingJackson2HttpMessageConverter jackson2HttpMessageConverter(Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder) {
        return new MappingJackson2HttpMessageConverter(jackson2ObjectMapperBuilder.build());
    }

}
