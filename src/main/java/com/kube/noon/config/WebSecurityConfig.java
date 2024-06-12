package com.kube.noon.config;

import com.kube.noon.common.security.authentication.authtoken.generator.*;
import com.kube.noon.common.security.authentication.provider.JwtAuthenticationProvider;
import com.kube.noon.common.security.authentication.provider.KakaoTokenAuthenticationProvider;
import com.kube.noon.common.security.authentication.provider.NoAuthenticationProvider;
import com.kube.noon.common.security.authentication.provider.SimpleJsonAuthenticationProvider;
import com.kube.noon.common.security.filter.AuthFilter;
import com.kube.noon.common.security.filter.TokenAuthenticationFilter;
import com.kube.noon.common.security.filter.TokenRefreshFilter;
import com.kube.noon.common.security.support.BearerTokenSupport;
import com.kube.noon.common.security.support.JwtSupport;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

/**
 * Spring Security Configuration class.
 *
 * @author PGD
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    @ConditionalOnMissingBean(AuthenticationProvider.class)
    public NoAuthenticationProvider noAuthenticationProvider() {
        return new NoAuthenticationProvider();
    }

    @Bean
    @ConditionalOnBean(NoAuthenticationProvider.class)
    public NoAuthenticationGenerator noAuthenticationTokenGenerator() {
        return new NoAuthenticationGenerator();
    }

    @Bean
    @Profile("dev")
    public SimpleJsonAuthenticationProvider simpleJsonAuthenticationProvider(UserDetailsService userDetailsService) {
        return new SimpleJsonAuthenticationProvider(userDetailsService);
    }

    @Bean
    @ConditionalOnBean(SimpleJsonAuthenticationProvider.class)
    public SimpleJsonAuthenticationGenerator simpleJsonAuthenticationTokenGenerator() {
        return new SimpleJsonAuthenticationGenerator();
    }

    @Bean
    @Profile("prod")
    public JwtAuthenticationProvider jwtAuthenticationProvider(JwtSupport jwtSupport, UserDetailsService userDetailsService) {
        return new JwtAuthenticationProvider(jwtSupport, userDetailsService);
    }

    @Bean
    @ConditionalOnBean(JwtAuthenticationProvider.class)
    public JwtAuthenticationGenerator jwtAuthenticationTokenGenerator() {
        return new JwtAuthenticationGenerator();
    }

    @Bean
    @Profile("prod")
    public KakaoTokenAuthenticationProvider kakaoTokenAuthenticationProvider() {
        return new KakaoTokenAuthenticationProvider();
    }

    @Bean
    @ConditionalOnBean(KakaoTokenAuthenticationProvider.class)
    public KakaoTokenAuthenticationGenerator kakaoTokenAuthenticationGenerator() {
        return new KakaoTokenAuthenticationGenerator();
    }

    @Bean
    public AuthenticationManager authenticationManager(List<AuthenticationProvider> authenticationProviders) {
        return new ProviderManager(authenticationProviders);
    }

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter(List<BearerTokenAuthenticationTokenGenerator> generatorList) {
        return new TokenAuthenticationFilter(generatorList);
    }

    @Bean
    public AuthFilter authFilter(AuthenticationManager authenticationManager) {
        return new AuthFilter(authenticationManager);
    }

    @Bean
    public TokenRefreshFilter tokenRefreshFilter(BearerTokenSupport tokenSupport) {
        return new TokenRefreshFilter(tokenSupport);
    }

    @Bean
    @Profile({"dev", "prod"})
    public SecurityFilterChain tokenBasedFilterChainDev(
            HttpSecurity http,
            AuthFilter authFilter,
            TokenAuthenticationFilter tokenAuthenticationFilter,
            TokenRefreshFilter tokenRefreshFilter
    ) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((registry) -> registry.anyRequest().authenticated()) // TODO
                .sessionManagement((config) -> config.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(tokenAuthenticationFilter, AuthFilter.class)
                .addFilterAfter(tokenRefreshFilter, AuthorizationFilter.class)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean(SecurityFilterChain.class)
    public SecurityFilterChain defaultFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((registry) -> registry.anyRequest().permitAll())
                .build();
    }
}
