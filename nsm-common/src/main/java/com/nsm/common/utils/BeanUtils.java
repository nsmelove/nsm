package com.nsm.common.utils;

import com.google.common.collect.Maps;
import org.apache.commons.beanutils.BeanMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Description for this file
 *
 * @author Created by nsm on 2018/6/14.
 */
public class BeanUtils {
    private static Logger logger = LoggerFactory.getLogger(BeanUtils.class);

    public static Map<String, Object> beanToMap(Object bean) {
        Map<Object, Object> beanMap = new BeanMap(bean);
        Map<String, Object> resultMap = Maps.newHashMapWithExpectedSize(beanMap.size());
        beanMap.forEach((k, v) -> {
            if (!"class".equals(k) && v != null) {
                resultMap.put((String) k, v);
            }
        });
        return resultMap;
    }

    public static <T> T mapToBean(Map<String, Object> map, Class<T> beanClass) {
        try {
            T bean = beanClass.newInstance();
            org.apache.commons.beanutils.BeanUtils.populate(bean, map);
            return bean;
        } catch (Exception e) {
            logger.error("map to entity error", e);
            return null;
        }
    }
}
