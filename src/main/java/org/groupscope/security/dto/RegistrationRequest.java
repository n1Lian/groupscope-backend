package org.groupscope.security.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.groupscope.security.entity.User;

@Data
@EqualsAndHashCode(callSuper = true)
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

    public boolean isValid() {
        return (login != null && login.length() != 0) && (password != null && password.length() != 0);
    }

    @Override
    public String toString() {
        return "RegistrationRequest {" +
                "login = '" + login + '\'' +
                ", password = '" + password + '\'' +
                '}';
    }
}
