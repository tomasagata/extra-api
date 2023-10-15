package org.mojodojocasahouse.extra.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;

import java.math.BigDecimal;
import java.sql.Date;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mojodojocasahouse.extra.dto.ApiError;
import org.mojodojocasahouse.extra.dto.ApiResponse;
import org.mojodojocasahouse.extra.dto.ExpenseAddingRequest;
import org.mojodojocasahouse.extra.exception.InvalidSessionTokenException;
import org.mojodojocasahouse.extra.exception.handler.UserAuthenticationExceptionHandler;
import org.mojodojocasahouse.extra.model.ExtraUser;
import org.mojodojocasahouse.extra.service.AuthenticationService;
import org.mojodojocasahouse.extra.service.ExpenseService;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
public class ExpensesControllerAddingTest {

    private MockMvc mvc;

    private JacksonTester<ApiError> jsonApiError;

    private JacksonTester<ApiResponse> jsonApiResponse;

    @Mock
    public AuthenticationService authService;

    @Mock
    public ExpenseService expenseService;

    @InjectMocks
    public ExpensesController controller;

    @BeforeEach
    public void setup() {
        JacksonTester.initFields(this, new ObjectMapper());

        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new UserAuthenticationExceptionHandler())
                .build();
    }


    @Test
    public void testAddingExpenseWithCredentialsReturnsSuccessfulResponse() throws Exception {
        // Setup - data
        ExpenseAddingRequest request = new ExpenseAddingRequest(
            "test",
            new BigDecimal(100),
            Date.valueOf("2018-12-09")
        );
        Cookie sessionCookie = new Cookie(
                "JSESSIONID",
                "123e4567-e89b-12d3-a456-426655440000"
        );
        ExtraUser linkedUser = new ExtraUser(
                "M",
                "J",
                "mj@me.com",
                "Somepassword"
        );
        ApiResponse expectedResponse = new ApiResponse(
                "Expense added succesfully!"
        );

        // Setup - Expectations
        given(authService.getUserBySessionToken(any())).willReturn(linkedUser);
        given(expenseService.addExpense(any(), any())).willReturn(expectedResponse);

        // exercise
        MockHttpServletResponse response = postExpenseAddToControllerWithCookie(request, sessionCookie);

        // Verify
        Assertions.assertThat(response.getContentAsString()).isEqualTo(jsonApiResponse.write(expectedResponse).getJson());
    }


    @Test
    public void testAddingNewExpenseWithInvalidCredentialsThrowsError() throws Exception {
        // Setup - data
        ExpenseAddingRequest request = new ExpenseAddingRequest(
                "test",
                new BigDecimal(100),
                Date.valueOf("2018-12-09")
        );
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
        doThrow(new InvalidSessionTokenException()).when(authService).validateAuthentication(any());

        // exercise
        MockHttpServletResponse response = postExpenseAddToControllerWithCookie(request, sessionCookie);

        // Verify
        assertThatResponseReturnsError(response, expectedError);
    }


    @Test
    public void testAddingExpenseWithNoCookieThrowsError() throws Exception {
        // Setup - data
        ExpenseAddingRequest request = new ExpenseAddingRequest(
                "test",
                new BigDecimal(100),
                Date.valueOf("2018-12-09")
        );
        ApiError expectedError = new ApiError(
                HttpStatus.UNAUTHORIZED,
                "Authorization Error",
                "Required cookie 'JSESSIONID' for method parameter type UUID is not present"
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
