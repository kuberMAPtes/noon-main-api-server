package com.kube.noon.config;

import com.kube.noon.common.security.AccessDefinition;
import com.kube.noon.common.security.authentication.authtoken.generator.*;
import com.kube.noon.common.security.authentication.provider.JwtAuthenticationProvider;
import com.kube.noon.common.security.authentication.provider.KakaoTokenAuthenticationProvider;
import com.kube.noon.common.security.authentication.provider.NoAuthenticationProvider;
import com.kube.noon.common.security.authentication.provider.SimpleJsonAuthenticationProvider;
import com.kube.noon.common.security.filter.AccessControlFilter;
import com.kube.noon.common.security.filter.AuthFilter;
import com.kube.noon.common.security.filter.TokenAuthenticationFilter;
import com.kube.noon.common.security.filter.TokenRefreshFilter;
import com.kube.noon.common.security.support.BearerTokenSupport;
import com.kube.noon.common.security.support.JwtSupport;
import com.kube.noon.member.enums.Role;
import com.kube.noon.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer;
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
    @Profile({ "prod", "accesscontrol" })
    public JwtAuthenticationProvider jwtAuthenticationProvider(JwtSupport tokenSupportList, UserDetailsService userDetailsService) {
        return new JwtAuthenticationProvider(tokenSupportList, userDetailsService);
    }

    @Bean
    @ConditionalOnBean(JwtAuthenticationProvider.class)
    public JwtAuthenticationGenerator jwtAuthenticationTokenGenerator() {
        return new JwtAuthenticationGenerator();
    }

    @Bean
    @Profile({ "prod", "accesscontrol" })
    public KakaoTokenAuthenticationProvider kakaoTokenAuthenticationProvider(UserDetailsService userDetailsService) {
        return new KakaoTokenAuthenticationProvider(userDetailsService);
    }

    @Bean
    @ConditionalOnBean(KakaoTokenAuthenticationProvider.class)
    public KakaoTokenAuthenticationGenerator kakaoTokenAuthenticationGenerator() {
        return new KakaoTokenAuthenticationGenerator();
    }

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
    public ProviderManager authenticationManager(List<AuthenticationProvider> authenticationProviders) {
        return new ProviderManager(authenticationProviders);
    }

    @Bean
    @Profile({"dev", "prod", "accesscontrol" })
    public TokenAuthenticationFilter tokenAuthenticationFilter(List<BearerTokenAuthenticationTokenGenerator> generatorList) {
        return new TokenAuthenticationFilter(generatorList);
    }

    @Bean
    @Profile({"dev", "prod", "accesscontrol" })
    public AuthFilter authFilter(AuthenticationManager authenticationManager) {
        return new AuthFilter(authenticationManager);
    }

    @Bean
    @Profile({"dev", "prod", "accesscontrol" })
    public TokenRefreshFilter tokenRefreshFilter(List<BearerTokenSupport> tokenSupport, @Value("${client-server-domain}") String clientDomain) {
        return new TokenRefreshFilter(tokenSupport, clientDomain);
    }

    @Bean
    @Profile({"accesscontrol"})
    public AccessControlFilter accessRestrictionFilter(List<BearerTokenSupport> tokenSupports,
                                                       ApplicationContext applicationContext,
                                                       MemberRepository memberRepository) {
        return new AccessControlFilter(tokenSupports, applicationContext, memberRepository);
    }

    @Bean
    @Profile({ "prod", "accesscontrol" })
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
    @Profile({"authdev", "prod"})
    public SecurityFilterChain tokenBasedFilterChain(
            HttpSecurity http,
            AuthFilter authFilter,
            TokenAuthenticationFilter tokenAuthenticationFilter,
            TokenRefreshFilter tokenRefreshFilter
    ) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((registry) -> {
                    registry.requestMatchers(AccessDefinition.ALLOWED_TO_MEMBER.stream().map(AntPathRequestMatcher::new).toArray(AntPathRequestMatcher[]::new))
                            .hasAnyAuthority(Role.MEMBER.name(), Role.ADMIN.name())
                            .anyRequest()
                            .permitAll();
                })
                .sessionManagement((config) -> config.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .requestCache(RequestCacheConfigurer::disable)
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(tokenAuthenticationFilter, AuthFilter.class)
                .addFilterAfter(tokenRefreshFilter, AuthorizationFilter.class)
                .build();
    }

    @Bean
    @Profile({"accesscontrol"})
    public SecurityFilterChain tokenBasedFilterChainWithAccessControl(
            HttpSecurity http,
            AuthFilter authFilter,
            TokenAuthenticationFilter tokenAuthenticationFilter,
            TokenRefreshFilter tokenRefreshFilter,
            AccessControlFilter accessControlFilter
    ) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((registry) -> {
                    registry.requestMatchers(AccessDefinition.ALLOWED_TO_MEMBER.stream().map(AntPathRequestMatcher::new).toArray(AntPathRequestMatcher[]::new))
                            .hasAnyAuthority(Role.MEMBER.name(), Role.ADMIN.name())
                            .anyRequest()
                            .permitAll();
                })
                .sessionManagement((config) -> config.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .requestCache(RequestCacheConfigurer::disable)
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(tokenAuthenticationFilter, AuthFilter.class)
                .addFilterAfter(tokenRefreshFilter, AuthorizationFilter.class)
                .addFilterAfter(accessControlFilter, AuthorizationFilter.class)
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
