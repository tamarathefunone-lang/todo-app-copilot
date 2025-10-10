package com.todoapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * Request DTO for user login
 */
public class LoginRequest {
    
    @JsonProperty("email")
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;
    
    @JsonProperty("password")
    @NotBlank(message = "Password is required")
    private String password;

    public LoginRequest() {}

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
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

    @Override
    public String toString() {
        return "LoginRequest{" +
                "email='" + email + '\'' +
                '}';
    }
}
