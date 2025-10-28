package com.ai.aitravelplanner.dto;

import lombok.Data;

/**
 * 景点详细信息
 */
@Data
public class SpotDTO {
    private String name;          // 景点名称
    private Double lng;           // 经度
    private Double lat;           // 纬度
    private String description;   // 简介
    private String type;          // 类型
}