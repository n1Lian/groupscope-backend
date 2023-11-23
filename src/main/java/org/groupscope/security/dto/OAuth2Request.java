package org.groupscope.security.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class OAuth2Request {

    @NotEmpty
    private String idToken;

    private String learnerName;

    private String learnerLastname;

    private String inviteCode;

    private String groupName;

    public RegistrationRequest toRegistrationRequest() {
        RegistrationRequest request = new RegistrationRequest();
        request.setLearnerName(this.getLearnerName());
        request.setLearnerLastname(this.getLearnerLastname());
        request.setInviteCode(this.getInviteCode());
        request.setGroupName(this.getGroupName());
        return request;
    }
}
