package com.nsm.common.fastdfs;

import org.csource.common.MyException;
import org.csource.fastdfs.FileInfo;

import java.io.IOException;

/**
 * Created by nsm on 2018/6/3
 */
public class FastdfsTest {
    public static void main(String[] args) {

        FastdfsClient client = FastdfsClient.getClient();
        try {
            //String [] group_file = client.upload_appender_file("中华人民共和国".getBytes(Charset.forName("utf-8")),null,null);
            //System.out.println("group:" + group_file[0] + ",file:" + group_file[1]);
            String fileId = "group1/M00/00/00/wKgBC1scE6qEBi6bAAAAALrUgMA8950499";
            FileInfo fileInfo = client.get_file_info1(fileId);
            System.out.println(fileInfo);
            FastdfsClient.getStorageClient1().get_file_info1(fileId);
            System.out.println("length:" + client.download_file1(fileId).length);
            System.out.println(fileInfo);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
    }
}
