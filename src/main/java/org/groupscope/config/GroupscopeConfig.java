package org.groupscope.config;


import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableJpaRepositories("org.groupscope.*")
@ComponentScan(basePackages = { "org.groupscope.*"})
@EntityScan("org.groupscope.*")
@EnableScheduling
public class GroupscopeConfig {

}
