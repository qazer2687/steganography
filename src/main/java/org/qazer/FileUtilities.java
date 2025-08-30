package org.qazer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileUtilities {
    public static String getTimestamp() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        return now.format(formatter);
    }
}
