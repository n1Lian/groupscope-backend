package org.groupscope.assignment_management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Mykyta Liashko
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewHeadmanUpdateRequest {

  @NotNull(message = "Id is required")
  private Long id;

  @NotBlank(message = "Name is required")
  private String name;

  @NotBlank(message = "Lastname is required")
  private String lastname;

}
