package com.nsm.common.Hash;

import com.google.common.hash.Hashing;

/**
 * Created by nsm on 2018/6/3.
 */
public class HashTest {
    public static void main(String[] args) {
        String key = 1527524717296L + "nsm123456";
        System.out.println(key);
        System.out.println(Hashing.md5().hashString(key));
    }
}
