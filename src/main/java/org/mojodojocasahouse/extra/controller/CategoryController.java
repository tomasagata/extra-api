package org.mojodojocasahouse.extra.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mojodojocasahouse.extra.dto.model.CategoryDTO;
import org.mojodojocasahouse.extra.model.ExtraUser;
import org.mojodojocasahouse.extra.service.AuthenticationService;
import org.mojodojocasahouse.extra.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final AuthenticationService authService;
    private final CategoryService service;

    @GetMapping(path = "/getAllCategories", produces = "application/json")
    public ResponseEntity<List<String>> getMyCategories (Principal principal){
        ExtraUser user = authService.getUserByPrincipal(principal);

        log.debug("Retrieving all categories of user: \"" + principal.getName() + "\"");

        return ResponseEntity.ok(service.getAllCategoryNamesOfUser(user));
    }

    @GetMapping(path = "/getAllCategoriesWithIcons", produces = "application/json")
    public ResponseEntity<List<CategoryDTO>> getMyCategoriesWithIcons (Principal principal){
        ExtraUser user = authService.getUserByPrincipal(principal);

        log.debug("Retrieving all categories along with icons of user: \"" + principal.getName() + "\"");

        return ResponseEntity.ok(service.getAllCategoriesOfUser(user));
    }

}
