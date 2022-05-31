package com.template.webserver.security;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{
    @Autowired
    public BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    public UserDetailsService userDetailsService;
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    public void configure(final WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers("/centralbank/save")
                .antMatchers("/politique/createmassemonnetaire")
                .antMatchers("/politique/createtxregulationlocal")
                .antMatchers("/politique/createtxregulationinterpays")
                ;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();

        //Desactivation de la session
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        //donner la permission à tous les utilisateurs de s'authentifier ou de s'enregistrer
        http.authorizeRequests().antMatchers(HttpMethod.POST,"/login/**","/politique/regulationmassemonnetaire/**","/politique/createdeviseregulation/**","/createmoney","/interbanktransaction").permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.GET,"/getCurrentBalance","/getAllCommercialBanks","/getCommercialBankByCountry/**","/politique/regulationmassemonnetaire/**","/politique/regulationdevise/**","/politique/txregulationlocal/**","/politique/txregulationinterpays/**").permitAll();
        //donner les permissions
        http.authorizeRequests().antMatchers(HttpMethod.POST,"/centralbank/save").hasAuthority("cbdcadmin");
        http.authorizeRequests().antMatchers(HttpMethod.POST,"/centralbank/saveotheraccount").hasAuthority("centralbank");
        http.authorizeRequests().antMatchers(HttpMethod.POST,"/centralbank/update").hasAuthority("centralbank");
        http.authorizeRequests().antMatchers(HttpMethod.POST, "/centralbank/deleteOrActiveOrSwithAccountType").hasAuthority("centralbank");
        http.authorizeRequests().anyRequest().authenticated();
        http.addFilter(new CentralBankAuthenticationFilter(authenticationManager()));
        http.addFilterBefore(new CentralBankAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public BCryptPasswordEncoder getBCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
