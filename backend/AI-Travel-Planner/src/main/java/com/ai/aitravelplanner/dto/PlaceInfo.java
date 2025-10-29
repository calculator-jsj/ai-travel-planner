package com.ai.aitravelplanner.dto;

import lombok.Data;

@Data
public class PlaceInfo {
    private String name;
    private String address;
    private String type;
    private String distance;
    private Double lng;
    private Double lat;

    private String tel;       // ✅ 新增电话
    private String photoUrl;  // ✅ 新增图片URL
}