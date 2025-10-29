package com.ai.aitravelplanner.controller;

import com.ai.aitravelplanner.dto.PlaceInfo;
import com.ai.aitravelplanner.service.MapService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/map")
@RequiredArgsConstructor
public class MapController {

    private final MapService mapService;

    /**
     * 示例：
     * GET /api/map/nearby?lng=116.397428&lat=39.90923&type=restaurant
     *
     * type 支持: restaurant / hotel / traffic
     */
    @GetMapping("/nearby")
    public List<PlaceInfo> getNearby(
            @RequestParam Double lng,
            @RequestParam Double lat,
            @RequestParam String type) {

        return mapService.searchNearby(lng, lat, type);
    }
}