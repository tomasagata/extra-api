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
import org.mojodojocasahouse.extra.exception.InvalidSessionTokenException;
import org.mojodojocasahouse.extra.exception.handler.UserAuthenticationExceptionHandler;
import org.mojodojocasahouse.extra.service.AuthenticationService;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
public class AuthenticationControllerLogoutTest {

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
    public void testLoggingOutOfAnExistingSessionIsSuccessful() throws Exception{
        // Setup - data
        Cookie requestCookie = new Cookie(
                "JSESSIONID",
                "123e4567-e89b-12d3-a456-426655440000"
        );
        ApiResponse expectedResponse = new ApiResponse("User logout successful");
        Cookie expectedCookie = new Cookie("JSESSIONID", null);
        expectedCookie.setMaxAge(0);

        // Setup - expectations

        // exercise
        MockHttpServletResponse response = requestLogoutToControllerWithCookie(requestCookie);

        // Verify
        Assertions.assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        Assertions.assertThat(response.getContentAsString()).isEqualTo(jsonApiResponse.write(expectedResponse).getJson());
        Assertions.assertThat(response.getCookie("JSESSIONID")).isEqualTo(expectedCookie);
    }

    @Test
    public void testLoggingOutOfAnInvalidSessionReturnsError() throws Exception{
        // Setup - data
        Cookie requestCookie = new Cookie(
                "JSESSIONID",
                "123e4567-e89b-12d3-a456-426655440000"
        );
        ApiError expectedResponse = new ApiError(
                HttpStatus.UNAUTHORIZED,
                "Authentication Error",
                "Session is invalid or expired"
        );

        // Setup - expectations
        doThrow(new InvalidSessionTokenException()).when(service).validateAuthentication(any());

        // exercise
        MockHttpServletResponse response = requestLogoutToControllerWithCookie(requestCookie);

        // Verify
        assertThatResponseReturnsError(response, expectedResponse);
    }

    @Test
    public void testLoggingOutWithNoCredentialsReturnsError() throws Exception{
        // Setup - data
        ApiError expectedResponse = new ApiError(
                HttpStatus.UNAUTHORIZED,
                "Authentication Error",
                "Required cookie 'JSESSIONID' for method parameter type UUID is not present"
        );

        // Setup - expectations

        // exercise
        MockHttpServletResponse response = requestLogoutToControllerNoCookie();

        // Verify
        assertThatResponseReturnsError(response, expectedResponse);
    }


    private MockHttpServletResponse requestLogoutToControllerWithCookie(Cookie cookie) throws Exception {
        return mvc.perform(MockMvcRequestBuilders.
                        get("/logout")
                        .cookie(cookie)
                        .accept(MediaType.ALL))
                .andReturn().getResponse();
    }

    private MockHttpServletResponse requestLogoutToControllerNoCookie() throws Exception{
        return mvc.perform(MockMvcRequestBuilders.
                        get("/logout")
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
