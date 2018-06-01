package com.nsm.common.conf;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.PropertyUtils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/5/31.
 */
public class YamlConfigUtils {
    private static Map<Class,Object> cachedConfigs = new HashMap<>();

    /**
     * get cached config
     * @param type Class of config
     * @param <T> instance of config
     * @return config
     */
    public static <T> T getConfig(Class<T> type){
        return getConfig(type, null);
    };

    /**
     * get cached config
     * @param type Class of config
     * @param loadConfigPath loadConfig path
     * @param <T> instance of config
     * @return config
     */
    public static <T> T getConfig(Class<T> type, String loadConfigPath){
        if (!cachedConfigs.containsKey(type)) {
            synchronized(type){
                if(!cachedConfigs.containsKey(type)){
                    T t = loadConfigPath == null ? loadConfig(type) : loadConfig(loadConfigPath, type);
                    cachedConfigs.put(type, t);
                }
            }
        }
        return (T)cachedConfigs.get(type);
    };

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
     * @param path config path
     * @param type class type of config
     * @param <T> instance of config
     * @return config
     */
    public static <T> T loadConfig(String path, Class<T> type) {
        PropertyUtils propertyUtils = new PropertyUtils();
        propertyUtils.setSkipMissingProperties(true);
        Constructor constructor = new Constructor();
        constructor.setPropertyUtils(propertyUtils);
        Yaml yaml = new Yaml(constructor);
        InputStream in = YamlConfigUtils.class.getClassLoader().getResourceAsStream(path);
        if(in == null) {
            throw new NullPointerException("resource '" + path + "' not find !");
        }
        T t = yaml.loadAs(in, type);
        return t;
    }
}
