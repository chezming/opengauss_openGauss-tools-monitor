/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2020. All rights reserved.
 */

package org.opengauss.monitor.entity;

/**
 * 功能描述
 *
 * @author liu
 * @since 2022-10-01
 */
public enum ResponseCode {
    RESPONSE_SUCCESS(200, "success"),
    RESPONSE_UNAUTH(300, "unauth"),
    RESPONSE_ERROR(400, "error"),
    RESPONSE_EXCEPTION(500, "exception"),
    RESPONSE_TARGET_ERR(600, "target_err");

    private Integer code;                       // 系统响应编码
    private String remark;                      // 响应编码说明

    private ResponseCode(Integer code, String remark) {
        this.code = code;
        this.remark = remark;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
