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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@WebMvcTest(InvestmentController.class)
@Import({
        DelegatingBasicAuthenticationEntryPoint.class,
        SecurityConfiguration.class,
        ExtraUserDetailsService.class
})
public class InvestmentListingTest {

    @Autowired
    private MockMvc mvc;

    private JacksonTester<ApiError> jsonApiError;

    private JacksonTester<List<InvestmentDTO>> jsonCategoryDtoResponse;

    @MockBean
    public AuthenticationService authenticationService;

    @MockBean
    public DepositService depositService;

    @MockBean
    public ExtraUserRepository userRepository;

    @Autowired
    public InvestmentController investmentController;

    @BeforeEach
    public void setup() {
        JacksonTester.initFields(this, new ObjectMapper());
    }



    @Test
    @WithMockUser
    public void testListingInvestmentsReturnsSuccessfulResponse() throws Exception {
        // Setup - data
        ExtraUser user = new ExtraUser(
                "M",
                "J",
                "mj@me.com",
                "Somepassword"
        );
        List<InvestmentDTO> investmentsFoundByService = List.of(
                new InvestmentDTO(),
                new InvestmentDTO()
        );

        // Setup - expectations
        given(depositService.getInvestmentsOfUser(any())).willReturn(investmentsFoundByService);
        given(authenticationService.getUserByPrincipal(any())).willReturn(user);

        // Exercise
        MockHttpServletResponse response = getInvestmentList();

        // Assert
        Assertions
                .assertThat(response.getContentAsString())
                .isEqualTo(jsonCategoryDtoResponse.write(investmentsFoundByService).getJson());

    }

    private MockHttpServletResponse getInvestmentList() throws Exception {
        return mvc.perform(MockMvcRequestBuilders.
                        get("/getMyInvestments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL))
                .andReturn().getResponse();
    }


}
