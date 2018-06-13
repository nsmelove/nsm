package com.nsm.mvc.bean;

import org.apache.commons.lang3.builder.ToStringBuilder;

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
        return ToStringBuilder.reflectionToString(this);
    }
}