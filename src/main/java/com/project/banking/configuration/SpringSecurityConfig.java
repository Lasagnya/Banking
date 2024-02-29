package com.project.banking.configuration;

import com.project.banking.security.JWTFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SpringSecurityConfig {
	public final UserDetailsService userDetailsService;
	private final JWTFilter jwtFilter;
	private final AuthenticationEntryPoint authenticationEntryPoint;

	@Autowired
	public SpringSecurityConfig(UserDetailsService userDetailsService, JWTFilter jwtFilter, AuthenticationEntryPoint authenticationEntryPoint) {
		this.userDetailsService = userDetailsService;
		this.jwtFilter = jwtFilter;
		this.authenticationEntryPoint = authenticationEntryPoint;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.exceptionHandling(Customizer.withDefaults())
				.csrf(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests((AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry requests) -> requests
						.requestMatchers("/", "/api/auth/**", "/api/person/**").permitAll()
						.requestMatchers("/api/transaction/**").permitAll()
						.anyRequest().authenticated()
				)
				.logout(LogoutConfigurer::permitAll)
				.httpBasic(configurer -> configurer.authenticationEntryPoint(authenticationEntryPoint))
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
		authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new Argon2PasswordEncoder(16, 32, 1, 65536, 1);
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}
}

