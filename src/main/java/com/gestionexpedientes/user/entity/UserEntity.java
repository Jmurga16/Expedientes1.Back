package com.gestionexpedientes.user.entity;

import com.gestionexpedientes.global.entity.EntityId;
import com.gestionexpedientes.security.enums.RoleEnum;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "users")
public class UserEntity extends EntityId {
    private String name;
    private String lastname;
    private String dni;
    private String address;
    private String email;
    private String username;
    private String password;
    private List<RoleEnum> roles;
    private Integer idArea;
    private int status;


    public UserEntity() {
    }

    public UserEntity(int id, String name, String lastname, String dni, String address, String email, String username, String password, List<RoleEnum> roles,Integer idArea, int status) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.dni = dni;
        this.address = address;
        this.email = email;
        this.username = username;
        this.password = password;
        this.roles = roles;
        this.idArea = idArea;
        this.status = status;
    }

    @Override
   public int getId() {
        return super.getId();
    }

    @Override
    public void setId(int id) {
        super.setId(id);
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

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public List<RoleEnum> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleEnum> roles) {
        this.roles = roles;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    public Integer getIdArea() {
        return idArea;
    }

    public void setIdArea(int idArea) {
        this.idArea = idArea;
    }

    public void setPassword(String password){ this.password = password;}


}
