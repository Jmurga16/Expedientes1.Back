package com.gestionexpedientes.security.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class CreateUserDto {
    @NotBlank(message = "Nombre es Obligatorio.")
    private String name;
    @NotBlank(message = "Apellidos es Obligatorio.")
    private String lastname;
    @NotBlank(message = "DNI es Obligatorio.")
    private String dni;
    @NotBlank(message = "Domicilio es Obligatorio.")
    private String address;
    @NotBlank(message = "Email es Obligatorio.")
    @Email(message = "invalid email")
    private String email;
    @NotBlank(message = "Contraseña es Obligatoria")
    private String password;

    public CreateUserDto() {
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
}
