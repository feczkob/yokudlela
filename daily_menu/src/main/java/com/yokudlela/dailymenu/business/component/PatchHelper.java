package com.yokudlela.dailymenu.business.component;

import com.yokudlela.dailymenu.application.bean.ObjectMerger;
import com.yokudlela.dailymenu.application.error.ErrorCase;
import com.yokudlela.dailymenu.application.error.TechnicalException;

import com.fasterxml.jackson.core.JsonProcessingException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

// IMPORTANT NOTE: not works with such objects annotated with Jacksonized!!!
@ApplicationScoped
public class PatchHelper {

    @Inject
    ObjectMerger objectMerger;


    public void merge(final Object source, final Object patch) {
        try {
            objectMerger.updateValue(source, patch);
        } catch (JsonProcessingException exc) {
            throw new TechnicalException(ErrorCase.JSON_PROCESSING_FAILURE, exc);
        }
    }

}
