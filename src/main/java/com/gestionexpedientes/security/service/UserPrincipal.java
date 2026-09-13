package com.gestionexpedientes.security.service;

import com.gestionexpedientes.security.entity.UserEntity;
import com.gestionexpedientes.security.enums.RoleEnum;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

public class UserPrincipal implements UserDetails {

    private int id;
    private Integer idArea;
    private String username;
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(int id, Integer idArea, String username, String email, String password, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.idArea = idArea;
        this.username = username;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    public static UserPrincipal build(UserEntity userEntity) {
        Collection<GrantedAuthority> authorities =
                userEntity.getRoles().stream().map(rol -> new SimpleGrantedAuthority(rol.name())).collect(Collectors.toList());
        return new UserPrincipal(userEntity.getId(), userEntity.getIdArea(), userEntity.getUsername(), userEntity.getEmail(), userEntity.getPassword(), authorities);
    }

    public boolean hasRole(RoleEnum role) {
        return authorities.stream().anyMatch(auth -> auth.getAuthority().equals(role.name()));
    }

    public boolean isAdmin() {
        return hasRole(RoleEnum.ROLE_ADMIN);
    }

    /** Referente o colaborador de un área: interviene en los expedientes cuyo flujo incluye su área. */
    public boolean isAreaStaff() {
        return hasRole(RoleEnum.ROLE_AREA) || hasRole(RoleEnum.ROLE_COLAB);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
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

    public int getId() {
        return id;
    }

    public Integer getIdArea() {
        return idArea;
    }

    public String getEmail() {
        return email;
    }
}
