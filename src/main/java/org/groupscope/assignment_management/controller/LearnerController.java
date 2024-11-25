package org.groupscope.assignment_management.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.groupscope.assignment_management.dto.LearnerDTO;
import org.groupscope.assignment_management.services.LearnerService;
import org.groupscope.security.entity.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Mykyta Liashko
 */
@Slf4j
@CrossOrigin("*")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Validated
public class LearnerController {

  private final LearnerService learnerService;

  @GetMapping("/student")
  public LearnerDTO getStudent(
      @AuthenticationPrincipal User user
  ) {
    return LearnerDTO.from(user.getLearner());
  }

  @PatchMapping("/student/patch")
  public void updateStudent(
      @RequestBody LearnerDTO learnerDTO,
      @AuthenticationPrincipal User user
  ) {
    learnerService.updateLearner(learnerDTO, user.getLearner());
  }

}
