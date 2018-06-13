package com.nsm.mvc.bean;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 产品分类
 *
 * @author Created by nsm on 2018/6/11.
 */
public class ProductCategory {

    private long categoryId;
    private long categoryName;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}