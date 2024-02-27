package org.groupscope.security.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;

@Data
public class AuthRequest {

    @NotEmpty
    @NotBlank
    protected String login;

    @NotEmpty
    @NotBlank
    protected String password;

}
