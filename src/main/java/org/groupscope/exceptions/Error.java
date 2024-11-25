package org.groupscope.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Mykyta Liashko
 */
@Data
@AllArgsConstructor
public class Error {

    private String message;

    private ErrorType errorType;

    private LocalDateTime timeStamp;

}

