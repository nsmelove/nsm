package com.nsm.common.conf;

import org.apache.commons.io.IOUtils;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.PropertyUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by nsm on 2018/5/31
 */
public class YamlConfigUtils {

    /**
     * load config
     * @param type Class of config
     * @param <T> instance of config
     * @return config
     */
    public static <T> T loadConfig(Class<T> type) {
        String className = type.getName();
        className = className.substring(className.lastIndexOf(".") + 1);
        StringBuilder sb = new StringBuilder();
        for(int i = 0 ; i < className.length() ; i++) {
            char c = className.charAt(i);
            if(Character.isUpperCase(c)) {
                if(i > 0) {
                    sb.append("_");
                }
                sb.append(Character.toLowerCase(c));
            }else {
                sb.append(c);
            }
        }
        sb.append(".yaml");
        return loadConfig(sb.toString(), type);
    }

    /**
     * load config
     * @param name config path
     * @param type class type of config
     * @param <T> instance of config
     * @return config
     */
    public static <T> T loadConfig(String name, Class<T> type) {
        PropertyUtils propertyUtils = new PropertyUtils();
        propertyUtils.setSkipMissingProperties(true);
        Constructor constructor = new Constructor();
        constructor.setPropertyUtils(propertyUtils);
        Yaml yaml = new Yaml(constructor);
        InputStream in = YamlConfigUtils.class.getClassLoader().getResourceAsStream(name);
        if(in == null) {
            throw new NullPointerException("resource '" + name + "' not find !");
        }
        T t = yaml.loadAs(in, type);
        IOUtils.closeQuietly(in);
        return t;
    }

}
