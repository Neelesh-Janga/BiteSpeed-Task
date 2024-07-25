package dev.neelesh.poc.identityreconciliation.controller;

import dev.neelesh.poc.identityreconciliation.dto.ConsolidatedContact;
import dev.neelesh.poc.identityreconciliation.dto.Order;
import dev.neelesh.poc.identityreconciliation.service.ContactService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class ItemCreationController {
    
    private final ContactService contactService;
    
    @PostMapping(value = "/identify", consumes = "application/json")
    public ResponseEntity<ConsolidatedContact> identify(@RequestBody Order order) {
        return contactService.getConsolidatedContactResponse(order.getEmail(), order.getPhoneNumber());
    }
}
