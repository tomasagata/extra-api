package org.mojodojocasahouse.extra.tests.controller.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mojodojocasahouse.extra.configuration.SecurityConfiguration;
import org.mojodojocasahouse.extra.controller.AuthenticationController;
import org.mojodojocasahouse.extra.dto.ApiError;
import org.mojodojocasahouse.extra.dto.ApiResponse;
import org.mojodojocasahouse.extra.dto.ForgotPasswordRequest;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@WebMvcTest(AuthenticationController.class)
@Import({
        DelegatingBasicAuthenticationEntryPoint.class,
        SecurityConfiguration.class,
        ExtraUserDetailsService.class
})
public class ForgottenPasswordTest {

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
    public void testPostingForgottenPasswordAsAnonymousUserRequestReturnsSuccessfulResponse() throws Exception {
        // Setup - data
        ForgotPasswordRequest request = new ForgotPasswordRequest("some_email@me.com");
        ApiResponse expectedResponse = new ApiResponse("If user is registered, an email was sent. Check inbox");

        //Setup - expectations
        given(service.sendPasswordResetEmail(any())).willReturn(expectedResponse);

        // execute
        MockHttpServletResponse response = postForgottenPasswordRequest(request);

        // Validate
        Assertions.assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        Assertions.assertThat(response.getContentAsString()).isEqualTo(jsonApiResponse.write(expectedResponse).getJson());
    }

    @Test
    @WithMockUser
    public void testPostingForgottenPasswordAsLoggedInUserRequestReturnsSuccessfulResponse() throws Exception {
        // Setup - data
        ForgotPasswordRequest request = new ForgotPasswordRequest("some_email@me.com");
        ApiResponse expectedResponse = new ApiResponse("If user is registered, an email was sent. Check inbox");

        //Setup - expectations
        given(service.sendPasswordResetEmail(any())).willReturn(expectedResponse);

        // execute
        MockHttpServletResponse response = postForgottenPasswordRequest(request);

        // Validate
        Assertions.assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        Assertions.assertThat(response.getContentAsString()).isEqualTo(jsonApiResponse.write(expectedResponse).getJson());
    }

    private MockHttpServletResponse postForgottenPasswordRequest(ForgotPasswordRequest request)
            throws Exception {

        return mvc.perform(MockMvcRequestBuilders.
                        post("/auth/forgotten")
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

}
