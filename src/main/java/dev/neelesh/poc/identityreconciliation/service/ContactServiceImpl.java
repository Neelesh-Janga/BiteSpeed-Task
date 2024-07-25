package dev.neelesh.poc.identityreconciliation.service;

import dev.neelesh.poc.identityreconciliation.dto.ConsolidatedContact;
import dev.neelesh.poc.identityreconciliation.dto.Contact;
import dev.neelesh.poc.identityreconciliation.model.ContactDetails;
import dev.neelesh.poc.identityreconciliation.repository.ContactRepository;
import dev.neelesh.poc.identityreconciliation.util.DateTimeFormatHelper;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class ContactServiceImpl implements ContactService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger("dev.neelesh.poc.identityreconciliation.service.ContactServiceImpl");
    
    private final ContactRepository contactRepository;
    
    @Override
    public ResponseEntity<ConsolidatedContact> getConsolidatedContactResponse(String email, String phoneNumber) {
        if ((email == null && phoneNumber == null) || (email != null && email.isEmpty()) || (phoneNumber != null && phoneNumber.isEmpty())) {
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
        
        ContactDetails contactByPhoneAndEmail =
                contactRepository.findFirstByPhoneNumberAndEmail(phoneNumber, email).orElse(null);
        
        ContactDetails contactByEmail = contactRepository.findFirstByEmail(email).orElse(null);
        ContactDetails contactByPhone = contactRepository.findFirstByPhoneNumber(phoneNumber).orElse(null);
        
        if (email != null && phoneNumber != null) {
            if(contactByPhoneAndEmail != null) {
                return getConsolidatedContact(
                        contactByPhoneAndEmail.getLinkPrecedence().equalsIgnoreCase("primary") ?
                                contactByPhoneAndEmail : findRootByLinkedId(contactByPhoneAndEmail.getLinkedId())
                );
            }
            if (contactByEmail != null && contactByPhone != null) {
                if (contactByEmail.getLinkPrecedence().equalsIgnoreCase("primary")
                        && contactByPhone.getLinkPrecedence().equalsIgnoreCase("primary")) {
                    
                    int compareId = contactByEmail.getId().compareTo(contactByPhone.getId());
                    ContactDetails parent = compareId < 0 ? contactByEmail : contactByPhone;
                    ContactDetails child = compareId < 0 ? contactByPhone : contactByEmail;
                    
                    child.setLinkPrecedence("secondary");
                    child.setLinkedId(parent.getId());
                    child.setUpdatedAt(DateTimeFormatHelper.date());
                    
                    contactRepository.save(child);
                    
                    return getConsolidatedContact(parent);
                }
                else {
                    ContactDetails contact = (contactByEmail.getId() < contactByPhone.getId()) ? contactByEmail : contactByPhone;
                    contact = contactRepository.save(createContact(phoneNumber, email, contact.getId(), "secondary"));
                    return getConsolidatedContact(findRootByLinkedId(contact.getLinkedId()));
                }
            }
            else if (contactByEmail != null || contactByPhone != null) {
                ContactDetails contact = (contactByEmail == null) ? contactByPhone : contactByEmail;
                contact = contactRepository.save(createContact(phoneNumber, email, contact.getId(), "secondary"));
                return getConsolidatedContact(findRootByLinkedId(contact.getLinkedId()));
            }
            else {
                return getConsolidatedContact(contactRepository.save(createContact(phoneNumber, email, null, "primary")));
            }
        }
        else {
            if (contactByPhoneAndEmail == null) {
                ContactDetails contact = (email == null) ? contactByPhone : contactByEmail;
                contact = contactRepository.save(createContact(phoneNumber, email, contact == null ? null :
                        contact.getId(), contact == null ? "primary" : "secondary"));
                return getConsolidatedContact(
                        contact.getLinkedId() != null ? findRootByLinkedId(contact.getLinkedId()) : contact
                );
            }else{
                return getConsolidatedContact(
                        contactByPhoneAndEmail.getLinkedId() == null ? contactByPhoneAndEmail :
                                findRootByLinkedId(contactByPhoneAndEmail.getLinkedId())
                );
            }
        }
    }
    
    /*
     * This is a template method to create and return ContactDetails object
     * */
    private ContactDetails createContact(String phoneNumber, String email, Integer linkedId, String linkPrecedence){
        ContactDetails contactDetails = new ContactDetails();
        contactDetails.setPhoneNumber(phoneNumber);
        contactDetails.setEmail(email);
        contactDetails.setLinkedId(linkedId);
        contactDetails.setLinkPrecedence(linkPrecedence);
        contactDetails.setCreatedAt(DateTimeFormatHelper.date());
        contactDetails.setUpdatedAt(DateTimeFormatHelper.date());
        
        return contactDetails;
    }

    /*
    * This method returns ConsolidatedContact sorted based on linkPrecedence (primary comes first then secondary) first
    * and then on createdAt.
    * */
    private ResponseEntity<ConsolidatedContact> getConsolidatedContact(ContactDetails root) {
        if(root == null) {
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
        
        List<ContactDetails> contactDetailsList = findAllChildrenOfRoot(root);
        
        contactDetailsList = contactDetailsList.stream()
                .sorted(Comparator.comparing(ContactDetails::getCreatedAt))
                .collect(Collectors.toList());
        
        contactDetailsList.add(0, root);
        
        Contact contact = new Contact();
        contact.setPrimaryContactId(root.getId());
        contact.setPhoneNumbers(contactDetailsList.stream().map(ContactDetails::getPhoneNumber).filter(Objects::nonNull).distinct().toList().toArray(new String[0]));
        contact.setEmails(contactDetailsList.stream().map(ContactDetails::getEmail).filter(Objects::nonNull).distinct().toList().toArray(new String[0]));
        contactDetailsList.remove(0);
        contact.setSecondaryContactIds(contactDetailsList.stream().map(ContactDetails::getId).toList().toArray(new Integer[0]));
        
        return new ResponseEntity<>(new ConsolidatedContact(contact), HttpStatus.OK);
    }
    
    /*
     * Given an id of the database entry, the method recursively looks for the root id using linkedId.
     * if given id is not found in the database, error log is displayed in the console and status code is 200 OK.
     * */
    private ContactDetails findRootByLinkedId(Integer id) {
        ContactDetails contactDetails = contactRepository.findById(id).orElse(null);
        
        if (Objects.isNull(contactDetails)) {
            LOGGER.error("Contact with given id not found");
            return null;
        }
        
        if (contactDetails.getLinkedId() == null) {
            LOGGER.info("Root id is {}", id);
            return contactDetails;
        }
        
        return findRootByLinkedId(contactDetails.getLinkedId());
    }
    
    /*
     * Given ContactDetails root, this method will get all the associated secondary contact information using
     * recursive method findDescendants
     * */
    private List<ContactDetails> findAllChildrenOfRoot(ContactDetails root) {
        Set<Integer> visitedIds = new HashSet<>();
        List<ContactDetails> allDescendants = new ArrayList<>();
        findDescendants(root.getId(), allDescendants, visitedIds);
        return allDescendants;
    }
    
    /*
    * This is a recursive method that finds out all the children associated to given currentId
    * */
    private void findDescendants(Integer currentId, List<ContactDetails> descendants, Set<Integer> visitedIds) {
        List<ContactDetails> children = contactRepository.findByLinkedId(currentId).orElse(new ArrayList<>());
        
        for (ContactDetails child : children) {
            if (visitedIds.contains(child.getId())) {
                continue;
            }
            visitedIds.add(child.getId());
            descendants.add(child);
            
            findDescendants(child.getId(), descendants, visitedIds);
        }
    }
}
