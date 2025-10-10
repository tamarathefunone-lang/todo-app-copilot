package com.todoapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response DTO for authentication operations
 */
public class AuthResponse {
    
    @JsonProperty("token")
    private String token;
    
    @JsonProperty("userId")
    private String userId;
    
    @JsonProperty("email")
    private String email;
    
    @JsonProperty("firstName")
    private String firstName;
    
    @JsonProperty("lastName")
    private String lastName;
    
    @JsonProperty("expiresIn")
    private long expiresIn; // in seconds

    public AuthResponse() {}

    public AuthResponse(String token, String userId, String email, String firstName, String lastName, long expiresIn) {
        this.token = token;
        this.userId = userId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.expiresIn = expiresIn;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    @Override
    public String toString() {
        return "AuthResponse{" +
                "userId='" + userId + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", expiresIn=" + expiresIn +
                '}';
    }
}
