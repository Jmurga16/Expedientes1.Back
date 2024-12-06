package com.gestionexpedientes.user.dto;

import com.gestionexpedientes.security.enums.RoleEnum;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.List;

public class UserDto {
    @NotBlank(message = "Nombre es Obligatorio")
    private String name;
    @NotBlank(message = "Apellidos es Obligatorio")
    private String lastname;
    @NotBlank(message = "DNI es Obligatorio")
    private String dni;
    @NotBlank(message = "Domicilio es Obligatorio")
    private String address;

    private String username;
    @NotBlank(message = "Email es Obligatorio")
    private String email;
    @NotBlank(message = "Contraseña es Obligatorio")
    private String password;
    List<RoleEnum> roles;
    private Integer idArea;
    private Integer status;

    public UserDto() {
    }

    public UserDto(String name, String lastname, String dni, String address, String username, String email, String password, List<RoleEnum> roles, Integer idArea, Integer status) {
        this.name = name;
        this.lastname = lastname;
        this.dni = dni;
        this.address = address;
        this.username = username;
        this.email = email;
        this.password = password;
        this.roles = roles;
        this.idArea = idArea;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<RoleEnum> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleEnum> roles) {
        this.roles = roles;
    }

    public Integer getIdArea() {
        return idArea;
    }

    public void setIdArea(Integer idArea) {
        this.idArea = idArea;
    }
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }



}
