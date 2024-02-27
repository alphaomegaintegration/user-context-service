package com.alpha.omega.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UserContextServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserContextServiceApplication.class, args);
        try{

        } catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

}
