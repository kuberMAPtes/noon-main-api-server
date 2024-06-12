package com.kube.noon.config;

import com.kube.noon.common.security.AccessDefinition;
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
import com.kube.noon.member.enums.Role;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

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
    public JwtAuthenticationProvider jwtAuthenticationProvider(JwtSupport tokenSupportList, UserDetailsService userDetailsService) {
        return new JwtAuthenticationProvider(tokenSupportList, userDetailsService);
    }

    @Bean
    @ConditionalOnBean(JwtAuthenticationProvider.class)
    public JwtAuthenticationGenerator jwtAuthenticationTokenGenerator() {
        return new JwtAuthenticationGenerator();
    }

    @Bean
    @Profile("prod")
    public KakaoTokenAuthenticationProvider kakaoTokenAuthenticationProvider(UserDetailsService userDetailsService) {
        return new KakaoTokenAuthenticationProvider(userDetailsService);
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
    @Profile({"dev", "prod"})
    public TokenAuthenticationFilter tokenAuthenticationFilter(List<BearerTokenAuthenticationTokenGenerator> generatorList) {
        return new TokenAuthenticationFilter(generatorList);
    }

    @Bean
    @Profile({"dev", "prod"})
    public AuthFilter authFilter(AuthenticationManager authenticationManager) {
        return new AuthFilter(authenticationManager);
    }

    @Bean
    @Profile({"dev", "prod"})
    public TokenRefreshFilter tokenRefreshFilter(List<BearerTokenSupport> tokenSupport) {
        return new TokenRefreshFilter(tokenSupport);
    }

    @Bean
    @Profile("prod")
    public PasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder(); // TODO: Should replace with Argon2PasswordEncoder someday
    }

    @Bean
    @ConditionalOnMissingBean(PasswordEncoder.class)
    public PasswordEncoder noneEncryptionPasswordEncoder() {
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return String.valueOf(rawPassword);
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return String.valueOf(rawPassword).equals(encodedPassword);
            }
        };
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
                .authorizeHttpRequests((registry) -> {
                    AccessDefinition.WHITE_LIST.forEach((uri) ->
                            registry.requestMatchers(new AntPathRequestMatcher(uri)).permitAll());

                    AccessDefinition.ALLOWED_TO_MEMBER.forEach((uri) ->
                            registry.requestMatchers(new AntPathRequestMatcher(uri))
                                    .hasAnyAuthority(Role.MEMBER.name(), Role.ADMIN.name()));
                })
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
