package com.nsm.common.utils;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;

/**
 * Created by Administrator on 2018/5/31.
 */
public class YamlConfigUtils {

    public static <T> T loadConfig(String path, Class<T> type) {
        Yaml yaml = new Yaml();
        InputStream in = YamlConfigUtils.class.getClassLoader().getResourceAsStream(path);
        T t = yaml.loadAs(in, type);
        return t;
    }
}
