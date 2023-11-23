package org.groupscope.security.dto;

import lombok.Data;

import jakarta.validation.constraints.NotEmpty;

@Data
public class AuthRequest {

    @NotEmpty
    protected String login;

    @NotEmpty
    protected String password;

}
