package org.mojodojocasahouse.extra.tests.securitylayer.expensescontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.apache.commons.codec.binary.Base64;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mojodojocasahouse.extra.configuration.SecurityConfiguration;
import org.mojodojocasahouse.extra.controller.TransactionController;
import org.mojodojocasahouse.extra.dto.responses.ApiError;
import org.mojodojocasahouse.extra.dto.model.ExpenseDTO;
import org.mojodojocasahouse.extra.repository.ExtraUserRepository;
import org.mojodojocasahouse.extra.security.DelegatingBasicAuthenticationEntryPoint;
import org.mojodojocasahouse.extra.security.ExtraUserDetailsService;
import org.mojodojocasahouse.extra.service.AuthenticationService;
import org.mojodojocasahouse.extra.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

@WebMvcTest(TransactionController.class)
@Import({
        SecurityConfiguration.class,
        DelegatingBasicAuthenticationEntryPoint.class,
        ExtraUserDetailsService.class
})
public class GetExpensesEndpointSecurityTest {

    @Autowired
    private MockMvc mvc;

    private JacksonTester<ApiError> jsonApiError;

    private JacksonTester<List<ExpenseDTO>> jsonExpenseDtoList;

    @MockBean
    public AuthenticationService authService;

    @MockBean
    public ExtraUserRepository userRepository;

    @MockBean
    public ExpenseService expenseService;

    @Autowired
    public TransactionController controller;

    @BeforeEach
    public void setup() {
        JacksonTester.initFields(this, new ObjectMapper());
    }

    @Test
    public void testListingExpensesWithInvalidSessionCookieThrowsError() throws Exception {
        // Setup - data
        Cookie sessionCookie = new Cookie(
                "JSESSIONID",
                "123e4567-e89b-12d3-a456-426655440000"
        );
        ApiError expectedError = new ApiError(
                HttpStatus.UNAUTHORIZED,
                "Authentication Error",
                "Full authentication is required to access this resource"
        );

        // exercise
        MockHttpServletResponse response = getExpensesWithCookie(sessionCookie);

        // Verify
        assertThatResponseReturnsError(response, expectedError);
    }

    @Test
    public void testListingExpensesWithInvalidUsernameAndPasswordThrowsError() throws Exception {
        // Setup - data
        ApiError expectedError = new ApiError(
                HttpStatus.UNAUTHORIZED,
                "Authentication Error",
                "Bad credentials"
        );

        // exercise
        MockHttpServletResponse response = getExpensesWithUsernameAndPassword("user", "pass");

        // Verify
        assertThatResponseReturnsError(response, expectedError);
    }


    @Test
    @WithAnonymousUser
    public void testListingExpensesWithNoCookieThrowsError() throws Exception {
        // Setup - data
        ApiError expectedError = new ApiError(
                HttpStatus.UNAUTHORIZED,
                "Authentication Error",
                "Full authentication is required to access this resource"
        );

        // exercise
        MockHttpServletResponse response = getExpensesNoCookie();

        // Verify
        assertThatResponseReturnsError(response, expectedError);
    }

    private MockHttpServletResponse getExpensesWithCookie(Cookie cookie) throws Exception{
        return mvc.perform(MockMvcRequestBuilders.
                        get("/getMyExpenses")
                        .cookie(cookie)
                        .accept(MediaType.ALL))
                .andReturn().getResponse();

    }

    private MockHttpServletResponse getExpensesNoCookie() throws Exception {
        return mvc.perform(MockMvcRequestBuilders.
                        get("/getMyExpenses")
                        .accept(MediaType.ALL))
                .andReturn().getResponse();
    }

    private MockHttpServletResponse getExpensesWithUsernameAndPassword(String username,
                                                                       String password) throws Exception {
        return mvc.perform(MockMvcRequestBuilders.
                        get("/getMyExpenses")
                        .header("Authorization",
                                "Basic " + Base64
                                        .encodeBase64String((username + ":" + password).getBytes()))
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
