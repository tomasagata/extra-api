package org.mojodojocasahouse.extra.tests.controller.expenses;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mojodojocasahouse.extra.configuration.SecurityConfiguration;
import org.mojodojocasahouse.extra.controller.TransactionController;
import org.mojodojocasahouse.extra.dto.responses.ApiError;
import org.mojodojocasahouse.extra.dto.responses.ApiResponse;
import org.mojodojocasahouse.extra.model.ExtraUser;
import org.mojodojocasahouse.extra.repository.ExtraUserRepository;
import org.mojodojocasahouse.extra.repository.TransactionRepository;
import org.mojodojocasahouse.extra.security.DelegatingBasicAuthenticationEntryPoint;
import org.mojodojocasahouse.extra.security.ExtraUserDetailsService;
import org.mojodojocasahouse.extra.service.AuthenticationService;
import org.mojodojocasahouse.extra.service.ExpenseService;
import org.mojodojocasahouse.extra.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@WebMvcTest(TransactionController.class)
@Import({
        DelegatingBasicAuthenticationEntryPoint.class,
        SecurityConfiguration.class,
        ExtraUserDetailsService.class
})
public class ExpensesDeletingTest {

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

    @MockBean
    public TransactionService transactionService;

    @Autowired
    public TransactionController controller;

    @BeforeEach
    public void setup() {
        JacksonTester.initFields(this, new ObjectMapper());
    }

    @Test
    @WithMockUser
    public void testDeletingAnExistingAndOwnedExpenseReturnsSuccessfulResponse() throws Exception {
        // Setup - data
        ExtraUser linkedUser = new ExtraUser(
                "M",
                "J",
                "mj@me.com",
                "Somepassword1!"
        );
        ApiResponse expectedResponse = new ApiResponse(
                "Expense deleted successfully"
        );

        // Setup - Expectations
        given(authService.getUserByPrincipal(any())).willReturn(linkedUser);
        given(expenseService.existsById(any())).willReturn(true);
        given(expenseService.isOwner(any(), any())).willReturn(true);

        // exercise
        MockHttpServletResponse response = postExpenseDeletingRequest(1L);

        // Verify
        Assertions.assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        Assertions.assertThat(response.getContentAsString()).isEqualTo(jsonApiResponse.write(expectedResponse).getJson());
    }

    @Test
    @WithMockUser
    public void testDeletingANonExistingExpenseReturnsErrorResponse() throws Exception {
        // Setup - data
        ExtraUser linkedUser = new ExtraUser(
                "M",
                "J",
                "mj@me.com",
                "Somepassword1!"
        );
        ApiError expectedResponse = new ApiError(
                HttpStatus.NOT_FOUND,
                "Expense Fetch Error",
                "Expense not found"
        );

        // Setup - Expectations
        given(authService.getUserByPrincipal(any())).willReturn(linkedUser);
        given(expenseService.existsById(any())).willReturn(false);

        // exercise
        MockHttpServletResponse response = postExpenseDeletingRequest(1L);

        // Verify
        assertThatResponseReturnsError(response, expectedResponse);
    }

    @Test
    @WithMockUser
    public void testDeletingAnExistingButNotOwnedExpenseReturnsErrorResponse() throws Exception {
        // Setup - data
        ExtraUser linkedUser = new ExtraUser(
                "M",
                "J",
                "mj@me.com",
                "Somepassword1!"
        );
        ApiError expectedResponse = new ApiError(
                HttpStatus.FORBIDDEN,
                "Expense Access Denied",
                "You are not the owner of the expense you are trying to access"
        );

        // Setup - Expectations
        given(authService.getUserByPrincipal(any())).willReturn(linkedUser);
        given(expenseService.existsById(any())).willReturn(true);
        given(expenseService.isOwner(any(), any())).willReturn(false);

        // exercise
        MockHttpServletResponse response = postExpenseDeletingRequest(1L);

        // Verify
        assertThatResponseReturnsError(response, expectedResponse);
    }

    private MockHttpServletResponse postExpenseDeletingRequest(Long id) throws Exception {
        return mvc.perform(MockMvcRequestBuilders.
                        delete("/expenses/" + id.toString())
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
