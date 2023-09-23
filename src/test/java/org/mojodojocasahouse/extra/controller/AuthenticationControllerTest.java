package org.mojodojocasahouse.extra.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.mojodojocasahouse.extra.dto.UserAuthenticationRequest;
import org.mojodojocasahouse.extra.dto.UserAuthenticationResponse;
import org.mojodojocasahouse.extra.dto.UserRegistrationRequest;
import org.mojodojocasahouse.extra.dto.UserRegistrationResponse;
import org.mojodojocasahouse.extra.exception.ExistingUserEmailException;
import org.mojodojocasahouse.extra.exception.InvalidCredentialsException;
import org.mojodojocasahouse.extra.exception.InvalidSessionTokenException;
import org.mojodojocasahouse.extra.exception.handler.UserAuthenticationExceptionHandler;
import org.mojodojocasahouse.extra.exception.handler.helper.ApiError;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mojodojocasahouse.extra.service.AuthenticationService;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    private MockMvc mvc;

    private JacksonTester<ApiError> jsonApiError;

    private JacksonTester<UserAuthenticationResponse> jsonAuthResponse;

    private JacksonTester<UserRegistrationResponse> jsonRegistrationResponse;

    @Mock
    public AuthenticationService service;

    @InjectMocks
    public AuthenticationController controller;

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

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
        UserRegistrationResponse registrationResponse = new UserRegistrationResponse(
                "User created successfully"
        );

        // Setup - expectations
        given(service.registerUser(userRegistrationRequest)).willReturn(registrationResponse);

        // exercise
        MockHttpServletResponse response = postUserRegistrationRequestToController(userRegistrationRequest);

        // verify
        Assertions.assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        Assertions.assertThat(response.getContentAsString()).isEqualTo(jsonRegistrationResponse.write(registrationResponse).getJson());
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
                Arrays.asList(
                        "passwordRepeat: Password must contain eight characters, one letter, one number and one of the following: @$!%*#?",
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
                Arrays.asList(
                        "passwordRepeat: Password must contain eight characters, one letter, one number and one of the following: @$!%*#?",
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

    @Test
    public void testAuthenticatingAsARegisteredUserReturnsASuccessfulResponse() throws Exception {
        // Setup - data
        UserAuthenticationRequest request = new UserAuthenticationRequest(
                "mj@me.com",
                "SomePassword1!"
        );
        UserAuthenticationResponse expectedResponse = new UserAuthenticationResponse(
                "Login Success"
        );
        Cookie expectedCookie = new Cookie(
                "JSESSIONID",
                "123e4567-e89b-12d3-a456-426655440000"
        );

        // Setup - expectations
        given(service.authenticateUser(request)).willReturn(Pair.of(expectedResponse, expectedCookie));

        // exercise
        MockHttpServletResponse response = postUserAuthenticationRequestToController(request);

        // Verify
        Assertions.assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        Assertions.assertThat(response.getContentAsString()).isEqualTo(jsonAuthResponse.write(expectedResponse).getJson());
    }

    @Test
    public void testAuthenticatingAsAnUnregisteredUserReturnsUnauthorizedResponse() throws Exception {
        // Setup - data
        UserAuthenticationRequest request = new UserAuthenticationRequest(
                "mj@me.com",
                "SomePassword1!"
        );
        ApiError expectedError = new ApiError(
                HttpStatus.UNAUTHORIZED,
                "User Authentication Error",
                "Invalid Authentication Credentials"
        );

        // Setup - expectations
        given(service.authenticateUser(request)).willThrow(new InvalidCredentialsException());

        // exercise
        MockHttpServletResponse response = postUserAuthenticationRequestToController(request);

        // Verify
        assertThatResponseReturnsError(response, expectedError);
    }

    @Test
    public void testPostingAMalformedRequestToAuthenticationEndpointThrowsError() throws Exception {
        // Setup - data
        String malformedRequest = "{exampleMalformedRequest";
        ApiError expectedError = new ApiError(
                HttpStatus.BAD_REQUEST,
                "Failed to read request",
                "Malformed Request"
        );

        // exercise
        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.
                        post("/login")
                        .content(malformedRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL))
                .andReturn().getResponse();

        // Verify
        assertThatResponseReturnsError(response, expectedError);
    }

    @Test
    public void testPostingAMalformedRequestToRegistrationEndpointThrowsError() throws Exception {
        // Setup - data
        String malformedRequest = "{exampleMalformedRequest";
        ApiError expectedError = new ApiError(
                HttpStatus.BAD_REQUEST,
                "Failed to read request",
                "Malformed Request"
        );

        // exercise
        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.
                        post("/register")
                        .content(malformedRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL))
                .andReturn().getResponse();

        // Verify
        assertThatResponseReturnsError(response, expectedError);
    }

    @Test
    public void testAccessingProtectedResourceWithNoCookieThrowsError() throws Exception {
        // Setup - data
        ApiError expectedError = new ApiError(
                HttpStatus.UNAUTHORIZED,
                "Authorization Error",
                "Required cookie 'JSESSIONID' for method parameter type UUID is not present"
        );

        // exercise
        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.
                        get("/protected")
                        .accept(MediaType.ALL))
                .andReturn().getResponse();

        // Verify
        assertThatResponseReturnsError(response, expectedError);
    }

    @Test
    public void testAccessingProtectedResourceWithInvalidCredentialsThrowsError() throws Exception {
        // Setup - data
        Cookie sessionCookie = new Cookie(
                "JSESSIONID",
                "123e4567-e89b-12d3-a456-426655440000"
        );
        ApiError expectedError = new ApiError(
                HttpStatus.UNAUTHORIZED,
                "User Authentication Error",
                "Session is invalid or expired"
        );

        // Setup - expectations
        doThrow(new InvalidSessionTokenException()).when(service).validateAuthentication(any());

        // exercise
        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.
                        get("/protected")
                        .cookie(sessionCookie)
                        .accept(MediaType.ALL))
                .andReturn().getResponse();

        // Verify
        assertThatResponseReturnsError(response, expectedError);
    }


    @Test
    public void testAccessingProtectedResourceWithValidCredentialsIsSuccessful() throws Exception {
        // Setup - data
        Cookie sessionCookie = new Cookie(
                "JSESSIONID",
                "123e4567-e89b-12d3-a456-426655440000"
        );

        // exercise
        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.
                        get("/protected")
                        .cookie(sessionCookie)
                        .accept(MediaType.ALL))
                .andReturn().getResponse();

        // Verify
        Assertions.assertThat(response.getContentAsString()).isEqualTo("Authenticated and authorized!");
    }

    private MockHttpServletResponse postUserAuthenticationRequestToController(UserAuthenticationRequest userAuthenticationRequest) throws Exception {
        return mvc.perform(MockMvcRequestBuilders.
                        post("/login")
                        .content(asJsonString(userAuthenticationRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL))
                .andReturn().getResponse();
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

}