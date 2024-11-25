package org.groupscope.assignment_management.services.impl;

import static java.util.Objects.requireNonNull;

import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.groupscope.assignment_management.dao.repositories.SubjectRepository;
import org.groupscope.assignment_management.dto.SubjectCreateRequest;
import org.groupscope.assignment_management.dto.SubjectDTO;
import org.groupscope.assignment_management.entity.LearningGroup;
import org.groupscope.assignment_management.entity.Subject;
import org.groupscope.assignment_management.services.SubjectService;
import org.groupscope.exceptions.DuplicateEntityException;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * @author Mykyta Liashko
 */
@Primary
@Slf4j
@Service
@RequiredArgsConstructor
@Validated
public class SubjectServiceImpl implements SubjectService {

  private final SubjectRepository subjectRepository;

  @Override
  @Transactional
  public Subject addSubject(
      @NotNull LearningGroup group,
      @NotNull SubjectCreateRequest createRequest
  ) {
    Subject subject = new Subject(
        1L,
        createRequest.getName(),
        createRequest.getBrief(),
        false,
        new ArrayList<>(),
        group
    );

    boolean isSubjectExists = group.getSubjects().stream()
        .anyMatch(s ->
            s.getName().equals(subject.getName())
            && s.getBrief().equals(subject.getBrief())
        );

    if (isSubjectExists) {
      throw new DuplicateEntityException(
          "Subject: " + subject.getBrief() + " has been already existing in group = "
              + group.getName());
    }

    subjectRepository.save(subject);
    return subject;
  }

  @Override
  public Subject getSubjectByName(String subjectName, LearningGroup group) {
    requireNonNull(group, "Learning group is null");
    requireNonNull(subjectName, "Subject name is null");

    Subject subject = group.getSubjects().stream()
        .filter(s -> s.getName().equals(subjectName))
        .findFirst().orElse(null);

    return requireNonNull(subject, "Subject with name = " + subjectName + "not found");
  }

  @Override
  @Transactional
  public Subject updateSubject(
      @NotNull Long subjectId,
      @NotNull SubjectDTO subjectDTO,
      @NotNull LearningGroup group
  ) {
    Subject subject = subjectRepository.findById(subjectId).orElseThrow(
        () -> new IllegalArgumentException("Subject not found with id: " + subjectId));

    if (subjectDTO.getName() != null) {
      subject.setName(subjectDTO.getName());
    }
    if (subjectDTO.getBrief() != null) {
      subject.setBrief(subjectDTO.getBrief());
    }
    if (subjectDTO.getIsExam() != null) {
      subject.setIsExam(subjectDTO.getIsExam());
    }

    return subjectRepository.save(subject);
  }

  @Override
  @Transactional
  public void deleteSubject(
      @NotNull Long subjectId,
      @NotNull LearningGroup group
  ) {

    Subject subject = subjectRepository.findById(subjectId).orElseThrow(
        () -> new IllegalArgumentException("Subject not found with id: " + subjectId));

    requireNonNull(subject, "Subject not found with name: " + subjectId);
    subjectRepository.delete(subject);
  }

  @Override
  public List<SubjectDTO> getAllSubjectsByGroup(LearningGroup group) {
    requireNonNull(group, "Group doesnt exist");
    List<Subject> subjects = group.getSubjects();

    if (subjects == null) {
      return new ArrayList<>();
    }

    return subjects.stream()
        .map(SubjectDTO::from)
        .toList();
  }
}
