package com.template.webserver.security;
import java.util.ArrayList;
import java.util.Collection;

import com.template.flows.centralBankFlows.LoadCentralBankByAccountIdFlowInitiator;
import com.template.flows.model.AccountIdAndPassword;
import com.template.webserver.NodeRPCConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserDetailsServiceImplementation implements UserDetailsService{

    @Autowired
    private NodeRPCConnection nodeRPCConnection ;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override

    public UserDetails loadUserByUsername(String accountId) throws UsernameNotFoundException {
        try {
            Collection<GrantedAuthority> authorisations = new ArrayList<GrantedAuthority>();
            AccountIdAndPassword accountIdAndPassword = nodeRPCConnection.proxy.startTrackedFlowDynamic(
                    LoadCentralBankByAccountIdFlowInitiator.class,accountId).getReturnValue().get();
            if(accountIdAndPassword == null) throw  new UsernameNotFoundException("Cette banque centrale n'existe pas");
            //Preparer les roles de l sous forme de collection d'objets compressible par spring security
            if (accountId.charAt(accountId.length()-1) == 'c' && accountId.charAt(accountId.length()-2) == 'b'){
                //bc = bank central
                authorisations.add(new SimpleGrantedAuthority("centralbank"));
            }else{
                //super admin
                authorisations.add(new SimpleGrantedAuthority("cbdcadmin"));
            }
            String passwordEncoder = bCryptPasswordEncoder.encode(accountIdAndPassword.getPassword());
            return new User(accountIdAndPassword.getCompteId(),passwordEncoder,authorisations);
        }catch (Exception exception){
            throw  new UsernameNotFoundException("Problème d'accès au reseau");
        }
    }
}
