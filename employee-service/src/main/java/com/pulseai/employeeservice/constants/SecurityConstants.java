package com.pulseai.employeeservice.constants;

public class SecurityConstants {
    public static final String JWT_SECRET = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";

    // Roles
    public static final String ROLE_VP = "ROLE_VP";
    public static final String ROLE_GLOBAL_HR = "ROLE_GLOBAL_HR";
    public static final String ROLE_REGIONAL_HR = "ROLE_REGIONAL_HR";
    public static final String ROLE_EMPLOYEE = "ROLE_EMPLOYEE";
}
