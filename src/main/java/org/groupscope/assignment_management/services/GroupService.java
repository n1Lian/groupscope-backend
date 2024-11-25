package org.groupscope.assignment_management.services;

import org.groupscope.assignment_management.dto.LearnerDTO;
import org.groupscope.assignment_management.dto.LearningGroupDTO;
import org.groupscope.assignment_management.dto.NewHeadmanUpdateRequest;
import org.groupscope.assignment_management.entity.Learner;
import org.groupscope.assignment_management.entity.LearningGroup;

/**
 * @author Mykyta Liashko
 */
public interface GroupService {


    LearningGroupDTO getGroup(Learner learner);

    LearningGroup createGroup(LearnerDTO headman);

    LearningGroup getGroupById(Long id);

    LearningGroup getGroupByInviteCode(String inviteCode);

    LearningGroup updateHeadmanOfGroup(LearningGroup group, NewHeadmanUpdateRequest request);

}
