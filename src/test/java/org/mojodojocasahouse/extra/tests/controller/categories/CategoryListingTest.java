package org.mojodojocasahouse.extra.tests.controller.categories;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mojodojocasahouse.extra.configuration.SecurityConfiguration;
import org.mojodojocasahouse.extra.controller.CategoryController;
import org.mojodojocasahouse.extra.dto.model.CategoryDTO;
import org.mojodojocasahouse.extra.dto.responses.ApiError;
import org.mojodojocasahouse.extra.dto.responses.ApiResponse;
import org.mojodojocasahouse.extra.model.ExtraUser;
import org.mojodojocasahouse.extra.repository.ExtraUserRepository;
import org.mojodojocasahouse.extra.security.DelegatingBasicAuthenticationEntryPoint;
import org.mojodojocasahouse.extra.security.ExtraUserDetailsService;
import org.mojodojocasahouse.extra.service.AuthenticationService;
import org.mojodojocasahouse.extra.service.CategoryService;
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

@WebMvcTest(CategoryController.class)
@Import({
        DelegatingBasicAuthenticationEntryPoint.class,
        SecurityConfiguration.class,
        ExtraUserDetailsService.class
})
public class CategoryListingTest {

    @Autowired
    private MockMvc mvc;

    private JacksonTester<ApiError> jsonApiError;

    private JacksonTester<List<String>> jsonCategoryNamesResponse;

    @MockBean
    public AuthenticationService authenticationService;

    @MockBean
    public CategoryService categoryService;

    @MockBean
    public ExtraUserRepository userRepository;

    @Autowired
    public CategoryController categoryController;

    @BeforeEach
    public void setup() {
        JacksonTester.initFields(this, new ObjectMapper());
    }



    @Test
    @WithMockUser
    public void testListingCategoryNamesReturnsSuccessfulResponse() throws Exception {
        // Setup - data
        ExtraUser user = new ExtraUser(
                "M",
                "J",
                "mj@me.com",
                "Somepassword"
        );
        List<String> categoryNamesFoundByService = List.of("Category 1", "Category 2");

        // Setup - expectations
        given(categoryService.getAllCategoryNamesOfUser(any())).willReturn(categoryNamesFoundByService);
        given(authenticationService.getUserByPrincipal(any())).willReturn(user);

        // Exercise
        MockHttpServletResponse response = getCategoryNames();

        // Assert
        Assertions
                .assertThat(response.getContentAsString())
                .isEqualTo(jsonCategoryNamesResponse.write(categoryNamesFoundByService).getJson());

    }

    private MockHttpServletResponse getCategoryNames() throws Exception {
        return mvc.perform(MockMvcRequestBuilders.
                        get("/getAllCategories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL))
                .andReturn().getResponse();
    }



}
