package org.mojodojocasahouse.extra.security;

import lombok.RequiredArgsConstructor;
import org.mojodojocasahouse.extra.model.ExtraUser;
import org.mojodojocasahouse.extra.repository.ExtraUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExtraUserDetailsService implements UserDetailsService {

    private final ExtraUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ExtraUser foundUser = userRepository
                .findByEmail(username)
                .orElseThrow(
                        () -> new UsernameNotFoundException("User email: " + username + " not found.")
                );

        return ExtraUserDetails.from(foundUser);
    }
}
