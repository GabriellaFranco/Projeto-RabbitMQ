package com.enterprise.suporte.exception;

import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@NoArgsConstructor
public class ErrorResponse {

    long status;
    String message;
    String timestamp;
}
