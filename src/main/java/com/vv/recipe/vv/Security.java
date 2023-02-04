package com.vv.recipe.vv;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import com.vv.recipe.vv.service.RecipeService;


@EnableWebSecurity
@Configuration
public class Security extends WebSecurityConfigurerAdapter {
    @Autowired
    RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Autowired
    RecipeService recipeService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    @Bean
    public PasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2Y);
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        //dirty below (NoOpPasswordEncoder)
        daoAuthenticationProvider.setPasswordEncoder(NoOpPasswordEncoder.getInstance());
        daoAuthenticationProvider.setUserDetailsService(recipeService);
        return daoAuthenticationProvider;
    }


    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic()
                .authenticationEntryPoint(new RestAuthenticationEntryPoint()) // Handle auth error
                .and()
                .csrf().disable().headers().frameOptions().disable() // for Postman, the H2 console
                .and()
                .authorizeRequests() // manage access
                .antMatchers(HttpMethod.POST,"/api/register","/actuator/shutdown").permitAll()
                .antMatchers(HttpMethod.POST, "/api/recipe/new").hasRole("USER")
                .antMatchers(HttpMethod.GET, "/api/recipe/**").hasRole("USER")
                .antMatchers(HttpMethod.DELETE, "/api/recipe/**").hasRole("USER")
                .antMatchers(HttpMethod.PUT, "/api/recipe/**").hasRole("USER")
                .antMatchers(HttpMethod.GET, "/api/recipe/search").hasRole("USER")
                .anyRequest().denyAll()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }
//protected void configure(HttpSecurity httpSecurity) throws Exception {
//        httpSecurity.httpBasic()
//                .and()
//            .authorizeRequests()
//            .antMatchers("/api/register","/api/register/new").permitAll()
//            .antMatchers("/api/recipe/**","/api/recipe/new").authenticated()
//            .and()
//            .csrf().disable()
//            .headers().frameOptions().disable();
//}
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//                .authorizeRequests()
//                .antMatchers("/api/register").permitAll()
//                .anyRequest().authenticated()
//                ;
//    }

}