package org.mojodojocasahouse.extra.tests.securitylayer.expensescontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.apache.commons.codec.binary.Base64;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mojodojocasahouse.extra.configuration.SecurityConfiguration;
import org.mojodojocasahouse.extra.controller.ExpensesController;
import org.mojodojocasahouse.extra.dto.responses.ApiError;
import org.mojodojocasahouse.extra.dto.responses.ApiResponse;
import org.mojodojocasahouse.extra.dto.requests.ExpenseAddingRequest;
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

import java.math.BigDecimal;
import java.sql.Date;

@WebMvcTest(ExpensesController.class)
@Import({
        SecurityConfiguration.class,
        DelegatingBasicAuthenticationEntryPoint.class,
        ExtraUserDetailsService.class
})
public class AddExpenseEndpointSecurityTest {

    @Autowired
    private MockMvc mvc;

    private JacksonTester<ApiError> jsonApiError;

    private JacksonTester<ApiResponse> jsonApiResponse;

    @MockBean
    public AuthenticationService authService;

    @MockBean
    public ExtraUserRepository userRepository;

    @MockBean
    public ExpenseService expenseService;

    @Autowired
    public ExpensesController controller;

    @BeforeEach
    public void setup() {
        JacksonTester.initFields(this, new ObjectMapper());
    }


    @Test
    public void testAddingNewExpenseWithInvalidSessionCookieThrowsError() throws Exception {
        // Setup - data
        ExpenseAddingRequest request = new ExpenseAddingRequest(
                "test",
                new BigDecimal(100),
                Date.valueOf("2018-12-09"),
                "test",
                (short) 1
        );
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
        MockHttpServletResponse response = postExpenseAddToControllerWithCookie(request, sessionCookie);

        // Verify
        assertThatResponseReturnsError(response, expectedError);
    }

    @Test
    public void testAddingNewExpenseWithInvalidUsernameAndPasswordThrowsError() throws Exception {
        // Setup - data
        ExpenseAddingRequest request = new ExpenseAddingRequest(
                "test",
                new BigDecimal(100),
                Date.valueOf("2018-12-09"),
                "test",
                (short) 1
        );
        ApiError expectedError = new ApiError(
                HttpStatus.UNAUTHORIZED,
                "Authentication Error",
                "Bad credentials"
        );

        // exercise
        MockHttpServletResponse response = postExpenseAddToControllerWithUsernameAndPassword(
                request,
                "user",
                "pass"
        );

        // Verify
        assertThatResponseReturnsError(response, expectedError);
    }


    @Test
    @WithAnonymousUser
    public void testAddingExpenseWithNoCookieThrowsError() throws Exception {
        // Setup - data
        ExpenseAddingRequest request = new ExpenseAddingRequest(
                "test",
                new BigDecimal(100),
                Date.valueOf("2018-12-09"),
                "Test",
                (short) 1
        );
        ApiError expectedError = new ApiError(
                HttpStatus.UNAUTHORIZED,
                "Authentication Error",
                "Full authentication is required to access this resource"
        );

        // exercise
        MockHttpServletResponse response = postExpenseAddToControllerNoCookie(request);

        // Verify
        assertThatResponseReturnsError(response, expectedError);
    }


    private MockHttpServletResponse postExpenseAddToControllerWithCookie(ExpenseAddingRequest request, Cookie cookie) throws Exception{
        return mvc.perform(MockMvcRequestBuilders.
                        post("/addExpense")
                        .cookie(cookie)
                        .content(asJsonString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL))
                .andReturn().getResponse();

    }

    private MockHttpServletResponse postExpenseAddToControllerNoCookie(ExpenseAddingRequest request) throws Exception {
        return mvc.perform(MockMvcRequestBuilders.
                        post("/addExpense")
                        .content(asJsonString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL))
                .andReturn().getResponse();
    }

    private MockHttpServletResponse postExpenseAddToControllerWithUsernameAndPassword(ExpenseAddingRequest request,
                                                                                      String username,
                                                                                      String password) throws Exception{
        return mvc.perform(MockMvcRequestBuilders.
                        get("/addExpense")
                        .content(asJsonString(request))
                        .contentType(MediaType.APPLICATION_JSON)
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

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
