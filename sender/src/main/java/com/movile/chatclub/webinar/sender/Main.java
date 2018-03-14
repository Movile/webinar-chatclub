package com.movile.chatclub.webinar.sender;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@EnableAutoConfiguration
@ComponentScan("com.movile.chatclub.webinar.sender")
public class Main {

    public static void main(String[] args) {
        new SpringApplicationBuilder(Main.class).bannerMode(Banner.Mode.OFF).run(args);
    }

}

