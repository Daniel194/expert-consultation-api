package com.code4ro.legalconsultation.service.impl;

import com.code4ro.legalconsultation.common.security.CurrentUser;
import com.code4ro.legalconsultation.model.persistence.ApplicationUser;
import com.code4ro.legalconsultation.repository.ApplicationUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private ApplicationUserRepository applicationUserRepository;

    @Autowired
    public CustomUserDetailsService(final ApplicationUserRepository applicationUserRepository) {
        this.applicationUserRepository = applicationUserRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        // let people login with either username or email
        final ApplicationUser applicationUser = applicationUserRepository.findByUsernameOrEmail(usernameOrEmail)
                .orElseThrow(() ->
                        new UsernameNotFoundException("ApplicationUser not found with username or email : " + usernameOrEmail)
                );

        return CurrentUser.create(applicationUser);
    }

    // used by JWTAuthenticationFilter
    @Transactional
    @Cacheable(cacheNames = "users")
    public UserDetails loadUserById(UUID id) throws UsernameNotFoundException {
        ApplicationUser applicationUser = applicationUserRepository.findById(id).orElseThrow(
                () -> new UsernameNotFoundException("ApplicationUser not found with id : " + id)
        );

        return CurrentUser.create(applicationUser);
    }
}
