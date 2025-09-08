package com.payment.engine.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Api(tags = "健康检查")
@RestController
@RequestMapping("/api/engine")
public class HealthController {

    @ApiOperation(value = "健康检查", notes = "检查服务健康状态")
    @GetMapping("/health")
    public String health() {
        return "OK";
    }

    @ApiOperation(value = "获取版本信息", notes = "获取服务版本信息")
    @GetMapping("/version")
    public Map<String, Object> version() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "payment-engine");
        map.put("version", "1.0.0-SNAPSHOT");
        return map;
    }
}


