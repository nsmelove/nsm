package com.nsm.core.bean;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 产品属性
 *
 * @author Created by nsm on 2018/6/11.
 */
public class ProductProperty {

    private long propertyId;
    private long propertyName;
    private int valueType;

    public enum ValueType{
        INTEGER,
        LONG,
        FLOAT,
        DATE,
        STRING,
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
