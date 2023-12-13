package org.mojodojocasahouse.extra.tests.controller.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mojodojocasahouse.extra.configuration.SecurityConfiguration;
import org.mojodojocasahouse.extra.controller.AuthenticationController;
import org.mojodojocasahouse.extra.dto.ApiError;
import org.mojodojocasahouse.extra.dto.ApiResponse;
import org.mojodojocasahouse.extra.model.ExtraUser;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@WebMvcTest(AuthenticationController.class)
@Import({
        DelegatingBasicAuthenticationEntryPoint.class,
        SecurityConfiguration.class,
        ExtraUserDetailsService.class
})
public class LoginTest {

    @Autowired
    private MockMvc mvc;

    private JacksonTester<ApiError> jsonApiError;

    private JacksonTester<ApiResponse> jsonApiResponse;

    @MockBean
    public ExtraUserRepository userRepository;

    @MockBean
    public AuthenticationService authenticationService;

    @MockBean
    public PasswordEncoder passwordEncoder;

    @Autowired
    public AuthenticationController controller;

    @BeforeEach
    public void setup() {
        JacksonTester.initFields(this, new ObjectMapper());
    }

    @Test
    @WithMockUser
    public void testAccessingLoginEndpointWithValidCredentialsReturnsSuccessfulResponse() throws Exception {
        // Setup - data
        ExtraUser user = new ExtraUser(
                "michael",
                "jackson",
                "mj@me.com",
                "some_pass"
        );
        Map<String, String> expectedCredentials = new HashMap<>();
        expectedCredentials.put("firstName", "michael");
        expectedCredentials.put("lastName", "jackson");
        ApiResponse expectedResponse = new ApiResponse(
                "Login successful",
                expectedCredentials
        );
        Principal principal = Mockito.mock(Principal.class);

        // Setup - expectations
        given(authenticationService.getUserByPrincipal(any())).willReturn(user);

        // exercise
        MockHttpServletResponse response = loginWithPrincipal(principal);

        // Verify
        Assertions.assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        Assertions.assertThat(response.getContentAsString()).isEqualTo(jsonApiResponse.write(expectedResponse).getJson());
    }

    private MockHttpServletResponse loginWithPrincipal(Principal principal) throws Exception{

        return mvc.perform(MockMvcRequestBuilders.
                        post("/login")
                        .principal(principal)
                        .accept(MediaType.ALL))
                .andReturn().getResponse();

    }

}
