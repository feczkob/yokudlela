package hu.soft4d.yokudlela3.business.component;

import hu.soft4d.yokudlela3.application.bean.ObjectMerger;
import hu.soft4d.yokudlela3.application.error.ErrorCase;
import hu.soft4d.yokudlela3.application.error.TechnicalException;

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
