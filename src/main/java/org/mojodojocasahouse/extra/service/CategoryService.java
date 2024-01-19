package org.mojodojocasahouse.extra.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mojodojocasahouse.extra.dto.model.CategoryDTO;
import org.mojodojocasahouse.extra.model.Category;
import org.mojodojocasahouse.extra.model.ExtraUser;
import org.mojodojocasahouse.extra.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository repository;

    public List<CategoryDTO> getAllCategoriesOfUser(ExtraUser user) {
        return repository
                .getCategoriesByOwner(user)
                .stream()
                .map(Category::asDto)
                .collect(Collectors.toList());
    }

    public List<String> getAllCategoryNamesOfUser(ExtraUser user) {
        return repository
                .getCategoryNamesByUser(user);
    }

    public Optional<Category> getCategoryByUserAndNameAndIconId(ExtraUser user, String name, Short iconId){
        return repository.getCategoryByOwnerAndNameAndIconId(user, name, iconId);
    }

    public Category fetchOrCreateCategoryFromUserAndNameAndIconId(ExtraUser user, String name, Short iconId){
        return repository
                .getCategoryByOwnerAndNameAndIconId(user, name, iconId)
                .orElseGet(() -> new Category(name, iconId, user));
    }

}
