package com.vv.recipe;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.context.annotation.Configuration;


@ComponentScan({"recipes.vv"})
@EntityScan("recipes.vv.model")
@EnableJpaRepositories("recipes.vv.repository")
@Configuration
public class DataConfig {
}
