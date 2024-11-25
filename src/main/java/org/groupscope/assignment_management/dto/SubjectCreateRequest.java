package org.groupscope.assignment_management.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Mykyta Liashko
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectCreateRequest {

  @NotBlank(message = "Name is required")
  private String name;

  @NotBlank(message = "Brief is required")
  private String brief;

}
