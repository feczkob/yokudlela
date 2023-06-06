package hu.soft4d.yokudlela3.business.component;

import hu.soft4d.yokudlela3.application.error.ErrorCase;
import hu.soft4d.yokudlela3.application.error.TechnicalException;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

import javax.enterprise.context.ApplicationScoped;

/**
 * The type Bean merger.
 */
@ApplicationScoped
public class BeanMerger {

    /**
     * Merge.
     *
     * @param <T>             return type
     * @param originalOne     the original one will be the destination
     * @param updaterInstance the new one, its attribute values will copy into original one by merging
     */
    public <T> void merge(T originalOne, T updaterInstance) {
        writeOrMerge(originalOne, updaterInstance, true);
    }

    /**
     * Write.
     *
     * @param <T>             return type
     * @param originalOne     the original one will be the destination
     * @param updaterInstance the new one, its attribute values will copy into original one by overwriting
     */
    public <T> void write(T originalOne, T updaterInstance) {
        writeOrMerge(originalOne, updaterInstance, true);
    }

    private <T> void writeOrMerge(T originalOne, T newOne, boolean onlyMerge) {
        try {
            final BeanInfo beanInfo = Introspector.getBeanInfo(originalOne.getClass());

            // Iterate over all the attributes
            for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
                // Only copy writable attributes
                if (null == descriptor.getWriteMethod()) {
                    continue;
                }

                Object newValue = descriptor.getReadMethod().invoke(newOne);
                // if only merge allowed, only copy values values where the newOne values is NON null,
                // otherwise overwrite all
                if ((onlyMerge && null != newValue) || !onlyMerge) {
                    descriptor.getWriteMethod().invoke(originalOne, newValue);
                }
            }
        } catch (IntrospectionException | IllegalAccessException | InvocationTargetException exc) {
            throw new TechnicalException(ErrorCase.UNKNOWN_TECHNICAL_ERROR, exc);
        }
    }
}
