package org.mojodojocasahouse.extra.tests.controller.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mojodojocasahouse.extra.configuration.SecurityConfiguration;
import org.mojodojocasahouse.extra.controller.AuthenticationController;
import org.mojodojocasahouse.extra.dto.ApiError;
import org.mojodojocasahouse.extra.dto.ApiResponse;
import org.mojodojocasahouse.extra.dto.PasswordResetRequest;
import org.mojodojocasahouse.extra.exception.InvalidPasswordResetTokenException;
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
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@WebMvcTest(AuthenticationController.class)
@Import({
        DelegatingBasicAuthenticationEntryPoint.class,
        SecurityConfiguration.class,
        ExtraUserDetailsService.class
})
public class ResetPasswordTest {

    @Autowired
    private MockMvc mvc;

    private JacksonTester<ApiError> jsonApiError;

    private JacksonTester<ApiResponse> jsonApiResponse;

    @MockBean
    public ExtraUserRepository userRepository;

    @MockBean
    public AuthenticationService service;

    @Autowired
    public AuthenticationController controller;

    @BeforeEach
    public void setup() {
        JacksonTester.initFields(this, new ObjectMapper());
    }


    @Test
    @WithAnonymousUser
    public void testPostingPasswordResetRequestAsAnonymousUserReturnsSuccessfulResponse() throws Exception {
        // Setup - data
        PasswordResetRequest request = new PasswordResetRequest(UUID.randomUUID(),
                "somenewpass1!",
                "somenewpass1!"
        );
        ApiResponse expectedResponse = new ApiResponse("Password changed successfully");

        //Setup - expectations
        given(service.resetPassword(any())).willReturn(expectedResponse);

        // execute
        MockHttpServletResponse response = postPasswordResetRequest(request);

        // Validate
        Assertions.assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        Assertions.assertThat(response.getContentAsString()).isEqualTo(jsonApiResponse.write(expectedResponse).getJson());
    }

    @Test
    @WithMockUser
    public void testPostingInvalidPasswordResetRequestAsAnonymousInUserReturnsErrorResponse() throws Exception {
        // Setup - data
        PasswordResetRequest request = new PasswordResetRequest(UUID.randomUUID(),
                "somenewpass1!",
                "somenewpass1!"
        );
        ApiError expectedResponse = new ApiError(
                HttpStatus.UNAUTHORIZED,
                "Invalid Token Error",
                "Password reset token is invalid or expired"
        );

        //Setup - expectations
        given(service.resetPassword(any())).willThrow(new InvalidPasswordResetTokenException());

        // execute
        MockHttpServletResponse response = postPasswordResetRequest(request);

        // Validate
        assertThatResponseReturnsError(response, expectedResponse);
    }

    @Test
    @WithMockUser
    public void testPostingPasswordResetRequestAsLoggedInUserReturnsSuccessfulResponse() throws Exception {
        // Setup - data
        PasswordResetRequest request = new PasswordResetRequest(UUID.randomUUID(),
                "somenewpass1!",
                "somenewpass1!"
        );
        ApiResponse expectedResponse = new ApiResponse("Password changed successfully");

        //Setup - expectations
        given(service.resetPassword(any())).willReturn(expectedResponse);

        // execute
        MockHttpServletResponse response = postPasswordResetRequest(request);

        // Validate
        Assertions.assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        Assertions.assertThat(response.getContentAsString()).isEqualTo(jsonApiResponse.write(expectedResponse).getJson());
    }

    @Test
    @WithMockUser
    public void testPostingInvalidPasswordResetRequestAsLoggedInUserReturnsErrorResponse() throws Exception {
        // Setup - data
        PasswordResetRequest request = new PasswordResetRequest(UUID.randomUUID(),
                "somenewpass1!",
                "somenewpass1!"
        );
        ApiError expectedResponse = new ApiError(
                HttpStatus.UNAUTHORIZED,
                "Invalid Token Error",
                "Password reset token is invalid or expired"
        );

        //Setup - expectations
        given(service.resetPassword(any())).willThrow(new InvalidPasswordResetTokenException());

        // execute
        MockHttpServletResponse response = postPasswordResetRequest(request);

        // Validate
        assertThatResponseReturnsError(response, expectedResponse);
    }

    private MockHttpServletResponse postPasswordResetRequest(PasswordResetRequest request)
            throws Exception {

        return mvc.perform(MockMvcRequestBuilders.
                        post("/auth/forgotten/reset")
                        .content(asJsonString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL))
                .andReturn().getResponse();
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void assertThatResponseReturnsError(MockHttpServletResponse response, ApiError expectedApiError) throws Exception {
        ApiError actualApiError = jsonApiError.parse(response.getContentAsString()).getObject();

        Assertions.assertThat(actualApiError.getMessage()).isEqualTo(expectedApiError.getMessage());
        Assertions.assertThat(actualApiError.getStatus()).isEqualTo(expectedApiError.getStatus());
        Assertions.assertThat(actualApiError.getErrors().toArray()).containsExactlyInAnyOrder(expectedApiError.getErrors().toArray());
    }

}
