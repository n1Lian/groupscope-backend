package org.groupscope.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.lang.reflect.Field;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
@SpringJUnitConfig
@ExtendWith(SpringExtension.class)
@PropertySource("classpath:application.properties")

public class JwtProviderTest {

    /*private JwtProvider testJwtProvider;
    @Bean
    @Test
    public void generateTokenTest() throws NoSuchFieldException, IllegalAccessException {
        final JwtProvider jwtProvider = new JwtProvider();

        System.out.println("test "+testJwtProvider.getJwtSecret());


        /*long jwtLifetime = Duration.ofMillis(jwtTokenDurationMs).toDays();
        Date date = Date.from(LocalDate.now().plusDays(jwtLifetime).atStartOfDay(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .setSubject(login)
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(date)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();*/
        //System.out.println(testJwtProvider.jwtSecret);
        /*System.out.println(testJwtProvider.jwtTokenDurationMs);
        System.out.println(valueWorks);
        System.out.println(jwtTokenDurationMs);*/
        // врменные переменные, в алгритм вставляю с 29 строки, вставляю jwttokendyration,
        // просчитываю все по алгоритму, сравниваю что выдаст сравнивание с тем что я посчитаю и то
        // что выдаст метод
    }

