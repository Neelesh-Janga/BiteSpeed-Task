package dev.neelesh.poc.identityreconciliation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    
    private String email;
    private String phoneNumber;
}
