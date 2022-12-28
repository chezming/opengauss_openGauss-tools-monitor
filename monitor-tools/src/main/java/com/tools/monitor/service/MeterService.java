/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2022. All rights reserved.
 */

package com.tools.monitor.service;

import com.tools.monitor.entity.SysConfig;
import com.tools.monitor.quartz.domain.SysJob;

import java.util.List;
import java.util.Map;

/**
 * 功能描述
 *
 * @author liu
 * @since 2022-10-01
 */
public interface MeterService {
    /**
     * publish
     *
     * @param list list
     * @param sysConfig sysConfig
     * @param task task
     * @param sysJob sysJob
     */
    void publish(List<Map<String, Object>> list, SysConfig sysConfig, String task, SysJob sysJob);
}

