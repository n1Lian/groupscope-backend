package org.groupscope.assignment_management.controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import org.groupscope.assignment_management.dto.*;
import org.groupscope.assignment_management.entity.LearningRole;
import org.groupscope.assignment_management.entity.Task;
import org.groupscope.assignment_management.services.AssignmentManagerService;
import org.groupscope.files.services.FileManager;
import org.groupscope.security.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

import static org.groupscope.util.RoleUtil.getPriority;


@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api")
public class AssignmentManagerController {

    private final FileManager fileManager;

    private final AssignmentManagerService assignmentManagerService;

    @Autowired
    public AssignmentManagerController(AssignmentManagerService assignmentManagerService,
                                       FileManager fileManager) {
        this.assignmentManagerService = assignmentManagerService;
        this.fileManager = fileManager;
    }

    private boolean hasAccess(LearningRole check, LearningRole target) {
        int checkPriority = getPriority(check),
                targetPriority = getPriority(target);

        return (checkPriority >= targetPriority);
    }

    private void logRequestMapping(User user, HttpServletRequest request) {
        String requestPath = request.getRequestURI();
        log.info("{} {}\t{}", request.getMethod(), requestPath, user);
    }

    @GetMapping("/subject/all")
    public ResponseEntity<List<SubjectDTO>> getSubjects() {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            logRequestMapping(user, request);

            List<SubjectDTO> subjectsDto = assignmentManagerService.getAllSubjectDTOsByGroup(
                    user.getLearner().getLearningGroup(),
                    user.getLearner()
            );

            return ResponseEntity.ok(subjectsDto);
        } catch (NullPointerException | IllegalArgumentException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }

    @PostMapping("/subject/add")
    public ResponseEntity<HttpStatus> addSubject(@RequestBody SubjectDTO subjectDTO) {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            logRequestMapping(user, request);

            LearningRole userRole = user.getLearner().getRole();
            if(hasAccess(userRole, LearningRole.HEADMAN)) {
                assignmentManagerService.addSubject(subjectDTO, user.getLearner().getLearningGroup());

                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
            }
        } catch (NullPointerException | IllegalArgumentException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }

    @PatchMapping("/subject/patch")
    public ResponseEntity<HttpStatus> patchSubject(@RequestBody SubjectDTO subjectDTO) {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            logRequestMapping(user, request);

            LearningRole userRole = user.getLearner().getRole();
            if(hasAccess(userRole, LearningRole.HEADMAN)) {
                assignmentManagerService.updateSubject(subjectDTO, user.getLearner().getLearningGroup());

                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
            }
        } catch (NullPointerException | IllegalArgumentException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }

    @DeleteMapping("/subject/{subject-name}/delete")
    public ResponseEntity<HttpStatus> deleteSubject(@PathVariable("subject-name") String subjectName) {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            logRequestMapping(user, request);

            LearningRole userRole = user.getLearner().getRole();
            if(hasAccess(userRole, LearningRole.HEADMAN)) {
                assignmentManagerService.deleteSubject(subjectName, user.getLearner().getLearningGroup());

                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
            }
        } catch (NullPointerException | IllegalArgumentException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }

    @GetMapping("/subject/task/all")
    public ResponseEntity<List<TaskDTO>> getTasksOfSubject(@RequestParam("id") Long id) {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            logRequestMapping(user, request);

            List<TaskDTO> tasks = assignmentManagerService.getAllTaskDTOsOfSubject(
                    id,
                    user.getLearner().getLearningGroup(),
                    user.getLearner()
            );

            return ResponseEntity.ok(tasks);
        } catch (NullPointerException | IllegalArgumentException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }


    @PostMapping("/subject/{subject-name}/task/add")
    public ResponseEntity<HttpStatus> addTask(@RequestBody TaskDTO taskDTO,
                                              @PathVariable("subject-name") String subjectName) {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            logRequestMapping(user, request);

            LearningRole userRole = user.getLearner().getRole();
            if(hasAccess(userRole, LearningRole.EDITOR)) {
                assignmentManagerService.addTask(taskDTO, subjectName, user.getLearner().getLearningGroup());

                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
            }
        } catch (NullPointerException | IllegalArgumentException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }

    @PatchMapping("/subject/{subject-name}/task/patch")
    public ResponseEntity<HttpStatus> patchTask(@RequestBody TaskDTO taskDTO,
                                                @PathVariable("subject-name") String subjectName) {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            logRequestMapping(user, request);

            LearningRole userRole = user.getLearner().getRole();
            if(hasAccess(userRole, LearningRole.EDITOR)) {
                assignmentManagerService.updateTask(taskDTO, subjectName, user.getLearner().getLearningGroup());

                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
            }
        } catch (NullPointerException | IllegalArgumentException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }

    // Process dto class with only filled name field
    @DeleteMapping("/subject/{subject-name}/task/delete")
    public ResponseEntity<HttpStatus> deleteTask(@RequestBody TaskDTO taskDTO,
                                                 @PathVariable("subject-name") String subjectName) {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            logRequestMapping(user, request);

            LearningRole userRole = user.getLearner().getRole();
            if(hasAccess(userRole, LearningRole.HEADMAN)) {
                assignmentManagerService.deleteTask(subjectName, taskDTO, user.getLearner().getLearningGroup());

                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
            }
        } catch (NullPointerException | IllegalArgumentException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }

    @GetMapping("/student")
    public ResponseEntity<LearnerDTO> getStudent() {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            logRequestMapping(user, request);

            LearnerDTO learnerDTO = LearnerDTO.from(user.getLearner());

            return ResponseEntity.ok(learnerDTO);
        } catch (NullPointerException | IllegalArgumentException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }

    // It maybe will be in service in the future
    @PostMapping("/student/add")
    public ResponseEntity<HttpStatus> addStudent(@RequestBody LearnerDTO learnerDTO) {
        try {
            //groupScopeService.addStudent(learnerDTO, groupId);

            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
        } catch (NullPointerException | IllegalArgumentException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }

    @PatchMapping("/student/patch")
    public ResponseEntity<HttpStatus> updateStudent(@RequestBody LearnerDTO learnerDTO) {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            logRequestMapping(user, request);

            assignmentManagerService.updateLearner(learnerDTO, user.getLearner());

            return ResponseEntity.ok().build();
        } catch (NullPointerException | IllegalArgumentException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }

    //TODO added user deleting
    @DeleteMapping("/student/delete")
    public ResponseEntity<HttpStatus> deleteStudent() {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            logRequestMapping(user, request);

            // delete user
            assignmentManagerService.deleteLearner(user.getLearner());
            // delete learner
            return ResponseEntity.ok().build();
        } catch (NullPointerException | IllegalArgumentException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }

    @GetMapping("/group")
    public ResponseEntity<LearningGroupDTO> getGroup() {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            logRequestMapping(user, request);

            LearningGroupDTO learningGroupDTO = assignmentManagerService.getGroup(user.getLearner());

            return ResponseEntity.ok(learningGroupDTO);
        } catch (NullPointerException | IllegalArgumentException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }

    @PostMapping("/group/create")
    public ResponseEntity<HttpStatus> createGroup(@RequestBody LearningGroupDTO learningGroupDTO) {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            logRequestMapping(user, request);

            learningGroupDTO.setHeadmen(LearnerDTO.from(user.getLearner()));
            assignmentManagerService.addGroup(learningGroupDTO);

            return ResponseEntity.ok().build();
        } catch (NullPointerException | IllegalArgumentException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }

    @PostMapping("/group/join")
    public ResponseEntity<HttpStatus> joinToGroup(@RequestBody LearningGroupDTO learningGroupDTO) {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            logRequestMapping(user, request);

            assignmentManagerService.addLearner(user.getLearner(), learningGroupDTO.getInviteCode());

            return ResponseEntity.ok().build();
        } catch (NullPointerException | IllegalArgumentException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }

    }

    @GetMapping("/subject/{subject-name}/grade/all")
    public ResponseEntity<List<GradeDTO>> getGradesOfSubject(@PathVariable("subject-name") String subjectName) {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            logRequestMapping(user, request);

            List<GradeDTO> gradeDTOs = assignmentManagerService.getAllGradesOfSubject(subjectName, user.getLearner());

            return ResponseEntity.ok(gradeDTOs);
        } catch (NullPointerException | IllegalArgumentException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }

    @GetMapping("/group/{subject-name}/grade/all")
    public ResponseEntity<List<LearnerDTO>> getGradesOfSubjectFromGroup(@PathVariable("subject-name") String subjectName) {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            logRequestMapping(user, request);

            LearningRole userRole = user.getLearner().getRole();
            if(hasAccess(userRole, LearningRole.HEADMAN)) {
                List<LearnerDTO> learnerDTOs = assignmentManagerService
                        .getGradesOfSubjectFromGroup(subjectName, user.getLearner().getLearningGroup());

                return ResponseEntity.ok(learnerDTOs);
            } else {
                return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
            }
        } catch (NullPointerException | IllegalArgumentException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }

    @PostMapping("/group/editor")
    public ResponseEntity<HttpStatus> processEditor(@RequestParam(name = "id") Long learnerId,
                                                    @RequestParam(name = "active") Boolean active) {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            logRequestMapping(user, request);

            LearningRole userRole = user.getLearner().getRole();
            if(hasAccess(userRole, LearningRole.HEADMAN)) {
                assignmentManagerService.manageEditorRole(learnerId, user.getLearner().getLearningGroup(), active);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
            }
        } catch (NullPointerException | IllegalArgumentException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }

    @PatchMapping("/group/headman")
    public ResponseEntity<HttpStatus> updateHeadmanOfGroup(@RequestParam(name = "id") Long learnerId) {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            logRequestMapping(user, request);

            LearningRole userRole = user.getLearner().getRole();
            if(hasAccess(userRole, LearningRole.HEADMAN)) {
                assignmentManagerService.updateHeadmanOfGroup(user.getLearner().getLearningGroup(), learnerId);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
            }
        } catch (NullPointerException | IllegalArgumentException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }

    @PostMapping("/grade")
    public ResponseEntity<HttpStatus> updateGrade(@RequestBody GradeDTO gradeDTO) {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            logRequestMapping(user, request);

            assignmentManagerService.updateGrade(gradeDTO, user.getLearner());

            return ResponseEntity.ok().build();
        } catch (NullPointerException | IllegalArgumentException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }

    @PostMapping("/grades")
    public ResponseEntity<HttpStatus> updateGrades(@RequestBody List<GradeDTO> gradeDTOs) {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            logRequestMapping(user, request);

            gradeDTOs.forEach(gradeDTO -> assignmentManagerService.updateGrade(gradeDTO, user.getLearner()));

            return ResponseEntity.ok().build();
        } catch (NullPointerException | IllegalArgumentException e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }

    @GetMapping("/files")
    public ResponseEntity<List<byte[]>> getFiles(@RequestParam("task_id") Long taskId) {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            logRequestMapping(user, request);

            Task task = assignmentManagerService.getTaskById(taskId);

            String path = File.separator + task.getSubject().getGroup().getName()
                    + File.separator + task.getSubject().getName()
                    + File.separator + task.getName();

            List<File> files = fileManager.findFilesByPath(path);
            List<byte[]> bytes = fileManager.downloadFile(files);


            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + files.get(0).getName() + "\"")
                    .body(bytes);
        } catch (NullPointerException | IllegalArgumentException e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }

    @PostMapping("/files")
    public ResponseEntity<HttpStatus> uploadFiles(@RequestParam("task_id") Long taskId,
                                                  @RequestBody List<MultipartFile> files) {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            logRequestMapping(user, request);

            LearningRole userRole = user.getLearner().getRole();
            if(hasAccess(userRole, LearningRole.EDITOR)) {

                Task task = assignmentManagerService.getTaskById(taskId);

                String path = File.separator + task.getSubject().getGroup().getName()
                        + File.separator + task.getSubject().getName()
                        + File.separator + task.getName()
                        + File.separator;

                for (MultipartFile file : files) {
                    fileManager.uploadFile(path, file);
                }

                return ResponseEntity.ok().build();

            } else {
                return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
            }
        } catch (NullPointerException | IllegalArgumentException e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }
}
