package org.mojodojocasahouse.extra.tests.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mojodojocasahouse.extra.dto.model.CategoryDTO;
import org.mojodojocasahouse.extra.model.Category;
import org.mojodojocasahouse.extra.model.ExtraUser;
import org.mojodojocasahouse.extra.repository.CategoryRepository;
import org.mojodojocasahouse.extra.service.CategoryService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;


    @Test
    public void testGettingAllCategoriesOfUser() {
        ExtraUser user = new ExtraUser(
                "Michael",
                "Jackson",
                "mj@me.com",
                "Somepassword1!"
        );
        List<Category> existingCategories = List.of(
                new Category("test cat 1", (short) 1, user),
                new Category("test cat 2", (short) 2, user)
        );
        CategoryDTO[] expectedCategories = existingCategories
                .stream()
                .map(Category::asDto)
                .toArray(CategoryDTO[]::new);

        given(categoryRepository.getCategoriesByOwner(any())).willReturn(existingCategories);

        List<CategoryDTO> response = categoryService.getAllCategoriesOfUser(user);

        Assertions.assertThat(response).containsExactlyInAnyOrder(expectedCategories);

    }

    @Test
    public void testGettingAllCategoryNamesOfUser() {
        ExtraUser user = new ExtraUser(
                "Michael",
                "Jackson",
                "mj@me.com",
                "Somepassword1!"
        );
        List<String> existingCategoryNames = List.of("test cat 1", "test cat 2");
        String[] expectedCategories = existingCategoryNames.toArray(String[]::new);

        given(categoryRepository.getCategoryNamesByUser(any())).willReturn(existingCategoryNames);

        List<String> response = categoryService.getAllCategoryNamesOfUser(user);

        Assertions.assertThat(response).containsExactlyInAnyOrder(expectedCategories);

    }

    @Test
    public void testGettingCategoryByUserAndNameAndIconId() {
        ExtraUser user = new ExtraUser(
                "Michael",
                "Jackson",
                "mj@me.com",
                "Somepassword1!"
        );
        List<Category> existingCategories = List.of(
                new Category("test cat 1", (short) 1, user),
                new Category("test cat 2", (short) 2, user)
        );

        given(categoryRepository.getCategoryByOwnerAndNameAndIconId(any(), any(), any()))
                .willReturn(Optional.of(existingCategories.get(1)));

        Optional<Category> response = categoryService
                .getCategoryByUserAndNameAndIconId(user, "test cat 2", (short) 2);

        Assertions.assertThat(response).isEqualTo(Optional.of(existingCategories.get(1)));

    }

    @Test
    public void testFetchingOrCreatingCategoryFromUserAndNameAndIconIdWithExistingCategoryValuesReturnsExistingCat() {
        ExtraUser user = new ExtraUser(
                "Michael",
                "Jackson",
                "mj@me.com",
                "Somepassword1!"
        );
        List<Category> existingCategories = List.of(
                new Category("test cat 1", (short) 1, user),
                new Category("test cat 2", (short) 2, user)
        );

        given(categoryRepository.getCategoryByOwnerAndNameAndIconId(any(), any(), any()))
                .willReturn(Optional.of(existingCategories.get(1)));
        given(categoryRepository.save(any())).willReturn(existingCategories.get(1));

        Category response = categoryService
                .fetchOrCreateCategoryFromUserAndNameAndIconId(user, "test cat 2", (short) 2);

        Assertions.assertThat(response).isEqualTo(existingCategories.get(1));

    }

}
