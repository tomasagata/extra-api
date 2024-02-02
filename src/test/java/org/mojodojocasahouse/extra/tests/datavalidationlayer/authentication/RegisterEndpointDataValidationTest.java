package org.mojodojocasahouse.extra.tests.datavalidationlayer.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mojodojocasahouse.extra.configuration.SecurityConfiguration;
import org.mojodojocasahouse.extra.controller.AuthenticationController;
import org.mojodojocasahouse.extra.dto.responses.ApiError;
import org.mojodojocasahouse.extra.dto.responses.ApiResponse;
import org.mojodojocasahouse.extra.dto.requests.UserRegistrationRequest;
import org.mojodojocasahouse.extra.exception.ExistingUserEmailException;
import org.mojodojocasahouse.extra.repository.ExtraUserRepository;
import org.mojodojocasahouse.extra.security.DelegatingBasicAuthenticationEntryPoint;
import org.mojodojocasahouse.extra.security.ExtraUserDetailsService;
import org.mojodojocasahouse.extra.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.BDDMockito.given;

@WebMvcTest(AuthenticationController.class)
@Import({
        DelegatingBasicAuthenticationEntryPoint.class,
        SecurityConfiguration.class,
        ExtraUserDetailsService.class
})
public class RegisterEndpointDataValidationTest {

    @Autowired
    private MockMvc mvc;

    private JacksonTester<ApiError> jsonApiError;

    private JacksonTester<ApiResponse> jsonApiResponse;

    @MockBean
    public ExtraUserRepository userRepository;

    @MockBean
    public AuthenticationService service;

    @MockBean
    public PasswordEncoder passwordEncoder;

    @Autowired
    public AuthenticationController controller;

    @BeforeEach
    public void setup() {
        JacksonTester.initFields(this, new ObjectMapper());
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
                List.of(
                        "firstName: First name is mandatory",
                        "firstName: First name must not be left blank or contain special characters or numbers"
                )
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
                List.of(
                        "lastName: Last name is mandatory",
                        "lastName: Last name must not be left blank or contain special characters or numbers"
                )
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
                        "password: Password is mandatory",
                        "passwordRepeat: Repeating password is mandatory",
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
    public void testPostingToRegisterEndpointWithAMalformedRequestReturnsErrorMessage() throws Exception {
        // Setup - data
        String request = "{' OR 1=1";
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                "Failed to read request",
                "Malformed Request"
        );

        // exercise
        MockHttpServletResponse response = postStringToRegisterEndpoint(request);

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

    private MockHttpServletResponse postStringToRegisterEndpoint(String request) throws Exception {
        return mvc.perform(MockMvcRequestBuilders.
                        post("/register")
                        .content(asJsonString(request))
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
