package be.pxl.services;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 *  Review Service App
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ReviewServiceApp
{
    public static void main( String[] args )
    {
        SpringApplication.run(ReviewServiceApp.class, args);
    }
}
