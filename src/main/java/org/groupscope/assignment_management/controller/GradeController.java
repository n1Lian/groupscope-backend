package org.groupscope.assignment_management.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.groupscope.assignment_management.dto.GradeDTO;
import org.groupscope.assignment_management.dto.LearnerDTO;
import org.groupscope.assignment_management.services.GradeService;
import org.groupscope.security.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
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
public class GradeController {

  private final GradeService gradeService;

  @GetMapping("/subject/{subjectId}/grade/all")
  @ResponseStatus(HttpStatus.OK)
  public List<GradeDTO> getGradesOfSubject(
      @PathVariable("subjectId") Long subjectId,
      @AuthenticationPrincipal User user
  ) {
    return gradeService.getAllGradesOfSubject(subjectId, user.getLearner());
  }

  @GetMapping("/group/{subjectId}/grade/all")
  @PreAuthorize("hasRole('HEADMAN')")
  @ResponseStatus(HttpStatus.OK)
  public List<LearnerDTO> getGradesOfSubjectFromGroup(
      @PathVariable("subjectId") Long subjectId,
      @AuthenticationPrincipal User user
  ) {
    return gradeService.getGradesOfSubjectFromGroup(subjectId, user.getLearner().getLearningGroup());
  }


  @PostMapping("/grade")
  public void updateGrade(
      @RequestBody GradeDTO gradeDTO,
      @AuthenticationPrincipal User user
  ) {
    gradeService.updateGrade(gradeDTO, user.getLearner());
  }

  @PostMapping("/grades")
  @ResponseStatus(HttpStatus.OK)
  public void updateGrades(
      @RequestBody List<GradeDTO> gradeDTOs,
      @AuthenticationPrincipal User user
  ) {
    gradeDTOs.forEach(gradeDTO -> gradeService.updateGrade(gradeDTO, user.getLearner()));
  }

}
