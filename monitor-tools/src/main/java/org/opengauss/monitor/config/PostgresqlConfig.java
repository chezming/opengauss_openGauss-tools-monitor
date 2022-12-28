/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2022. All rights reserved.
 */

package org.opengauss.monitor.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 功能描述
 *
 * @author liu
 * @since 2022-10-01
 */
@Component
@ConfigurationProperties(prefix = "postgresql")
public class PostgresqlConfig {
    private static String prefix;

    private static String suffix;

    private static String driver;

    public static String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        PostgresqlConfig.prefix = prefix;
    }

    public static String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        PostgresqlConfig.suffix = suffix;
    }

    public static String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        PostgresqlConfig.driver = driver;
    }
}
