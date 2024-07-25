package dev.neelesh.poc.identityreconciliation.util;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class DateTimeFormatHelper {
    
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public static LocalDateTime date() {
        return LocalDateTime.parse(modifyTime(), formatter);
    }
    
    private static String modifyTime() {
        LocalDateTime now = LocalDateTime.now();
        return now.format(formatter);
    }
}
