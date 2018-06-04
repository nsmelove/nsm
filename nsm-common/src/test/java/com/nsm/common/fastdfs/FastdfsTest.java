package com.nsm.common.fastdfs;

import org.csource.common.MyException;

import java.io.IOException;

/**
 * Created by nsm on 2018/6/3
 */
public class FastdfsTest {
    public static void main(String[] args) {

        FastdfsClient client = FastdfsClient.getClient();
        try {
            client.get_file_info1("group1/111");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
    }
}
