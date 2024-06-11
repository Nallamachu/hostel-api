package com.msrts.hostel;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.msrts.hostel.constant.Role;
import com.msrts.hostel.model.AddressDto;
import com.msrts.hostel.model.RegisterRequest;
import com.msrts.hostel.service.AuthenticationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class HostelManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(HostelManagementApplication.class, args);
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        JavaTimeModule module = new JavaTimeModule();
        //module.addSerializer(LOCAL_DATETIME_SERIALIZER);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
        return objectMapper.registerModule(module);
    }

    @Bean
    public CommandLineRunner commandLineRunner(AuthenticationService service) {
        return args -> {
            AddressDto address = new AddressDto(
                    null, "YSR", "India", "516227", "Andhra Pradesh", 516227l
            );
            var admin = RegisterRequest.builder()
                    .firstname("Admin")
                    .lastname("Admin")
                    .username("admin@msrts.com")
                    .email("admin@msrts.com")
                    .password("password")
                    .mobile("+91-9164278483")
                    .address(address)
                    .role(Role.ADMIN)
                    .build();
            System.out.println("Admin token: " + service.register(admin, true).getAccessToken());

            var manager = RegisterRequest.builder()
                    .firstname("Manager")
                    .lastname("Manager")
                    .username("manager@msrts.com")
                    .email("manager@msrts.com")
                    .password("password")
                    .mobile("+91-8105596018")
                    .address(address)
                    .role(Role.MANAGER)
                    .referredByCode("ADAD1")
                    .build();
            System.out.println("Manager token: " + service.register(manager, true).getAccessToken());

            var user = RegisterRequest.builder()
                    .firstname("MSR")
                    .lastname("Tech Soft")
                    .username("msrts@msrts.com")
                    .email("msrts@msrts.com")
                    .mobile("+91-8712278483")
                    .password("password")
                    .address(address)
                    .role(Role.USER)
                    .referredByCode("MAMA1")
                    .build();
            System.out.println("User token: " + service.register(user, true).getAccessToken());

        };
    }
}
