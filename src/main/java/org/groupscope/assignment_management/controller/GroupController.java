package org.groupscope.assignment_management.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.groupscope.assignment_management.dto.LearnerDTO;
import org.groupscope.assignment_management.dto.LearningGroupDTO;
import org.groupscope.assignment_management.dto.NewHeadmanUpdateRequest;
import org.groupscope.assignment_management.entity.LearningGroup;
import org.groupscope.assignment_management.services.GroupService;
import org.groupscope.assignment_management.services.LearnerService;
import org.groupscope.security.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author Mykyta Liashko
 */
@Slf4j
@CrossOrigin("*")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Validated
public class GroupController {

    private final GroupService groupService;

    private final LearnerService learnerService;

    @GetMapping("/group")
    @ResponseStatus(HttpStatus.OK)
    public LearningGroupDTO getGroup(
        @AuthenticationPrincipal User user
    ) {
        return groupService.getGroup(user.getLearner());
    }

    @PostMapping("/group/create")
    @ResponseStatus(HttpStatus.OK)
    public LearningGroup createGroup(
        @AuthenticationPrincipal User user
    ) {
        return groupService.createGroup(LearnerDTO.from(user.getLearner()));
    }

    @PostMapping("/group/join")
    @ResponseStatus(HttpStatus.OK)
    public void joinToGroup(
        @RequestParam @NotNull String inviteCode,
        @AuthenticationPrincipal User user
    ) {
        learnerService.addLearner(user.getLearner(), inviteCode);
    }


    @PostMapping("/group/editor")
    @PreAuthorize("hasRole('HEADMAN')")
    @ResponseStatus(HttpStatus.OK)
    public void processEditor(
        @RequestParam @NotNull Long learnerId,
        @RequestParam @NotNull Boolean active,
        @AuthenticationPrincipal User user
    ) {
        learnerService.manageEditorRole(learnerId, user.getLearner().getLearningGroup(), active);
    }

    @PatchMapping("/group/headman")
    @PreAuthorize("hasRole('HEADMAN')")
    @ResponseStatus(HttpStatus.OK)
    public void updateHeadmanOfGroup(
        @RequestBody NewHeadmanUpdateRequest request,
        @AuthenticationPrincipal User user
    ) {
        groupService.updateHeadmanOfGroup(user.getLearner().getLearningGroup(), request);
    }

}
