package com.restapi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {


	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		httpSecurity.httpBasic().and().authorizeRequests().antMatchers("/").authenticated();
		httpSecurity.httpBasic().and().authorizeRequests().antMatchers("/note").authenticated();
		httpSecurity.httpBasic().and().authorizeRequests().antMatchers("/note/{id}").authenticated();
		httpSecurity.httpBasic().and().authorizeRequests().antMatchers("/note/{id}/attachments").authenticated();
		
		httpSecurity.csrf().disable();
	}
	
	public void configure(WebSecurity web) throws Exception 
	{
		web.ignoring().antMatchers(HttpMethod.POST, "/user/register/");
		web.ignoring().antMatchers(HttpMethod.POST, "/reset");
	}
	
	@Autowired
	CustomAuthenticationProvider customAuthenticationProvider;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	    auth.authenticationProvider(customAuthenticationProvider);
	}
	
}