package be.pxl.services;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Discovery Service App
 */
@SpringBootApplication
@EnableEurekaServer
public class DiscoveryServiceApp
{
    public static void main( String[] args )
    {
        new SpringApplicationBuilder(DiscoveryServiceApp.class).run(args);
    }
}
