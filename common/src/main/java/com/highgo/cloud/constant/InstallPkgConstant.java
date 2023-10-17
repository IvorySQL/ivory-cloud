package com.highgo.cloud.constant;
/* ------------------------------------------------ 
 * 
 * 文件名称: InstallPkgConstant.java
 *
 * 摘要： 
 *      云平台安装包对应的常量。
 *
 * 作者信息及编写日期：chushaolin@highgo.com，2023-8-8 11:30:23.
 *
 * 修改信息：（如果需要）
 * 2023-8-8，chushaolin@highgo.com，具体修改内容。
 * 
 * 版权信息：
 * Copyright (c) 2009-2099, HighGo Software Co.,Ltd. All rights reserved. 
 *
 *文件路径：
 *		com.highgo.cloud.constant.InstallPkgConstant.java
 *
 *-------------------------------------------------
 */
public class InstallPkgConstant {
	/**
	 * cpu架构x86  安装包名
	 */
	public static final String X86_PACKAGE = "x86";

	/**
	 * cpu架构arm  安装包名
	 */
	public static final String ARM_PACKAGE = "arm";
	
    /**
     * 企业版 安装包名
     */
    public static final String INSTALL_ENTER_DB_PACKAGE = "enter";

    /**
     * 安全版 安装包名
     */
    public static final String INSTALL_SEE_DB_PACKAGE = "see";

    /**
     * BMS所有的安装包名
     */
    public static final String INSTALL_PACKAGE_NAME = "installPkgs";
    
    /**
     * 存放数据库安装包的路径名字
     */
    public static final String DB_PATH_NAME = "db";
    
    /**
     * 存放backup安装包的路径名字
     */
    public static final String BACKUP_PATH_NAME = "backup";
    
    /**
     * 存放dbha安装包的路径名字
     */
    public static final String DBHA_PATH_NAME = "dbha";
    
    
    /**
     * 存放hghac安装包的路径名字
     */
    public static final String HGHAC_PATH_NAME = "hghac";
    
    /**
     * 存放monitor安装包的路径名字
     */
    public static final String MONITOR_PATH_NAME = "monitor";
}

