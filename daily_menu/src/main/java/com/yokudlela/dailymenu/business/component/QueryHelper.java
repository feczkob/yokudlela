package com.yokudlela.dailymenu.business.component;

import com.yokudlela.dailymenu.business.BusinessConstant;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class QueryHelper {

    public String getLikeParamString(String name) {
        String nameParam = StringUtils.trimToNull(name);
        return (null == nameParam)
                ? null
                : BusinessConstant.PERCENT.concat(nameParam).concat(BusinessConstant.PERCENT);
    }

    public Pageable getPageable(String page, String size) {
        return create(page, size);
    }

    private Pageable create(String page, String size) {
        return PageRequest.of(Integer.parseInt(page), Integer.parseInt(size));
    }

}
