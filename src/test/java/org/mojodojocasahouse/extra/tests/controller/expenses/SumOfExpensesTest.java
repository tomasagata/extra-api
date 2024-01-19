package org.mojodojocasahouse.extra.tests.controller.expenses;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mojodojocasahouse.extra.configuration.SecurityConfiguration;
import org.mojodojocasahouse.extra.controller.ExpensesController;
import org.mojodojocasahouse.extra.dto.responses.ApiError;
import org.mojodojocasahouse.extra.dto.model.ExpenseDTO;
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

import java.sql.Date;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;

@WebMvcTest(ExpensesController.class)
@Import({
        SecurityConfiguration.class,
        DelegatingBasicAuthenticationEntryPoint.class,
        ExtraUserDetailsService.class
})
public class SumOfExpensesTest {

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
    public void testListingExpensesWithNoDateRangesCallsServiceMethodWithNullReturnsAllExpenses() throws Exception {
        // Setup - data
        ExtraUser linkedUser = new ExtraUser(
                "M",
                "J",
                "mj@me.com",
                "Somepassword"
        );
        List<Map<String, String>> expectedResponse = List.of(
                Map.of("category", "A category", "amount", "10000"),
                Map.of("category", "Another category", "amount", "20000"),
                Map.of("category", "A third category", "amount", "30000")
        );

        // Setup - Expectations
        given(authService.getUserByPrincipal(any()))
                .willReturn(linkedUser);
        given(expenseService.getSumOfExpensesOfUserByCategoriesAndDateRanges(any(), any(), isNull(), isNull()))
                .willReturn(expectedResponse);

        // exercise
        MockHttpServletResponse response = getSumOfExpenses();

        // Verify
        Assertions.assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        Assertions.assertThat(response.getContentAsString()).isEqualTo(asJsonString(expectedResponse));
    }

    @Test
    @WithMockUser
    public void testListingExpensesWithRangesCallsServiceMethodWithDateRangesAndReturnsAllExpensesWithinThoseRanges() throws Exception {
        // Setup - data
        ExtraUser linkedUser = new ExtraUser(
                "M",
                "J",
                "mj@me.com",
                "Somepassword"
        );
        List<Map<String, String>> expectedResponse = List.of(
                Map.of("category", "A category", "amount", "10000"),
                Map.of("category", "Another category", "amount", "20000"),
                Map.of("category", "A third category", "amount", "30000")
        );

        // Setup - Expectations
        given(authService.getUserByPrincipal(any()))
                .willReturn(linkedUser);
        given(expenseService.getSumOfExpensesOfUserByCategoriesAndDateRanges(any(), any(), any(Date.class), any(Date.class)))
                .willReturn(expectedResponse);

        // exercise
        MockHttpServletResponse response = getSumOfExpensesWithArguments("2022-12-09", "2022-12-10");

        // Verify
        Assertions.assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        Assertions.assertThat(response.getContentAsString()).isEqualTo(asJsonString(expectedResponse));
    }


    private MockHttpServletResponse getSumOfExpenses() throws Exception {
        return mvc.perform(MockMvcRequestBuilders.
                        get("/getSumOfExpenses")
                        .accept(MediaType.ALL))
                .andReturn().getResponse();
    }

    private MockHttpServletResponse getSumOfExpensesWithArguments(String from_date_iso, String until_date_iso) throws Exception {
        return mvc.perform(
                        MockMvcRequestBuilders
                                .get("/getSumOfExpenses")
                                .param("from", from_date_iso)
                                .param("until", until_date_iso)
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

}
