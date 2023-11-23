package org.groupscope;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "org.groupscope.security.entity")
public class Program {
    public static void main(String[] args) {
        SpringApplication.run(Program.class, args);
    }
}