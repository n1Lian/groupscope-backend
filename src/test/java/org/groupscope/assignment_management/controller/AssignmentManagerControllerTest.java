package org.groupscope.assignment_management.controller;

import org.groupscope.assignment_management.services.AssignmentManagerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AssignmentManagerControllerTest {

    @Mock
    private AssignmentManagerService assignmentManagerService;

    @InjectMocks
    private AssignmentManagerController groupScopeController;

    @Test
    void getSubjects() {

    }
}