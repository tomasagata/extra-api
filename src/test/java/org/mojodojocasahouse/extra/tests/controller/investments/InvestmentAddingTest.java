package org.mojodojocasahouse.extra.tests.controller.investments;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mojodojocasahouse.extra.configuration.SecurityConfiguration;
import org.mojodojocasahouse.extra.controller.CategoryController;
import org.mojodojocasahouse.extra.controller.InvestmentController;
import org.mojodojocasahouse.extra.dto.model.CategoryDTO;
import org.mojodojocasahouse.extra.dto.model.InvestmentDTO;
import org.mojodojocasahouse.extra.dto.requests.InvestmentAddingRequest;
import org.mojodojocasahouse.extra.dto.responses.ApiError;
import org.mojodojocasahouse.extra.model.ExtraUser;
import org.mojodojocasahouse.extra.repository.ExtraUserRepository;
import org.mojodojocasahouse.extra.security.DelegatingBasicAuthenticationEntryPoint;
import org.mojodojocasahouse.extra.security.ExtraUserDetailsService;
import org.mojodojocasahouse.extra.service.AuthenticationService;
import org.mojodojocasahouse.extra.service.DepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.sql.Timestamp;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@WebMvcTest(InvestmentController.class)
@Import({
        DelegatingBasicAuthenticationEntryPoint.class,
        SecurityConfiguration.class,
        ExtraUserDetailsService.class
})
public class InvestmentAddingTest {

    @Autowired
    private MockMvc mvc;

    private JacksonTester<ApiError> jsonApiError;

    private JacksonTester<InvestmentDTO> jsonInvestmentDtoResponse;

    @MockBean
    public AuthenticationService authenticationService;

    @MockBean
    public DepositService depositService;

    @MockBean
    public ExtraUserRepository userRepository;

    @Autowired
    public InvestmentController categoryController;

    @BeforeEach
    public void setup() {
        JacksonTester.initFields(this, new ObjectMapper());
    }



    @Test
    @WithMockUser
    public void testListingCategoriesWithIconReturnsSuccessfulResponse() throws Exception {
        // Setup - data
        ExtraUser user = new ExtraUser(
                "M",
                "J",
                "mj@me.com",
                "Somepassword"
        );
        InvestmentAddingRequest request = new InvestmentAddingRequest(
                "Test investment",
                BigDecimal.TWO,
                new Timestamp(System.currentTimeMillis()),
                BigDecimal.ONE,
                10,
                1,
                "Test Category 1",
                (short) 1
        );
        InvestmentDTO dtoOfInvestmentAddedByService = new InvestmentDTO(
                1L,
                "Test investment",
                BigDecimal.TWO,
                new Timestamp(System.currentTimeMillis()),
                BigDecimal.ONE,
                10,
                1,
                new CategoryDTO("Test Category 1", (short) 1)
        );

        // Setup - expectations
        given(depositService.createNewInvestment(any(), any())).willReturn(dtoOfInvestmentAddedByService);
        given(authenticationService.getUserByPrincipal(any())).willReturn(user);

        // Exercise
        MockHttpServletResponse response = postInvestmentToApi(request);

        // Assert
        Assertions
                .assertThat(response.getContentAsString())
                .isEqualTo(jsonInvestmentDtoResponse.write(dtoOfInvestmentAddedByService).getJson());

    }

    private MockHttpServletResponse postInvestmentToApi(InvestmentAddingRequest request) throws Exception {
        return mvc.perform(MockMvcRequestBuilders.
                        post("/addInvestment")
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
