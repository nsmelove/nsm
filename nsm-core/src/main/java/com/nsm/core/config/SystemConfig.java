package com.nsm.core.config;

/**
 * Created by nieshuming on 2018/6/11
 */
public class SystemConfig {

    /**
     * 是否为集群模式的服务
     */
    public static boolean clusterMode = true;

    /**
     * 用户同时登录上限
     */
    public static final int loginLimit = 5;
    /**
     * 系统用户上限
     */
    public static final long userLimit = Long.MAX_VALUE;

    /**
     * 用户联系人上限
     */
    public static final int userContactLimit = 500;

    /**
     * 用户能创建的群组上限
     */
    public static final int userGroupLimit = 100;

    /**
     * 群组能够够创建的群层级上限
     */
    public static final int groupLevelLimit = 3;
    /**
     * 群组成员上限
     */
    public static final int groupMemberLimit = 500;

    /**
     * 产品类别上限
     */
    public static final int productCategoryLimit = 200;

    /**
     * 产品子类别层级上限
     */
    public static final int categoryLevelLimit = 3;

    /**
     * 产品种类上限
     */
    public static final int productLimit = 2000;
}
