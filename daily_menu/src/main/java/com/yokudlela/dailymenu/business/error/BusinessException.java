package com.yokudlela.dailymenu.business.error;

import com.yokudlela.dailymenu.application.error.ErrorCase;

import java.io.Serializable;
import java.util.Arrays;

public class BusinessException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = 5323096335001312964L;


    public BusinessException(ErrorCase errorCase) {
        super(errorCase.getErrorCode());
    }

    public BusinessException(ErrorCase errorCase, Throwable cause) {
        super(errorCase.getErrorCode(), cause);
    }

    public ErrorCase getErrorCase() {
        return Arrays.stream(ErrorCase.values())
                .filter(val -> val.getErrorCode().equals(this.getMessage()))
                .findFirst()
                .orElse(ErrorCase.UNKNOWN_BUSINESS_ERROR);
    }

}
