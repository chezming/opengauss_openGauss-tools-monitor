/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2022. All rights reserved.
 */

package com.tools.monitor.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 功能描述
 *
 * @author liu
 * @since 2022-10-01
 */
@Component
@ConfigurationProperties(prefix = "mysql")
public class ZabbixConfig {
    private static String prefix;

    private static String suffix;

    private static String driver;

    private static Integer timeout;

    public static String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        ZabbixConfig.prefix = prefix;
    }

    public static String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        ZabbixConfig.suffix = suffix;
    }

    public static String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        ZabbixConfig.driver = driver;
    }

    public static Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        ZabbixConfig.timeout = timeout;
    }
}
