package com.example.employee_management_system;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

@Configuration
public class SecurityConfig {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public UserDetailsService userDetailsService(AppUserRepository appUserRepository) {

		return username -> {
			AppUser appUser = appUserRepository.findByUsername(username)
					.orElseThrow(() -> new org.springframework.security.core.userdetails.UsernameNotFoundException(
							"User not found"));

			return org.springframework.security.core.userdetails.User.withUsername(appUser.getUsername())
					.password(appUser.getPassword()).roles(appUser.getRole()).build();
		};
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(auth -> auth.requestMatchers("/api/auth/register").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/**").hasAnyRole("USER", "ADMIN")
						.requestMatchers("/api/**").hasRole("ADMIN").anyRequest().permitAll())
				.exceptionHandling(exceptions -> exceptions.defaultAuthenticationEntryPointFor(
						new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
						new org.springframework.security.web.util.matcher.AntPathRequestMatcher("/api/**")))
				.formLogin(form -> form.successHandler((request, response, authentication) -> {
					response.setStatus(200);
					response.setContentType("application/json");
					response.getWriter().write("{\"message\":\"Login successful\"}");
				}).failureHandler((request, response, exception) -> {
					response.setStatus(401);
					response.setContentType("application/json");
					response.getWriter().write("{\"error\":\"Invalid username or password\"}");
				}))
				.sessionManagement(session -> session.sessionCreationPolicy(
						org.springframework.security.config.http.SessionCreationPolicy.IF_REQUIRED))
				.logout(logout -> logout.logoutUrl("/logout").invalidateHttpSession(true).clearAuthentication(true)
						.deleteCookies("JSESSIONID").logoutSuccessHandler((request, response, authentication) -> {
							response.setStatus(200);
							response.setContentType("application/json");
							response.getWriter().write("{\"message\":\"Logout successful\"}");
						}));

		return http.build();
	}
}
