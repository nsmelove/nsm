package com.nsm.websocket.server;

import com.google.common.collect.Maps;
import com.nsm.common.utils.JsonUtils;
import io.vertx.core.json.JsonObject;

import java.util.Map;

/**
 * Created by nieshuming on 2018/6/29
 */
public class JsonObjectTest {

    public static void main(String[] args) {
        Map<Integer, String> map = Maps.newHashMap();
        map.put(1,"1");
        map.put(2,"2");
        String json = JsonUtils.toJson(map);
        System.out.println(json);
        String JsonObjectStr = JsonObject.mapFrom(map).toString();
        System.out.println(JsonObjectStr);
    }
}
