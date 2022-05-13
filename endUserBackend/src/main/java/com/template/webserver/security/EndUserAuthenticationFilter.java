package com.template.webserver.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.template.flows.model.AccountIdAndPassword;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/*
 * Filtre d'extraction de l'utilisateurs
 */
public class EndUserAuthenticationFilter extends UsernamePasswordAuthenticationFilter{

    private AuthenticationManager authenticationManager;

    public EndUserAuthenticationFilter(AuthenticationManager authenticationManager) {
        super();
        this.authenticationManager = authenticationManager;
    }

    /*
     * Methode d'extraction de l'utilisateur
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        AccountIdAndPassword accountIdAndPassword = null;
        try {
            accountIdAndPassword = new ObjectMapper().readValue(request.getInputStream(), AccountIdAndPassword.class);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(accountIdAndPassword.getCompteId(), accountIdAndPassword.getPassword()));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        User springUser =(User) authResult.getPrincipal();

        //acces token definition
        String jwtToken = Jwts.builder()
                .setSubject(springUser.getUsername())//on peut mettre tout ce qu'on veut
                .setExpiration(new Date(System.currentTimeMillis()+SecurityConstante.EXPIRATION_TIME))
                .setIssuer(request.getRequestURI().toString())
                .signWith(SignatureAlgorithm.HS256, SecurityConstante.SECRET)
                .claim("roles", springUser.getAuthorities())
                .compact();
        response.addHeader(SecurityConstante.HEADER_STRING, SecurityConstante.TOKEN_PREFIX+jwtToken);
    }
}