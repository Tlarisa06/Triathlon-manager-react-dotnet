package ro.mpp.triathlon.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("ro.mpp.triathlon")
public class RestStartServer {
    public static void main(String[] args) {
        SpringApplication.run(RestStartServer.class, args);
    }
}
