package org.mojodojocasahouse.extra.tests.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mojodojocasahouse.extra.controller.AuthenticationController;
import org.mojodojocasahouse.extra.dto.ApiError;
import org.mojodojocasahouse.extra.dto.ApiResponse;
import org.mojodojocasahouse.extra.dto.UserRegistrationRequest;
import org.mojodojocasahouse.extra.exception.ExistingUserEmailException;
import org.mojodojocasahouse.extra.exception.handler.UserAuthenticationExceptionHandler;
import org.mojodojocasahouse.extra.service.AuthenticationService;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class AuthenticationControllerSignupTest {

    private MockMvc mvc;

    private JacksonTester<ApiError> jsonApiError;

    private JacksonTester<ApiResponse> jsonApiResponse;

    @Mock
    public AuthenticationService service;

    @InjectMocks
    public AuthenticationController controller;


    @BeforeEach
    public void setup() {
        JacksonTester.initFields(this, new ObjectMapper());

        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new UserAuthenticationExceptionHandler())
                .build();
    }


    @Test
    public void testPostingUnregisteredUserShouldReturnSuccessResponse() throws Exception {
        // Setup - data
        UserRegistrationRequest userRegistrationRequest = new UserRegistrationRequest(
                "Michael",
                "Jordan",
                "mj@me.com",
                "Somepassword1!",
                "Somepassword1!"
        );
        ApiResponse registrationResponse = new ApiResponse(
                "User created successfully"
        );

        // Setup - expectations
        given(service.registerUser(userRegistrationRequest)).willReturn(registrationResponse);

        // exercise
        MockHttpServletResponse response = postUserRegistrationRequestToController(userRegistrationRequest);

        // verify
        Assertions.assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        Assertions.assertThat(response.getContentAsString()).isEqualTo(jsonApiResponse.write(registrationResponse).getJson());
    }

    @Test
    public void testRegisteringANewUserWithSpecialCharacteredFirstNameReturnsBadRequest() throws Exception {
        // Setup - data
        UserRegistrationRequest userRegistrationRequest = new UserRegistrationRequest(
                "@llein",
                "Jordan",
                "mj@me.com",
                "Somepassword1!",
                "Somepassword1!"
        );
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                "Data validation error",
                "firstName: First name must not be left blank or contain special characters or numbers"
        );

        // exercise
        MockHttpServletResponse response = postUserRegistrationRequestToController(userRegistrationRequest);

        // verify
        assertThatResponseReturnsError(response, apiError);
    }

    @Test
    public void testRegisteringANewUserWithEmptyFirstNameReturnsBadRequest() throws Exception {
        // Setup - data
        UserRegistrationRequest userRegistrationRequest = new UserRegistrationRequest(
                "",
                "Jordan",
                "mj@me.com",
                "Somepassword1!",
                "Somepassword1!"
        );
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                "Data validation error",
                "firstName: First name must not be left blank or contain special characters or numbers"
        );

        // exercise
        MockHttpServletResponse response = postUserRegistrationRequestToController(userRegistrationRequest);

        // verify
        assertThatResponseReturnsError(response, apiError);
    }

    @Test
    public void testRegisteringANewUserWithNullFirstNameReturnsBadRequest() throws Exception {
        // Setup - data
        UserRegistrationRequest userRegistrationRequest = new UserRegistrationRequest(
                null,
                "Jordan",
                "mj@me.com",
                "Somepassword1!",
                "Somepassword1!"
        );
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                "Data validation error",
                "firstName: First name is mandatory"
        );

        // exercise
        MockHttpServletResponse response = postUserRegistrationRequestToController(userRegistrationRequest);

        // verify
        assertThatResponseReturnsError(response, apiError);
    }

    @Test
    public void testRegisteringANewUserWithSpecialCharacteredLastNameReturnsBadRequest() throws Exception {
        // Setup - data
        UserRegistrationRequest userRegistrationRequest = new UserRegistrationRequest(
                "Klein",
                "J0rdan",
                "mj@me.com",
                "Somepassword1!",
                "Somepassword1!"
        );
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                "Data validation error",
                "lastName: Last name must not be left blank or contain special characters or numbers"
        );

        // exercise
        MockHttpServletResponse response = postUserRegistrationRequestToController(userRegistrationRequest);

        // verify
        assertThatResponseReturnsError(response, apiError);
    }

    @Test
    public void testRegisteringANewUserWithEmptyLastNameReturnsBadRequest() throws Exception {
        // Setup - data
        UserRegistrationRequest userRegistrationRequest = new UserRegistrationRequest(
                "Michael",
                "",
                "mj@me.com",
                "Somepassword1!",
                "Somepassword1!"
        );
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                "Data validation error",
                "lastName: Last name must not be left blank or contain special characters or numbers"
        );

        // exercise
        MockHttpServletResponse response = postUserRegistrationRequestToController(userRegistrationRequest);

        // verify
        assertThatResponseReturnsError(response, apiError);
    }

    @Test
    public void testRegisteringANewUserWithNullLastNameReturnsBadRequest() throws Exception {
        // Setup - data
        UserRegistrationRequest userRegistrationRequest = new UserRegistrationRequest(
                "Michael",
                null,
                "mj@me.com",
                "Somepassword1!",
                "Somepassword1!"
        );
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                "Data validation error",
                "lastName: Last name is mandatory"
        );

        // exercise
        MockHttpServletResponse response = postUserRegistrationRequestToController(userRegistrationRequest);

        // verify
        assertThatResponseReturnsError(response, apiError);
    }

    @Test
    public void testRegisteringANewUserWithAWrongEmailReturnsBadRequest() throws Exception {
        // Setup - data
        UserRegistrationRequest userRegistrationRequest = new UserRegistrationRequest(
                "Michael",
                "Jordan",
                "@.",
                "Somepassword1!",
                "Somepassword1!"
        );
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                "Data validation error",
                "email: Email must be valid"
        );

        // exercise
        MockHttpServletResponse response = postUserRegistrationRequestToController(userRegistrationRequest);

        // verify
        assertThatResponseReturnsError(response, apiError);
    }

    @Test
    public void testRegisteringANewUserWithABlankEmailReturnsBadRequest() throws Exception {
        // Setup - data
        UserRegistrationRequest userRegistrationRequest = new UserRegistrationRequest(
                "Michael",
                "Jordan",
                "",
                "Somepassword1!",
                "Somepassword1!"
        );
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                "Data validation error",
                "email: Email must not be left blank"
        );

        // exercise
        MockHttpServletResponse response = postUserRegistrationRequestToController(userRegistrationRequest);

        // verify
        assertThatResponseReturnsError(response, apiError);
    }

    @Test
    public void testRegisteringANewUserWithNullEmailReturnsBadRequest() throws Exception {
        // Setup - data
        UserRegistrationRequest userRegistrationRequest = new UserRegistrationRequest(
                "Michael",
                "Jordan",
                null,
                "Somepassword1!",
                "Somepassword1!"
        );
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                "Data validation error",
                Arrays.asList(
                        "email: Email is mandatory",
                        "email: Email must not be left blank"
                )
        );

        // exercise
        MockHttpServletResponse response = postUserRegistrationRequestToController(userRegistrationRequest);

        // verify
        assertThatResponseReturnsError(response, apiError);
    }

    @Test
    public void testRegisteringANewUserWithAnExistingEmailReturnsConflict() throws Exception {
        // Setup - data
        UserRegistrationRequest userRegistrationRequest = new UserRegistrationRequest(
                "Michael",
                "Jordan",
                "mj@me.com",
                "Somepassword1!",
                "Somepassword1!"
        );
        ApiError apiError = new ApiError(
                HttpStatus.CONFLICT,
                "User registration conflict",
                "User email already registered"
        );

        // Setup - expectations
        given(service.registerUser(userRegistrationRequest)).willThrow(new ExistingUserEmailException());

        // exercise
        MockHttpServletResponse response = postUserRegistrationRequestToController(userRegistrationRequest);

        // verify
        assertThatResponseReturnsError(response, apiError);
    }


    @Test
    public void testRegisteringANewUserWithWeakPasswordReturnsBadRequest() throws Exception {
        // Setup - data
        UserRegistrationRequest userRegistrationRequest = new UserRegistrationRequest(
                "Michael",
                "Jordan",
                "mj@me.com",
                "password",
                "password"
        );
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                "Data validation error",
                List.of(
                        "password: Password must contain eight characters, one letter, one number and one of the following: @$!%*#?"
                )
        );

        // exercise
        MockHttpServletResponse response = postUserRegistrationRequestToController(userRegistrationRequest);

        // verify
        assertThatResponseReturnsError(response, apiError);
    }

    @Test
    public void testRegisteringANewUserWithEmptyPasswordReturnsBadRequest() throws Exception {
        // Setup - data
        UserRegistrationRequest userRegistrationRequest = new UserRegistrationRequest(
                "Michael",
                "Jordan",
                "mj@me.com",
                "",
                ""
        );
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                "Data validation error",
                List.of(
                        "password: Password must contain eight characters, one letter, one number and one of the following: @$!%*#?"
                )
        );

        // exercise
        MockHttpServletResponse response = postUserRegistrationRequestToController(userRegistrationRequest);

        // verify
        assertThatResponseReturnsError(response, apiError);
    }

    @Test
    public void testRegisteringANewUserWithNullPasswordReturnsBadRequest() throws Exception {
        // Setup - data
        UserRegistrationRequest userRegistrationRequest = new UserRegistrationRequest(
                "Michael",
                "Jordan",
                "mj@me.com",
                null,
                null
        );
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                "Data validation error",
                Arrays.asList(
                        "passwordRepeat: Repeating password is mandatory",
                        "password: Password is mandatory"
                )
        );

        // exercise
        MockHttpServletResponse response = postUserRegistrationRequestToController(userRegistrationRequest);

        // verify
        assertThatResponseReturnsError(response, apiError);
    }

    @Test
    public void testRegisteringANewUserWithIncorrectlyRepeatedPasswordReturnsBadRequest() throws Exception {
        // Setup - data
        UserRegistrationRequest userRegistrationRequest = new UserRegistrationRequest(
                "Michael",
                "Jordan",
                "mj@me.com",
                "Somepassword1!",
                "Anotherpassword2!"
        );
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                "Data validation error",
                "userRegistrationRequest: Passwords must match"
        );

        // exercise
        MockHttpServletResponse response = postUserRegistrationRequestToController(userRegistrationRequest);

        // verify
        assertThatResponseReturnsError(response, apiError);
    }


    private MockHttpServletResponse postUserRegistrationRequestToController(UserRegistrationRequest userRegistrationRequest) throws Exception {
        return mvc.perform(MockMvcRequestBuilders.
                        post("/register")
                        .content(asJsonString(userRegistrationRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL))
                .andReturn().getResponse();
    }

    private void assertThatResponseReturnsError(MockHttpServletResponse response, ApiError expectedApiError) throws Exception {
        ApiError actualApiError = jsonApiError.parse(response.getContentAsString()).getObject();

        Assertions.assertThat(actualApiError.getMessage()).isEqualTo(expectedApiError.getMessage());
        Assertions.assertThat(actualApiError.getStatus()).isEqualTo(expectedApiError.getStatus());
        Assertions.assertThat(actualApiError.getErrors().toArray()).containsExactlyInAnyOrder(expectedApiError.getErrors().toArray());
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
