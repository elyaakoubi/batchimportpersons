package com.example.tpspbatch001;

import ma.ensa.config.AppConfig;
import ma.ensa.entities.Personne;
import ma.ensa.repositories.PersonRepo;
import org.springframework.batch.core.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@Import(AppConfig.class)
public class Tpspbatch001Application implements CommandLineRunner
{

    @Override
    public void run(String... args) throws Exception {
    }

    public static void main(String[] args) {
        SpringApplication.run(Tpspbatch001Application.class, args);
    }


}
