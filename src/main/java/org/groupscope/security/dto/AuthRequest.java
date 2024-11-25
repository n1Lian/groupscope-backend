package org.groupscope.security.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AuthRequest {

    @NotBlank
    protected String login;

    @NotBlank
    protected String password;

}
