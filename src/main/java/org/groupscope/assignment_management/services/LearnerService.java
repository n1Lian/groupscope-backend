package org.groupscope.assignment_management.services;

import org.groupscope.assignment_management.dto.LearnerDTO;
import org.groupscope.assignment_management.entity.Learner;
import org.groupscope.assignment_management.entity.LearningGroup;

/**
 * @author Mykyta Liashko
 */
public interface LearnerService {

    Learner addLearner(Learner learner, String inviteCode);

    Learner addFreeLearner(LearnerDTO learnerDTO);

    Learner manageEditorRole(Long learnerId, LearningGroup group, boolean active);

    Learner updateLearner(LearnerDTO learnerDTO, Learner learner);

    void deleteLearner(Learner learner);

}
