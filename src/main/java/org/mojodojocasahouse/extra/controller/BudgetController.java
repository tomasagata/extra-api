package org.mojodojocasahouse.extra.controller;
import java.security.Principal;
import java.util.Optional;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mojodojocasahouse.extra.dto.model.BudgetDTO;
import org.mojodojocasahouse.extra.dto.requests.ActiveBudgetRequest;
import org.mojodojocasahouse.extra.dto.requests.BudgetAddingRequest;
import org.mojodojocasahouse.extra.dto.responses.ApiResponse;
import org.mojodojocasahouse.extra.model.Category;
import org.mojodojocasahouse.extra.model.ExtraUser;
import org.mojodojocasahouse.extra.service.AuthenticationService;
import org.mojodojocasahouse.extra.service.BudgetService;
import org.mojodojocasahouse.extra.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
public class BudgetController {

    private final AuthenticationService userService;
    private final CategoryService categoryService;
    private final BudgetService budgetService;


    @PostMapping(value = "/addBudget", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Object> addBudget(Principal principal,
                                             @Valid @RequestBody BudgetAddingRequest budgetAddingRequest){
        ExtraUser user = userService.getUserByPrincipal(principal);

        log.debug("Adding budget to user: \"" + user.getEmail() + "\"");
        
        
        ApiResponse response = budgetService.addBudget(user, budgetAddingRequest);
        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }

    @GetMapping("/allBudgets")
    public ResponseEntity<Object> getAllBudgets(Principal principal) {
        ExtraUser user = userService.getUserByPrincipal(principal);
        return new ResponseEntity<>(
                budgetService.getAllBudgetsByUserId(user),
                HttpStatus.OK
        );
    }

    @PostMapping("/getActiveBudgets")
    @Transactional(Transactional.TxType.REQUIRED)
    public ResponseEntity<Object> getActiveBudgets(Principal principal, @Valid @RequestBody ActiveBudgetRequest request){
        ExtraUser user = userService.getUserByPrincipal(principal);

        Optional<Category> foundCategory = categoryService.getCategoryByUserAndNameAndIconId(user,
                request.getCategory().getName(), request.getCategory().getIconId());

        if(foundCategory.isEmpty()){
            return ResponseEntity.ok(null);
        }

        BudgetDTO foundBudget = budgetService.getActiveBudgetByCategoryAndDate(user, foundCategory.get(), request.getDate());
        return ResponseEntity.ok(foundBudget);
    }

}
