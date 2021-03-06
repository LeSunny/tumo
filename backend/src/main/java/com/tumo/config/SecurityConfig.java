package com.tumo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.tumo.jwt.JwtAccessDeniedHandler;
import com.tumo.jwt.JwtAuthenticationEntryPoint;
import com.tumo.jwt.JwtSecurityConfig;
import com.tumo.jwt.TokenProvider;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	private final TokenProvider tokenProvider;
    private final CorsFilter corsFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
	
    public SecurityConfig(
            TokenProvider tokenProvider,
            CorsFilter corsFilter,
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
            JwtAccessDeniedHandler jwtAccessDeniedHandler
    ) {
        this.tokenProvider = tokenProvider;
        this.corsFilter = corsFilter;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    
	@Override
	public void configure(WebSecurity web) throws Exception {
		
		// ?????? ????????? ????????? Swagger??? ?????? ????????? ?????? ?????? ?????? ?????? ???????????????.
		web
			.ignoring()
			.antMatchers("/swagger/**", "/swagger-ui.html", 
					"/swagger-resources/**", "/webjars/**", "/v2/api-docs");	// swagger ????????? ?????? ??????	
	}
	
	@Override
	public void configure(HttpSecurity http) throws Exception {
		http			
			.csrf().disable()
			
//			.addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
			
			.exceptionHandling()
			.authenticationEntryPoint(jwtAuthenticationEntryPoint)
			.accessDeniedHandler(jwtAccessDeniedHandler)
			
			// ????????? ???????????? ?????? ????????? STATELESS??? ?????? (????????? ???????????? ?????? ????????? ??????)
			.and()
			.sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			
			.and()
			.formLogin().disable() // Spring Security ?????? ????????? ????????? ?????????
			.authorizeRequests()	// http ???????????? ?????? ?????? ??????
			.antMatchers("/**").permitAll()	
			.anyRequest().authenticated()
		
			.and()
			.apply(new JwtSecurityConfig(tokenProvider));
	}
	
}
