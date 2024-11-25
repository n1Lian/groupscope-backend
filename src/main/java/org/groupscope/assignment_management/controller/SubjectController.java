package org.groupscope.assignment_management.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.groupscope.assignment_management.dto.SubjectCreateRequest;
import org.groupscope.assignment_management.dto.SubjectDTO;
import org.groupscope.assignment_management.entity.LearningGroup;
import org.groupscope.assignment_management.services.SubjectService;
import org.groupscope.exceptions.NoLearnerGroupException;
import org.groupscope.security.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
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
public class SubjectController {

  private final SubjectService subjectService;

  @GetMapping("/subject/all")
  public List<SubjectDTO> getSubjects(
      @AuthenticationPrincipal User user
  ) {
    return subjectService.getAllSubjectsByGroup(user.getLearner().getLearningGroup());
  }

  @PostMapping("/subject/add")
  @PreAuthorize("hasRole('HEADMAN')")
  @ResponseStatus(HttpStatus.OK)
  public void addSubject(
      @RequestBody @NotNull(message = "Wrong request") @Valid SubjectCreateRequest createRequest,
      @AuthenticationPrincipal User user
  ) {
    if (user.getLearner() == null || user.getLearner().getLearningGroup() == null) {
      throw new NoLearnerGroupException();
    }

    LearningGroup group = user.getLearner().getLearningGroup();
    subjectService.addSubject(group, createRequest);
  }

  @PatchMapping("/subject/{subjectId}/patch")
  @PreAuthorize("hasRole('HEADMAN')")
  public void patchSubject(
      @PathVariable("subjectId") @NotNull(message = "Subject id is required") Long subjectId,
      @RequestBody SubjectDTO subjectDTO,
      @AuthenticationPrincipal User user
  ) {
    if (subjectDTO == null) {
      return;
    }
    if (user.getLearner() == null || user.getLearner().getLearningGroup() == null) {
      throw new NoLearnerGroupException();
    }

    LearningGroup group = user.getLearner().getLearningGroup();
    subjectService.updateSubject(subjectId, subjectDTO, group);
  }

  @DeleteMapping("/subject/{subjectId}/delete")
  @PreAuthorize("hasRole('HEADMAN')")
  public void deleteSubject(
      @PathVariable("subjectId") @NotNull(message = "Subject id is required") Long subjectId,
      @AuthenticationPrincipal User user
  ) {
    if (user.getLearner() == null || user.getLearner().getLearningGroup() == null) {
      throw new NoLearnerGroupException();
    }

    LearningGroup group = user.getLearner().getLearningGroup();
    subjectService.deleteSubject(subjectId, group);
  }
}
