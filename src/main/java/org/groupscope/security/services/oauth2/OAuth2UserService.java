package org.groupscope.security.services.oauth2;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
//import com.google.api.client.json.jackson2.JacksonFactory;
import lombok.extern.slf4j.Slf4j;
import org.groupscope.assignment_management.dao.AssignmentManagerDAOImpl;
import org.groupscope.security.entity.Provider;
import org.groupscope.security.entity.User;
import org.groupscope.security.services.RefreshTokenService;
import org.groupscope.security.services.auth.UserService;
import org.groupscope.security.dto.OAuth2Request;
import org.groupscope.security.dto.RegistrationRequest;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
@Slf4j
public class OAuth2UserService extends DefaultOAuth2UserService  {

    private final UserService userService;

    private final GoogleIdTokenVerifier idTokenVerifier;

    private final RefreshTokenService refreshTokenService;

    @Autowired
    public OAuth2UserService(@Value("${spring.security.oauth2.client.registration.google.client-id}") String clientId,
                             UserService userService,
                             RefreshTokenService refreshTokenService) {
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;

        NetHttpTransport transport = new NetHttpTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
        this.idTokenVerifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Collections.singletonList(clientId))
                .build();
    }

    @Transactional
    public User loginOAuthGoogle(OAuth2Request request) {
        if(request.getIdToken() != null) {
            RegistrationRequest registrationRequest = verifyIDToken(request.getIdToken());
            if (registrationRequest == null) {
                throw new IllegalArgumentException("Token not verified");
            }
            User user = new User();
            user.setLogin(registrationRequest.getLogin());
            registrationRequest.setInviteCode(request.getInviteCode());
            registrationRequest.setGroupName(request.getGroupName());

            User foundedUser = userService.findByLogin(user.getLogin());

            if(foundedUser == null) {
                foundedUser = userService.saveUser(user, registrationRequest, Provider.GOOGLE);
            }

            Hibernate.initialize(foundedUser.getLearner().getGrades());

            if(foundedUser.getLearner().getLearningGroup() != null)
                AssignmentManagerDAOImpl.removeDuplicates(foundedUser.getLearner().getLearningGroup().getSubjects());

            return foundedUser;
        } else
            throw new NullPointerException("IdToken is null");
    }

    private RegistrationRequest verifyIDToken(String idToken) {
        try {
            GoogleIdToken idTokenObj = idTokenVerifier.verify(idToken);
            if (idTokenObj == null) {
                return null;
            }
            GoogleIdToken.Payload payload = idTokenObj.getPayload();

            RegistrationRequest request = new RegistrationRequest();
            request.setLearnerName((String) payload.get("given_name"));
            request.setLearnerLastname((String) payload.get("family_name"));
            request.setLogin(payload.getEmail());

            if(verifyMailDomain(request.getLogin()))
                return request;
            else
                return null;

        } catch (GeneralSecurityException | IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }
    
    private boolean verifyMailDomain(String mail) {
        return mail.endsWith("@nure.ua");
    }
}
