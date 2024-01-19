package org.mojodojocasahouse.extra.tests.controller.budgets;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mojodojocasahouse.extra.configuration.SecurityConfiguration;
import org.mojodojocasahouse.extra.controller.BudgetsController;
import org.mojodojocasahouse.extra.dto.requests.ActiveBudgetRequest;
import org.mojodojocasahouse.extra.dto.responses.ApiError;
import org.mojodojocasahouse.extra.dto.responses.ApiResponse;
import org.mojodojocasahouse.extra.dto.model.BudgetDTO;
import org.mojodojocasahouse.extra.model.Category;
import org.mojodojocasahouse.extra.model.ExtraUser;
import org.mojodojocasahouse.extra.repository.ExtraUserRepository;
import org.mojodojocasahouse.extra.security.DelegatingBasicAuthenticationEntryPoint;
import org.mojodojocasahouse.extra.security.ExtraUserDetailsService;
import org.mojodojocasahouse.extra.service.AuthenticationService;
import org.mojodojocasahouse.extra.service.BudgetService;
import org.mojodojocasahouse.extra.service.CategoryService;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@WebMvcTest(BudgetsController.class)
@Import({
        DelegatingBasicAuthenticationEntryPoint.class,
        SecurityConfiguration.class,
        ExtraUserDetailsService.class
})
public class BudgetListingActiveTests {

    @Autowired
    private MockMvc mvc;

    private JacksonTester<ApiError> jsonApiError;

    private JacksonTester<ApiResponse> jsonApiResponse;

    @MockBean
    public AuthenticationService authService;

    @MockBean
    public ExtraUserRepository userRepository;

    @MockBean
    public BudgetService budgetService;

    @MockBean
    public CategoryService categoryService;

    @Autowired
    public BudgetsController controller;

    @BeforeEach
    public void setup() {
        JacksonTester.initFields(this, new ObjectMapper());
    }


    @Test
    @WithMockUser
    public void testListingActiveBudgetsByCategoryAndDateReturnsSuccessfulResponse() throws Exception {
        // Setup - data
        ExtraUser linkedUser = new ExtraUser(
                "M",
                "J",
                "mj@me.com",
                "Somepassword"
        );
        Category customCategory = new Category("test", (short) 1, linkedUser);
        ActiveBudgetRequest request = new ActiveBudgetRequest(
                customCategory.asDto(),
                Date.valueOf("2020-12-09")
        );
        BudgetDTO expectedResponse = new BudgetDTO(
                1L,
                "test",
                new BigDecimal(100),
                BigDecimal.ZERO,
                Date.valueOf("2018-12-09"),
                Date.valueOf("2024-12-09"),
                customCategory.asDto()
        );

        // Setup - Expectations
        given(authService.getUserByPrincipal(any())).willReturn(linkedUser);
        given(categoryService.getCategoryByUserAndNameAndIconId(any(), any(), any())).willReturn(Optional.of(customCategory));
        given(budgetService.getActiveBudgetByCategoryAndDate(any(), any(), any())).willReturn(expectedResponse);

        // exercise
        MockHttpServletResponse response = getAllBudgets(request);

        // Verify
        Assertions.assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        Assertions.assertThat(response.getContentAsString()).isEqualTo(asJsonString(expectedResponse));
    }

    private MockHttpServletResponse getAllBudgets(ActiveBudgetRequest request) throws Exception {
        return mvc.perform(MockMvcRequestBuilders.
                        post("/getActiveBudgets")
                        .content(asJsonString(request))
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

}
