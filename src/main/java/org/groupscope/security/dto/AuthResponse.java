package org.groupscope.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.groupscope.assignment_management.entity.LearningRole;

import jakarta.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    @NotEmpty
    private String jwtToken;

    private LearningRole role;
}
