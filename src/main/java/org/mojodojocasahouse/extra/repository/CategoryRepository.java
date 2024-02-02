package org.mojodojocasahouse.extra.repository;

import org.mojodojocasahouse.extra.model.Category;
import org.mojodojocasahouse.extra.model.ExtraUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> getCategoriesByOwner(ExtraUser owner);

    @Query("SELECT c.name FROM Category c WHERE c.owner = :user")
    List<String> getCategoryNamesByUser(ExtraUser user);

    Optional<Category> getCategoryByOwnerAndNameAndIconId(ExtraUser owner, String name, Short iconId);

}
