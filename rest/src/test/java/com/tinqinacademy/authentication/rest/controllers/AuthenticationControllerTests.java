package com.tinqinacademy.authentication.rest.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.tinqinacademy.authentication.api.RestApiRoutes;
import com.tinqinacademy.authentication.api.operations.changepassword.ChangePasswordInput;
import com.tinqinacademy.authentication.api.operations.confirmregistration.ConfirmRegistrationInput;
import com.tinqinacademy.authentication.api.operations.demote.DemoteInput;
import com.tinqinacademy.authentication.api.operations.login.LoginInput;
import com.tinqinacademy.authentication.api.operations.promote.PromoteInput;
import com.tinqinacademy.authentication.api.operations.recoverpassword.RecoverPasswordInput;
import com.tinqinacademy.authentication.api.operations.register.RegisterInput;
import com.tinqinacademy.authentication.core.security.HashingUtil;
import com.tinqinacademy.authentication.core.security.JwtUtil;
import com.tinqinacademy.authentication.persistence.model.User;
import com.tinqinacademy.authentication.persistence.model.enums.UserRole;
import com.tinqinacademy.authentication.persistence.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY, connection = EmbeddedDatabaseConnection.H2)
@ActiveProfiles("test")
@Transactional
public class AuthenticationControllerTests {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private HashingUtil hashingUtil;

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig()
                    .withUser("testuser@example.com", "password123"))
            .withPerMethodLifecycle(true);

    private User testUser;
    private String testUserToken;

    private User adminUser;
    private String adminUserToken;

    private String generateTestToken(User user) {
        return jwtUtil.generateToken(user.getId(), user.getUserRole());
    }

    @BeforeEach
    public void setup() {
        testUser = User.builder()
                .username("testuser")
                .firstName("Ivan")
                .lastName("Ivanov")
                .email("testuser@example.com")
                .password(hashingUtil.hashPassword("password123"))
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .phoneNumber("1234567890")
                .isActivated(true)
                .userRole(UserRole.USER)
                .build();
        testUser = userRepository.save(testUser);

        testUserToken = "Bearer " + generateTestToken(testUser);

        adminUser = User.builder()
                .username("adminuser")
                .email("admin@example.com")
                .password("adminpass123")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .phoneNumber("9876543210")
                .isActivated(true)
                .userRole(UserRole.ADMIN)
                .firstName("Admin")
                .lastName("User")
                .build();
        adminUser = userRepository.save(adminUser);

        adminUserToken = "Bearer " + generateTestToken(adminUser);
    }

    @Test
    public void testConfirmRegistrationBadRequest() throws Exception {
        ConfirmRegistrationInput nullValuesInput = ConfirmRegistrationInput.builder()
                .confirmationCode(null)
                .build();

        mvc.perform(post(RestApiRoutes.AUTH_CONFIRM_REGISTRATION)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(nullValuesInput)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testRecoverPasswordOk() throws Exception {
        RecoverPasswordInput nullEmailInput = RecoverPasswordInput.builder()
                .email(testUser.getEmail())
                .build();

        mvc.perform(post(RestApiRoutes.AUTH_RECOVER_PASSWORD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(nullEmailInput)))
                .andExpect(status().isOk());
    }

    @Test
    public void testRecoverPasswordOkWrongEmail() throws Exception {
        RecoverPasswordInput nullEmailInput = RecoverPasswordInput.builder()
                .email("wrongemail@example.com")
                .build();

        mvc.perform(post(RestApiRoutes.AUTH_RECOVER_PASSWORD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(nullEmailInput)))
                .andExpect(status().isOk());
    }

    @Test
    public void testPromoteSuccess() throws Exception {
        PromoteInput input = PromoteInput.builder()
                .userId(testUser.getId().toString())
                .build();

        mvc.perform(post(RestApiRoutes.AUTH_PROMOTE)
                        .header("Authorization", adminUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(input)))
                .andExpect(status().isOk());
    }

    @Test
    public void testPromoteForbidden() throws Exception {
        PromoteInput input = PromoteInput.builder()
                .userId(adminUser.getId().toString())
                .build();

        mvc.perform(post(RestApiRoutes.AUTH_PROMOTE)
                        .header("Authorization", testUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(input)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testPromoteNotFound() throws Exception {
        PromoteInput input = PromoteInput.builder()
                .userId(UUID.randomUUID().toString())
                .build();

        mvc.perform(post(RestApiRoutes.AUTH_PROMOTE)
                        .header("Authorization", adminUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(input)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testPromoteBadRequest() throws Exception {
        PromoteInput input = PromoteInput.builder()
                .userId("123")
                .build();

        mvc.perform(post(RestApiRoutes.AUTH_PROMOTE)
                        .header("Authorization", adminUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDemoteSuccess() throws Exception {
        User anotherAdmin = User.builder()
                .username("anotheradmin")
                .email("another@admin.com")
                .password("adminpass456")
                .dateOfBirth(LocalDate.of(1985, 1, 1))
                .phoneNumber("5555555555")
                .isActivated(true)
                .userRole(UserRole.ADMIN)
                .firstName("Another")
                .lastName("Admin")
                .build();
        anotherAdmin = userRepository.save(anotherAdmin);

        DemoteInput input = DemoteInput.builder()
                .userId(anotherAdmin.getId().toString())
                .build();

        mvc.perform(post(RestApiRoutes.AUTH_DEMOTE)
                        .header("Authorization", adminUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(input)))
                .andExpect(status().isOk());
    }

    @Test
    public void testDemoteForbidden() throws Exception {
        DemoteInput input = DemoteInput.builder()
                .userId(adminUser.getId().toString())
                .build();

        mvc.perform(post(RestApiRoutes.AUTH_DEMOTE)
                        .header("Authorization", testUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(input)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testDemoteNotFound() throws Exception {
        DemoteInput input = DemoteInput.builder()
                .userId(UUID.randomUUID().toString())
                .build();

        mvc.perform(post(RestApiRoutes.AUTH_DEMOTE)
                        .header("Authorization", adminUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(input)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDemoteBadRequest() throws Exception {
        DemoteInput input = DemoteInput.builder()
                .userId("123")
                .build();

        mvc.perform(post(RestApiRoutes.AUTH_DEMOTE)
                        .header("Authorization", adminUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testRegisterOk() throws Exception {
        RegisterInput input = RegisterInput.builder()
                .username("newuser")
                .email("newuser@example.com")
                .password("newpassword123")
                .firstName("Ivan")
                .lastName("Ivanov")
                .phoneNumber("0987654321")
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .build();

        mvc.perform(post(RestApiRoutes.AUTH_REGISTER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(input)))
                .andExpect(status().isOk());
    }

    @Test
    public void testRegisterBadRequestSameUsername() throws Exception {
        RegisterInput input = RegisterInput.builder()
                .username("testuser")
                .email("newuser@example.com")
                .password("newpassword123")
                .firstName("Ivan")
                .lastName("Ivanov")
                .phoneNumber("0987654321")
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .build();

        mvc.perform(post(RestApiRoutes.AUTH_REGISTER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(input)))
                .andExpect(status().isOk());
    }

    @Test
    public void testRegisterBadRequest() throws Exception {
        RegisterInput input = RegisterInput.builder()
                .username("testuser")
                .email("testuser@example.com")
                .password("password123")
                .build();

        mvc.perform(post(RestApiRoutes.AUTH_REGISTER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testLoginOk() throws Exception {
        LoginInput input = LoginInput.builder()
                .username("testuser")
                .password("password123")
                .build();

        mvc.perform(post(RestApiRoutes.AUTH_LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(input)))
                .andExpect(status().isOk());
    }

    @Test
    public void testLoginBadRequest() throws Exception {
        LoginInput input = LoginInput.builder()
                .username("")
                .password("wrongpassword")
                .build();

        mvc.perform(post(RestApiRoutes.AUTH_LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testLoginUnauthorized() throws Exception {
        LoginInput input = LoginInput.builder()
                .username("testuser")
                .password("wrongpassword")
                .build();

        mvc.perform(post(RestApiRoutes.AUTH_LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(input)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testAuthenticateOk() throws Exception {
        mvc.perform(post(RestApiRoutes.AUTH_AUTHENTICATE)
                        .header("Authorization", testUserToken))
                .andExpect(status().isOk());
    }

    @Test
    public void testAuthenticateUnauthorized() throws Exception {
        mvc.perform(post(RestApiRoutes.AUTH_AUTHENTICATE)
                        .header("Authorization", "invalid_token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testChangePasswordOk() throws Exception {
        ChangePasswordInput input = ChangePasswordInput.builder()
                .email(testUser.getEmail())
                .oldPassword("password123")
                .newPassword("newpassword123")
                .build();

        mvc.perform(post(RestApiRoutes.AUTH_CHANGE_PASSWORD)
                        .header("Authorization", testUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(input)))
                .andExpect(status().isOk());
    }

    @Test
    public void testChangePasswordUnauthorizedWrongPassword() throws Exception {
        ChangePasswordInput input = ChangePasswordInput.builder()
                .email(testUser.getEmail())
                .oldPassword("wrongpassword")
                .newPassword("newpassword123")
                .build();

        mvc.perform(post(RestApiRoutes.AUTH_CHANGE_PASSWORD)
                        .header("Authorization", testUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(input)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testChangePasswordBadRequestSamePassword() throws Exception {
        ChangePasswordInput input = ChangePasswordInput.builder()
                .email(testUser.getEmail())
                .oldPassword("password123")
                .newPassword("password123")
                .build();

        mvc.perform(post(RestApiRoutes.AUTH_CHANGE_PASSWORD)
                        .header("Authorization", testUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testChangePasswordBadRequestWrongEmail() throws Exception {
        ChangePasswordInput input = ChangePasswordInput.builder()
                .email("wrong email")
                .oldPassword("password123")
                .newPassword("newpassword123")
                .build();

        mvc.perform(post(RestApiRoutes.AUTH_CHANGE_PASSWORD)
                        .header("Authorization", testUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testLogoutOk() throws Exception {
        mvc.perform(post(RestApiRoutes.AUTH_LOGOUT)
                        .header("Authorization", testUserToken))
                .andExpect(status().isOk());
    }

    @Test
    public void testLogoutUnauthorized() throws Exception {
        mvc.perform(post(RestApiRoutes.AUTH_LOGOUT)
                        .header("Authorization", "invalidToken"))
                .andExpect(status().isUnauthorized());
    }




}
