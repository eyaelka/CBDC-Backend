package com.template.webserver.security;
import java.util.ArrayList;
import java.util.Collection;

import com.template.flows.centralBankFlows.LoadCentralBankByAccountIdFlowInitiator;
import com.template.flows.commercialBankFlows.LoadCommercialBankByAccountIdFlowInitiator;
import com.template.flows.model.AccountIdAndPassword;
import com.template.model.centralBank.CentralBankData;
import com.template.model.commercialBank.CommercialBankData;
import com.template.webserver.NodeRPCConnection;
import com.template.webserver.service.interfaces.CommercialBankInterface;
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

    @Autowired
    CommercialBankInterface commercialBankInterface;

    @Override

    public UserDetails loadUserByUsername(String accountId) throws UsernameNotFoundException {
        try {
            if (accountId.endsWith("bc")) {
                //bc = bank central
                AccountIdAndPassword accountIdAndPassword = nodeRPCConnection.proxy.startTrackedFlowDynamic(
                        LoadCentralBankByAccountIdFlowInitiator.class, accountId).getReturnValue().get();
                if (accountIdAndPassword == null)
                    throw new UsernameNotFoundException("Cette banque centrale n'existe pas");
                //Preparer les roles de l sous forme de collection d'objets compressible par spring security
                Collection<GrantedAuthority> authorisations = new ArrayList<>();
                authorisations.add(new SimpleGrantedAuthority("centralbank"));
                String passwordEncoder = bCryptPasswordEncoder.encode(accountIdAndPassword.getPassword());
                return new User(accountIdAndPassword.getCompteId(), passwordEncoder, authorisations);
            }
            AccountIdAndPassword accountIdAndPassword = nodeRPCConnection.proxy.startTrackedFlowDynamic(
                    LoadCommercialBankByAccountIdFlowInitiator.class, accountId).getReturnValue().get();
            if (accountIdAndPassword == null)
                throw new UsernameNotFoundException("Cette banque commerciale n'existe pas");
            //Preparer les roles de l sous forme de collection d'objets compressible par spring security
            Collection<GrantedAuthority> authorisations = new ArrayList<>();

            CommercialBankData commercialbank =commercialBankInterface.getCommercialBankById(accountIdAndPassword.getCompteId());

            authorisations.add(new SimpleGrantedAuthority("commercialbank"));
            authorisations.add(new SimpleGrantedAuthority(commercialbank.getPays()));

            String passwordEncoder = bCryptPasswordEncoder.encode(accountIdAndPassword.getPassword());
            return new User(accountIdAndPassword.getCompteId(), passwordEncoder, authorisations);
        }catch (Exception exception){
            throw  new UsernameNotFoundException(" Probl??me de l'acc??s au reseau");
        }
    }
}
