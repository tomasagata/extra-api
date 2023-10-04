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
import org.mojodojocasahouse.extra.controller.ExpensesController;
import org.mojodojocasahouse.extra.dto.ApiError;
import org.mojodojocasahouse.extra.dto.ExpenseDTO;
import org.mojodojocasahouse.extra.exception.InvalidSessionTokenException;
import org.mojodojocasahouse.extra.exception.handler.UserAuthenticationExceptionHandler;
import org.mojodojocasahouse.extra.model.ExtraExpense;
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

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
public class ExpensesControllerListingTest {
    private MockMvc mvc;

    private JacksonTester<ApiError> jsonApiError;

    private JacksonTester<List<ExpenseDTO>> jsonExpenseDtoList;

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
    public void testListingExpenseWithCredentialsReturnsSuccessfulResponse() throws Exception {
        // Setup - data
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
        List<ExtraExpense> expectedExpensesList = List.of(
                new ExtraExpense(linkedUser, "A concept", new BigDecimal("10.12"), Date.valueOf("2022-12-09"))
        );
        List<ExpenseDTO> expectedResponse = List.of(
                new ExpenseDTO(null, null, "A concept", new BigDecimal("10.12"), Date.valueOf("2022-12-09"))
        );

        // Setup - Expectations
        given(authService.getUserBySessionToken(any())).willReturn(linkedUser);
        given(expenseService.getAllExpensesByUserId(any())).willReturn(expectedExpensesList);

        // exercise
        MockHttpServletResponse response = getExpensesWithCookie(sessionCookie);

        // Verify
        Assertions.assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        Assertions.assertThat(response.getContentAsString()).isEqualTo(jsonExpenseDtoList.write(expectedResponse).getJson());
    }


    @Test
    public void testListingExpensesWithInvalidCredentialsThrowsError() throws Exception {
        // Setup - data
        Cookie sessionCookie = new Cookie(
                "JSESSIONID",
                "123e4567-e89b-12d3-a456-426655440000"
        );
        ApiError expectedError = new ApiError(
                HttpStatus.UNAUTHORIZED,
                "Authentication Error",
                "Session is invalid or expired"
        );

        // Setup - expectations
        doThrow(new InvalidSessionTokenException()).when(authService).validateAuthentication(any());

        // exercise
        MockHttpServletResponse response = getExpensesWithCookie(sessionCookie);

        // Verify
        assertThatResponseReturnsError(response, expectedError);
    }


    @Test
    public void testListingExpensesWithNoCookieThrowsError() throws Exception {
        // Setup - data
        ApiError expectedError = new ApiError(
                HttpStatus.UNAUTHORIZED,
                "Authentication Error",
                "Required cookie 'JSESSIONID' for method parameter type UUID is not present"
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

    private void assertThatResponseReturnsError(MockHttpServletResponse response, ApiError expectedApiError) throws Exception {
        ApiError actualApiError = jsonApiError.parse(response.getContentAsString()).getObject();

        Assertions.assertThat(actualApiError.getMessage()).isEqualTo(expectedApiError.getMessage());
        Assertions.assertThat(actualApiError.getStatus()).isEqualTo(expectedApiError.getStatus());
        Assertions.assertThat(actualApiError.getErrors().toArray()).containsExactlyInAnyOrder(expectedApiError.getErrors().toArray());
    }
}
