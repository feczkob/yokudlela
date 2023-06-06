package com.yokudlela.dailymenu.application.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class ApiError {

    private final String errorCode;
    private final String errorMessage; // after translation
    private final String correlationId;
    private String stacktrace;

}
