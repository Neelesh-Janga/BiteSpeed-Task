package dev.neelesh.poc.identityreconciliation.repository;

import dev.neelesh.poc.identityreconciliation.model.ContactDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContactRepository extends JpaRepository<ContactDetails, Integer> {
    
    Optional<List<ContactDetails>> findByLinkedId(Integer rootId);
    Optional<ContactDetails> findById(Integer id);
    Optional<ContactDetails> findFirstByPhoneNumber(String phone);
    Optional<ContactDetails> findFirstByEmail(String email);
    Optional<ContactDetails> findFirstByPhoneNumberAndEmail(String phone, String email);
}
