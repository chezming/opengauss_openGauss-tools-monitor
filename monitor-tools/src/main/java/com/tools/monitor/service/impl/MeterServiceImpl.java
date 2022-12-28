/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2022. All rights reserved.
 */

package com.tools.monitor.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.tools.monitor.common.contant.ConmmonShare;
import com.tools.monitor.entity.Prom;
import com.tools.monitor.entity.SysConfig;
import com.tools.monitor.mapper.SysConfigMapper;
import com.tools.monitor.quartz.domain.SysJob;
import com.tools.monitor.service.MeterService;
import com.tools.monitor.util.HandleUtils;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 功能描述
 *
 * @author liu
 * @since 2022-10-01
 */
@Slf4j
@Service
public class MeterServiceImpl implements MeterService {

    /**
     * 数字正则
     */
    private static final String ISNUM = "^(\\-|\\+)?\\d+(\\.\\d+)?$";

    /**
     * 科学计数法
     */
    private static final String KEXUE = "^[+-]?\\d+\\.?\\d*[Ee][+-]?\\d+$";


    @Autowired
    public CollectorRegistry collectorRegistry;

    @Autowired
    private NagiosServiceImpl nagiosServiceImpl;

    @Autowired
    private SysConfigMapper sysConfigMapper;

    private Gauge gauge;

    private List<Prom> list = new ArrayList<>();

    /**
     * publish
     *
     * @param list      list
     * @param sysConfig sysConfig
     * @param task      task
     * @param sysJob    sysJob
     */
    public void publish(List<Map<String, Object>> list, SysConfig sysConfig, String task, SysJob sysJob) {
        // 主机名称
        synchronized (this) {
            execut(list, sysConfig, task, sysJob);
        }
    }

    private void execut(List<Map<String, Object>> list, SysConfig sysConfig, String task, SysJob sysJob) {
        //空指针，将value为null的给个默认值
        for (Map<String, Object> maps : list) {
            for (Map.Entry<String, Object> entry : maps.entrySet()) {
                if (ObjectUtil.isEmpty(entry.getValue()) && !entry.getKey().equalsIgnoreCase("toastsize")) {
                    maps.put(entry.getKey(), "default");
                }
                if (ObjectUtil.isEmpty(entry.getValue()) && entry.getKey().equalsIgnoreCase("toastsize")) {
                    maps.put(entry.getKey(), "0");
                }
                if (entry.getValue().toString().startsWith(".")) {
                    String value = "0" + entry.getValue().toString();
                    maps.put(entry.getKey(), value);
                }
            }
        }
        Map<String, Object> nagiosMap = new HashMap<>();
        String name = sysConfig.getConnectName();
        Boolean isNagios = sysJob.getPlatform().equals(ConmmonShare.NAGIOS);
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> arry = list.get(i);
            Map<String, Object> metric = HandleUtils.getMap(arry);
            dealMetric(metric, i);
            String[] key = getKey(metric);
            String[] value = getValue(metric);
            for (Map.Entry<String, Object> entry : arry.entrySet()) {
                if ((entry.getValue().toString().matches(ISNUM) && !isNagios)
                        || (entry.getValue().toString().matches(KEXUE) && !isNagios)) {
                    report(entry.getKey() + "_" + task + "_" + name, entry.getValue(), key, value);
                }
                if ((entry.getValue().toString().matches(ISNUM) && isNagios)
                        || (entry.getValue().toString().matches(KEXUE) && isNagios)) {
                    nagiosMap.put(entry.getKey() + "_" + task + "_" + name + "_" + i, entry.getValue());
                }
            }
        }
        if (sysJob.getPlatform().equals(ConmmonShare.NAGIOS)) {
            reportNagios(nagiosMap);
        }
    }

    private void report(String metricKey, Object metricValue, String[] key, String[] value) {
        if (CollectionUtil.isEmpty(list)) {
            gauge = Gauge.build()
                    .name(metricKey)
                    .help("Active transactions.")
                    .labelNames(key)
                    .register(collectorRegistry);
            Prom prom01 = new Prom(gauge, metricKey);
            list.add(prom01);
        } else {
            Prom prom = list.stream().filter(item -> item.getGaugeName().equals(metricKey)).findFirst().orElse(null);
            if (null != prom) {
                gauge = prom.getGaugs();
            } else {
                gauge = Gauge.build()
                        .name(metricKey)
                        .help("Active transactions.")
                        .labelNames(key)
                        .register(collectorRegistry);
                Prom prom02 = new Prom(gauge, metricKey);
                list.add(prom02);
            }
        }
        String num = new BigDecimal(metricValue.toString()).toPlainString();
        gauge.labels(value).set(Double.valueOf(num));
    }

    /**
     * reportNagios
     *
     * @param nagiosMap nagiosMap
     */
    public void reportNagios(Map<String, Object> nagiosMap) {
        synchronized (this) {
            SysConfig sysConfig = sysConfigMapper.getNagiosConfig();
            if (ObjectUtil.isEmpty(sysConfig)) {
                return;
            }
            // nagios配置检查
            if (CollectionUtil.isEmpty(nagiosMap)) {
                return;
            }
            nagiosServiceImpl.writeSh(nagiosMap, sysConfig);
        }
    }

    /**
     * dealMetric
     *
     * @param metric metric
     * @param num    num
     */
    public void dealMetric(Map<String, Object> metric, int num) {
        if (CollectionUtil.isEmpty(metric)) {
            metric.put("instance", "node" + num);
        }
    }

    /**
     * getValue
     *
     * @param metric metric
     * @return str
     */
    public String[] getValue(Map<String, Object> metric) {
        Object[] objects = metric.values().toArray(new Object[metric.values().size()]);
        return Arrays.stream(objects).map(Object::toString).toArray(String[]::new);
    }

    /**
     * getKey
     *
     * @param metric metric
     * @return str
     */
    public String[] getKey(Map<String, Object> metric) {
        return metric.keySet().toArray(new String[metric.keySet().size()]);
    }

    /**
     * removeRegister
     *
     * @param gaugeName gaugeName
     */
    public void removeRegister(List<String> gaugeName) {
        try {
            Thread.sleep(6000);
            if (CollectionUtil.isNotEmpty(gaugeName)) {
                for (String str : gaugeName) {
                    Prom prom = list.stream().filter(item -> str.equals(item.getGaugeName())).findFirst().orElse(null);
                    if (ObjectUtil.isNotEmpty(prom)) {
                        list.remove(prom);
                        collectorRegistry.unregister(prom.getGaugs());
                    }
                }
            }
        } catch (InterruptedException exception) {
            log.error("removeAll-->{}", exception.getMessage());
        }
    }
}
