package org.mojodojocasahouse.extra.tests.datavalidationlayer.expenses;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mojodojocasahouse.extra.configuration.SecurityConfiguration;
import org.mojodojocasahouse.extra.controller.ExpensesController;
import org.mojodojocasahouse.extra.dto.model.ExpenseDTO;
import org.mojodojocasahouse.extra.dto.requests.FilteringRequest;
import org.mojodojocasahouse.extra.dto.responses.ApiError;
import org.mojodojocasahouse.extra.model.Category;
import org.mojodojocasahouse.extra.model.ExtraUser;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;

@WebMvcTest(ExpensesController.class)
@Import({
        SecurityConfiguration.class,
        DelegatingBasicAuthenticationEntryPoint.class,
        ExtraUserDetailsService.class
})
public class ExpenseListingValidationTests {

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
    public ExpensesController controller;

    @BeforeEach
    public void setup() {
        JacksonTester.initFields(this, new ObjectMapper());
    }

    @Test
    @WithMockUser
    public void testListingExpensesWithInvalidDateRangesReturnsErrorResponse() throws Exception {
        // Setup - data
        FilteringRequest request = new FilteringRequest(
                null, null, List.of("some_invalid_category_value_!@#$#^%$%&*&")
        );
        ApiError expectedResponse = new ApiError(
                HttpStatus.BAD_REQUEST,
                "Data validation error",
                "categories[0]: Category must only contain letters or numbers"
        );

        // exercise
        MockHttpServletResponse response = getExpensesWithArguments(request);

        // Verify
        assertThatResponseReturnsError(response, expectedResponse);
    }



    private MockHttpServletResponse getExpenses() throws Exception {
        return mvc.perform(MockMvcRequestBuilders.
                        get("/getMyExpenses")
                        .accept(MediaType.ALL))
                .andReturn().getResponse();
    }

    private MockHttpServletResponse getExpensesWithArguments(Object request) throws Exception {
        return mvc.perform(
                        MockMvcRequestBuilders
                                .post("/getMyExpenses")
                                .content(asJsonString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.ALL))
                .andReturn()
                .getResponse();
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
