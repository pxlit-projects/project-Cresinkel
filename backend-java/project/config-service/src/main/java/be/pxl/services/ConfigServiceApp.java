package be.pxl.services;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * ConfigServiceApp
 */

@SpringBootApplication
@EnableConfigServer
public class ConfigServiceApp {
    public static void main( String[] args )
    {

        new SpringApplicationBuilder(ConfigServiceApp.class).run(args);
    }
}
