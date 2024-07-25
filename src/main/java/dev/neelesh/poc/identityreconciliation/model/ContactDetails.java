package dev.neelesh.poc.identityreconciliation.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "contact")
public class ContactDetails {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    String phoneNumber;
    String email;
    @Column(nullable = true)
    Integer linkedId;
    @Column(nullable = false, columnDefinition = "VARCHAR(10) DEFAULT 'secondary'")
    String linkPrecedence;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    @Column(nullable = false)
    LocalDateTime deletedAt;
}
