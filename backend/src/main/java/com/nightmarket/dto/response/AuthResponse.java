package com.nightmarket.dto.response;

public class AuthResponse {

    private String token;
    private String tokenType = "Bearer";
    private StudentProfileResponse student;

    public AuthResponse() {}

    public AuthResponse(String token, StudentProfileResponse student) {
        this.token = token;
        this.tokenType = "Bearer";
        this.student = student;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public StudentProfileResponse getStudent() {
        return student;
    }

    public void setStudent(StudentProfileResponse student) {
        this.student = student;
    }
}
