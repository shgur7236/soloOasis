package com.real.realoasis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan(basePackages = {"com.real.*"})
@SpringBootApplication
public class RealoasisApplication {

    public static void main(String[] args) {
        SpringApplication.run(RealoasisApplication.class, args);
    }

}
