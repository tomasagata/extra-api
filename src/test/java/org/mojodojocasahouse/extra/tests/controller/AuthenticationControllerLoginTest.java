package org.mojodojocasahouse.extra.tests.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
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
import org.mojodojocasahouse.extra.dto.UserAuthenticationRequest;
import org.mojodojocasahouse.extra.exception.InvalidCredentialsException;
import org.mojodojocasahouse.extra.exception.handler.UserAuthenticationExceptionHandler;
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
import java.util.List;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class AuthenticationControllerLoginTest {

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
    public void testAuthenticatingAsARegisteredUserReturnsASuccessfulResponse() throws Exception {
        // Setup - data
        UserAuthenticationRequest request = new UserAuthenticationRequest(
                "mj@me.com",
                "SomePassword1!"
        );
        ApiResponse expectedResponse = new ApiResponse(
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
        Assertions.assertThat(response.getContentAsString()).isEqualTo(jsonApiResponse.write(expectedResponse).getJson());
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
                "Authentication Error",
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
    public void testAuthenticatingUserWithAWrongEmailReturnsBadRequest() throws Exception {
        // Setup - data
        UserAuthenticationRequest request = new UserAuthenticationRequest(
                "@.",
                "Somepassword1!"
        );
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                "Data validation error",
                "email: Email must be valid"
        );

        // exercise
        MockHttpServletResponse response = postUserAuthenticationRequestToController(request);

        // verify
        assertThatResponseReturnsError(response, apiError);
    }

    @Test
    public void testAuthenticatingUserWithABlankEmailReturnsBadRequest() throws Exception {
        // Setup - data
        UserAuthenticationRequest request = new UserAuthenticationRequest(
                "",
                "Somepassword1!"
        );
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                "Data validation error",
                "email: Email must not be left blank"
        );

        // exercise
        MockHttpServletResponse response = postUserAuthenticationRequestToController(request);

        // verify
        assertThatResponseReturnsError(response, apiError);
    }

    @Test
    public void testAuthenticatingUserWithNullEmailReturnsBadRequest() throws Exception {
        // Setup - data
        UserAuthenticationRequest request = new UserAuthenticationRequest(
                null,
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
        MockHttpServletResponse response = postUserAuthenticationRequestToController(request);

        // verify
        assertThatResponseReturnsError(response, apiError);
    }

    @Test
    public void testAuthenticatingUserWithSpecialCharacteredPasswordReturnsBadRequest() throws Exception {
        // Setup - data
        UserAuthenticationRequest userRegistrationRequest = new UserAuthenticationRequest(
                "mj@me.com",
                "' or 1=1"
        );
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                "Data validation error",
                List.of(
                        "password: Password can only contain letters, numbers or the following: @$!%*#?"
                )
        );

        // exercise
        MockHttpServletResponse response = postUserAuthenticationRequestToController(userRegistrationRequest);

        // verify
        assertThatResponseReturnsError(response, apiError);
    }

    @Test
    public void testAuthenticatingUserWithEmptyPasswordReturnsBadRequest() throws Exception {
        // Setup - data
        UserAuthenticationRequest userRegistrationRequest = new UserAuthenticationRequest(
                "mj@me.com",
                ""
        );
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                "Data validation error",
                List.of(
                        "password: Password can only contain letters, numbers or the following: @$!%*#?"
                )
        );

        // exercise
        MockHttpServletResponse response = postUserAuthenticationRequestToController(userRegistrationRequest);

        // verify
        assertThatResponseReturnsError(response, apiError);
    }

    @Test
    public void testAuthenticatingUserWithNullPasswordReturnsBadRequest() throws Exception {
        // Setup - data
        UserAuthenticationRequest userRegistrationRequest = new UserAuthenticationRequest(
                "mj@me.com",
                null
        );
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                "Data validation error",
                List.of(
                        "password: Password is mandatory"
                )
        );

        // exercise
        MockHttpServletResponse response = postUserAuthenticationRequestToController(userRegistrationRequest);

        // verify
        assertThatResponseReturnsError(response, apiError);
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


    private MockHttpServletResponse postUserAuthenticationRequestToController(UserAuthenticationRequest userAuthenticationRequest) throws Exception {
        return mvc.perform(MockMvcRequestBuilders.
                        post("/login")
                        .content(asJsonString(userAuthenticationRequest))
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
