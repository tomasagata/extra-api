package org.mojodojocasahouse.extra.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mojodojocasahouse.extra.dto.model.InvestmentDTO;
import org.mojodojocasahouse.extra.dto.requests.InvestmentAddingRequest;
import org.mojodojocasahouse.extra.model.ExtraUser;
import org.mojodojocasahouse.extra.service.AuthenticationService;
import org.mojodojocasahouse.extra.service.DepositService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class InvestmentController {

    private final AuthenticationService authService;
    private final DepositService depositService;

    @PostMapping(value = "/addInvestment", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Object> addExpense(Principal principal,
                                             @Valid @RequestBody InvestmentAddingRequest request){
        ExtraUser user = authService.getUserByPrincipal(principal);
        log.debug("Adding investment to user: \"" + user.getEmail() + "\"");

        InvestmentDTO response = depositService.createNewInvestment(user, request);
        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }

    @GetMapping(value = "/getMyInvestments")
    public ResponseEntity<Object> getMyInvestments(Principal principal) {
        ExtraUser user = authService.getUserByPrincipal(principal);
        log.debug("Getting investments of user: \"" + user.getEmail() + "\"");

        List<InvestmentDTO> response = depositService.getInvestmentsOfUser(user);
        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

}
