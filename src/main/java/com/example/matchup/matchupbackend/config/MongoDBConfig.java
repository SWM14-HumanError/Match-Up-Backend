package com.example.matchup.matchupbackend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@RequiredArgsConstructor
@EnableMongoAuditing
@EnableMongoRepositories(basePackages = "com.example.matchup.matchupbackend.mongodb")
public class MongoDBConfig {

}
