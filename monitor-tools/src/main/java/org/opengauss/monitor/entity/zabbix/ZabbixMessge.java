/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2022. All rights reserved.
 */

package org.opengauss.monitor.entity.zabbix;

import org.opengauss.monitor.entity.DataSource;
import lombok.Data;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 功能描述
 *
 * @author liu
 * @since 2022-10-01
 */
@Data
public class ZabbixMessge {
    private Integer hostId;

    private Integer fatherItemId;

    private Integer itemPreprocId;

    private DataSource dataSource;

    private JdbcTemplate openGaussTemplate;

    private String hostName;

    /**
     * ZabbixMessge
     */
    public ZabbixMessge() {
    }

    /**
     * ZabbixMessge
     *
     * @param hostId       hostId
     * @param fatherItemId fatherItemId
     * @param dataSource   dataSource
     * @param hostName     hostName
     */
    public ZabbixMessge(Integer hostId, Integer fatherItemId, DataSource dataSource, String hostName) {
        this.hostId = hostId;
        this.fatherItemId = fatherItemId;
        this.dataSource = dataSource;
        this.hostName = hostName;
    }
}
