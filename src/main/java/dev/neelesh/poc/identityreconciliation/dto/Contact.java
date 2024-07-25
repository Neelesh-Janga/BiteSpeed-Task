package dev.neelesh.poc.identityreconciliation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Contact {
    
    private Integer primaryContactId;
    private String[] emails;
    private String[] phoneNumbers;
    private Integer[] secondaryContactIds;
}
