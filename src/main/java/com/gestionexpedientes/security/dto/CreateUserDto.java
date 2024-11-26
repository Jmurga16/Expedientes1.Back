package com.gestionexpedientes.security.dto;


import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

public class CreateUserDto {
    @NotBlank(message = "Nombre es Obligatorio.")
    private String name;
    @NotBlank(message = "Apellidos es Obligatorio.")
    private String lastname;
    @NotBlank(message = "DNI es Obligatorio.")
    private String dni;
    @NotBlank(message = "Domicilio es Obligatorio.")
    private String address;

    private String username;
    @NotBlank(message = "Email es Obligatorio.")
    @Email(message = "invalid email")
    private String email;
    @NotBlank(message = "Contraseña es Obligatoria")
    private String password;
    // @NotEmpty(message = "roles are mandatory")
    List<String> roles = new ArrayList<>();

    public CreateUserDto() {
    }



    public CreateUserDto(String name, String lastname, String dni, String address, String username, String email, String password, List<String> roles) {
        this.name = name;
        this.lastname =lastname;
        this.dni=dni;
        this.address=address;
        this.username = username;
        this.email = email;
        this.password = password;
        this.roles = roles;
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

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
