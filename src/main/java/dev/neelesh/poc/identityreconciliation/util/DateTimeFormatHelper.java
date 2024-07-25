package dev.neelesh.poc.identityreconciliation.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DateTimeFormatHelper {
    
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public LocalDateTime date() {
        return LocalDateTime.parse(modifyTime(), formatter);
    }
    
    private String modifyTime() {
        LocalDateTime now = LocalDateTime.now();
        return now.format(formatter);
    }
}
