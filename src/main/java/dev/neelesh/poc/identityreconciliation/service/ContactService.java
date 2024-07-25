package dev.neelesh.poc.identityreconciliation.service;

import dev.neelesh.poc.identityreconciliation.dto.ConsolidatedContact;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface ContactService {
    
    ResponseEntity<ConsolidatedContact> getConsolidatedContactResponse(String email, String phone);
}
