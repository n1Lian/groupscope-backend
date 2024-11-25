package org.groupscope.security.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.groupscope.security.entity.User;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RegistrationRequest extends AuthRequest {

    private String learnerName;

    private String learnerLastname;

    private String inviteCode;

    private Long nureGroupId;

    public User toUser() {
        User user = new User();
        user.setPassword(this.getPassword());
        user.setLogin(this.getLogin());

        return user;
    }
}
