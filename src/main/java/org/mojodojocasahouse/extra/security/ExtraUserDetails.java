package org.mojodojocasahouse.extra.security;

import org.mojodojocasahouse.extra.model.Authority;
import org.mojodojocasahouse.extra.model.ExtraUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ExtraUserDetails implements UserDetails {

    private String email;

    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    private Boolean active;

    public ExtraUserDetails(String email, String password, Set<Authority> authorities, Boolean active){
        this.email = email;
        this.password = password;
        this.authorities = convertToSpringAuthorities(authorities);
        this.active = active;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.active;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.active;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.active;
    }

    @Override
    public boolean isEnabled() {
        return this.active;
    }

    public static ExtraUserDetails from(ExtraUser user){
        return new ExtraUserDetails(
                user.getEmail(),
                user.getPassword(),
                user.getAuthorities(),
                true
        );
    }

    private Collection<? extends GrantedAuthority> convertToSpringAuthorities(Set<Authority> authorities){
        if (authorities != null && !authorities.isEmpty()){
            return authorities.stream()
                    .map(Authority::getRole)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet());
        } else {
            return new HashSet<>();
        }
    }
}
