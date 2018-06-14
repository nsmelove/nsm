package com.nsm.mvc.bean;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;
import java.util.Map;

/**
 * Description for this file
 *
 * @author Created by nsm on 2018/6/11.
 */
public class Product {

    private long userId;
    private long productId;
    private String productName;
    private String productImage;
    private Map<Long,Object> properties;
    private List<Long> categoryIds;
    private int privacy;
    private long createTime;

    public enum ProductPrivacy{
        OPEN,//公开产品
        CLOSED,//封闭产品
        PRIVATE//私有产品
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
