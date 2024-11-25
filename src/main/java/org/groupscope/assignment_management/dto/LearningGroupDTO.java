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
                .map(subject -> {
                    var subjectDTO = SubjectDTO.from(subject);
                    subjectDTO.setGroup(dto.toString());
                    return subjectDTO;
                })
                .toList();

        dto.setSubjects(subjectDTOList);

        List<LearnerDTO> learnerDTOList = learningGroup.getLearners().stream()
                .map(learner -> {
                    var learnerDTO = LearnerDTO.from(learner);
                    learnerDTO.setLearningGroup(dto.toString());
                    return learnerDTO;
                })
                .toList();

        dto.setLearners(learnerDTOList);
        return dto;
    }

    public LearningGroup toLearningGroup() {
        LearningGroup learningGroup = new LearningGroup(this.getName());
        learningGroup.setHeadmen(this.getHeadmen().toLearner());
        learningGroup.setId(this.getId());

        if(!CollectionUtils.isEmpty(this.learners)) {
            List<Learner> learnersList = this.learners.stream()
                    .map(learnerDTO -> {
                        var learner = learnerDTO.toLearner();
                        learner.setLearningGroup(learningGroup);
                        return learner;
                    })
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