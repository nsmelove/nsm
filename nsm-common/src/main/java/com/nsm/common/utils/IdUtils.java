package com.nsm.common.utils;

import com.google.common.hash.Hashing;
import org.apache.commons.lang3.RandomUtils;

import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by nsm on 2018/5/27
 */
public class IdUtils {
    public static long nextLong(){
        return IdWorker.getInstance().nextId();
    }

    public static String nextString16(){
        return nextString32().substring(8, 24);
    }

    public static String nextString32(){
        return Hashing.md5().hashString(UUID.randomUUID().toString(), Charset.defaultCharset()).toString();
    }

    public static void main(String[] args) {
        Set<Long> sets = new HashSet<>();
        int count = 0;
        for(int i = 0 ; i< 1000000; i++) {
            long id = nextLong();
            if(sets.contains(id)) {
                count ++;
            }else {
                sets.add(id);
            }
            System.out.println(id);
        }
        System.out.println("repeat count:" + count);
    }
}
