package org.groupscope.assignment_management.dto;

import lombok.Data;
import org.groupscope.assignment_management.entity.Learner;
import org.groupscope.assignment_management.entity.LearningGroup;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class LearningGroupDTO {

    private Long id;

    private String name;

    private String inviteCode;

    private LearnerDTO headmen;

    private List<SubjectDTO> subjects;

    private List<LearnerDTO> learners;

    public LearningGroupDTO() {
    }

    public LearningGroupDTO(String name, LearnerDTO headmen) {
        this.name = name;
        this.headmen = headmen;
    }

    public static LearningGroupDTO from(LearningGroup learningGroup) {
        LearningGroupDTO dto = new LearningGroupDTO();
        dto.setId(learningGroup.getId());
        dto.setName(learningGroup.getName());
        dto.setInviteCode(learningGroup.getInviteCode());
        dto.setHeadmen(LearnerDTO.from(learningGroup.getHeadmen()));

        List<SubjectDTO> subjectDTOList = learningGroup.getSubjects().stream()
                .map(SubjectDTO::from)
                .peek(subjectDTO -> subjectDTO.setGroup(dto.toString()))
                .collect(Collectors.toList());

        dto.setSubjects(subjectDTOList);

        List<LearnerDTO> learnerDTOList = learningGroup.getLearners().stream()
                .map(LearnerDTO::from)
                .peek(learnerDTO -> learnerDTO.setLearningGroup(dto.toString()))
                .collect(Collectors.toList());

        dto.setLearners(learnerDTOList);
        return dto;
    }

    public LearningGroup toLearningGroup() {
        LearningGroup learningGroup = new LearningGroup(this.getName());
        learningGroup.setHeadmen(this.getHeadmen().toLearner());
        learningGroup.setId(this.getId());

        if(!CollectionUtils.isEmpty(this.learners)) {
            List<Learner> learnersList = this.learners.stream()
                    .map(LearnerDTO::toLearner)
                    .peek(learner -> learner.setLearningGroup(learningGroup))
                    .collect(Collectors.toList());

            learningGroup.getHeadmen().setLearningGroup(learningGroup);
            learnersList.add(learningGroup.getHeadmen());

            learningGroup.setLearners(learnersList);
        }

        return learningGroup;
    }

    @Override
    public String toString() {
        return name;
    }
}