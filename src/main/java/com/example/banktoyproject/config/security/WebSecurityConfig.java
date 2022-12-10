package com.example.banktoyproject.config.security;

import com.example.banktoyproject.config.exception.AccessDeniedHandlerImpl;
import com.example.banktoyproject.config.jwt.JwtAuthenticationEntryPoint;
import com.example.banktoyproject.config.jwt.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity // 스프링 Security 지원을 가능하게 함
@EnableGlobalMethodSecurity(securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAuthenticationFilter jwtRequestFilter;
    private final AccessDeniedHandlerImpl accessDeniedHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.headers().frameOptions().disable();
        http.cors();

        http.authorizeRequests()
//                인증 과정 필요 없음
//                .antMatchers(HttpMethod.POST, "/..").permitAll()

//                인증 과정 필요함
//                .antMatchers(HttpMethod.GET, "/..").authenticated()

                .antMatchers(HttpMethod.PUT, "/reviedfdfws/**").authenticated()

                // 그 외 모든 요청은 인증과정 필요
                // .anyRequest().authenticated()
                // 그 외 모든 요청은 허용
                .anyRequest().permitAll()
                .and()
                // 정상적인 토큰이 없을 경우
                .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .and()
                // 정상적인 토큰이지만 권한이 다른 경우
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public BCryptPasswordEncoder encodePassword() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    // cors 설정
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOriginPattern("*");
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "OPTIONS", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // Spring Boot는 Jackson 라이브러리가 포함되어 있어서 자동으로 MappingJackson2HttpMessageConverter를 사용하여 JSON으로 변환한다.
    // Spring Boot를 사용하면 MappingJackson2HttpMessageConverter를 커스터마이징 할 때 WebMvcConfigurer를 implement 할 필요가 없다.
    // 해당 bean을 선언만 해주면 된다.
    @Bean
    public MappingJackson2HttpMessageConverter jsonEscapeConverter() {
        // MappingJackson2HttpMessageConverter Default ObjectMapper 설정 및 ObjectMapper Config 설정
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();
        objectMapper.getFactory().setCharacterEscapes(new HtmlCharacterEscapes());
        return new MappingJackson2HttpMessageConverter(objectMapper);
    }
}

