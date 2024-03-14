package com.isys.api.common.login.dto;

import com.isys.api.common.login.mapper.LoginMapper;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@AllArgsConstructor
public class CustomUserDetails implements UserDetails {
    private final UserAuthInfo userAuthInfo;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                //return userGroupEntity.getRole();
                return "";
            }
        });

        return collection;
    }

    @Override
    public String getPassword() {
        return userAuthInfo.getPASSWORD();
    }

    @Override
    public String getUsername() {
        return userAuthInfo.getNAME();
    }

    public String getUserId() {
        return userAuthInfo.getUSER_ID();
    }

    public String getUserGroupFk() {
        return userAuthInfo.getUSERGROUP_FK();
    }


    @Override
    public boolean isAccountNonExpired() {

        return true;
    }

    @Override
    public boolean isAccountNonLocked() {

        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {

        return true;
    }

    @Override
    public boolean isEnabled() {

        return true;
    }
}
