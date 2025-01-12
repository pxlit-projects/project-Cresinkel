package be.pxl.services;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * GatewayServiceApp
 */

@SpringBootApplication
@EnableDiscoveryClient
public class GatewayServiceApp
{
    public static void main( String[] args )
    {
        SpringApplication.run(GatewayServiceApp.class, args);
    }
}
