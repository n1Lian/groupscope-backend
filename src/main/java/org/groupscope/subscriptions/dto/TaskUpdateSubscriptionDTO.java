package org.groupscope.subscriptions.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.groupscope.subscriptions.entity.SubscriptionType;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskUpdateSubscriptionDTO {

    private Long id;

    private SubscriptionType type;

    private Long learningGroupId;

    private List<Long> subjectIds;
}