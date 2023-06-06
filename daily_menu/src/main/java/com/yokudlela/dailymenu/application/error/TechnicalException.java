package com.yokudlela.dailymenu.application.error;

import java.io.Serializable;
import java.util.Arrays;

public class TechnicalException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = 8961535866784616744L;


    public TechnicalException(ErrorCase errorCase) {
        super(errorCase.getErrorCode());
    }

    public TechnicalException(ErrorCase errorCase, Throwable cause) {
        super(errorCase.getErrorCode(), cause);
    }

    public ErrorCase getErrorCase() {
        return Arrays.stream(ErrorCase.values())
                .filter(val -> val.getErrorCode().equals(this.getMessage()))
                .findFirst()
                .orElse(ErrorCase.UNKNOWN_TECHNICAL_ERROR);
    }
}
