package org.mojodojocasahouse.extra.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
public class AuthenticationControllerProtectedTest {

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
    public void testAccessingProtectedResourceWithValidCredentialsIsSuccessful() throws Exception {
        // Setup - data
        Cookie sessionCookie = new Cookie(
                "JSESSIONID",
                "123e4567-e89b-12d3-a456-426655440000"
        );
        ApiResponse expectedResponse = new ApiResponse(
                "Authenticated and authorized!"
        );

        // exercise
        MockHttpServletResponse response = getProtectedResourceWithCookie(sessionCookie);

        // Verify
        Assertions.assertThat(response.getContentAsString()).isEqualTo(jsonApiResponse.write(expectedResponse).getJson());
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
        MockHttpServletResponse response = getProtectedResourceWithCookie(sessionCookie);

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
        MockHttpServletResponse response = getProtectedResourceNoCookie();

        // Verify
        assertThatResponseReturnsError(response, expectedError);
    }

    private MockHttpServletResponse getProtectedResourceNoCookie() throws Exception {
        return mvc.perform(MockMvcRequestBuilders.
                        get("/protected")
                        .accept(MediaType.ALL))
                .andReturn().getResponse();
    }

    private MockHttpServletResponse getProtectedResourceWithCookie(Cookie cookie) throws Exception {
        return mvc.perform(MockMvcRequestBuilders.
                        get("/protected")
                        .cookie(cookie)
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
