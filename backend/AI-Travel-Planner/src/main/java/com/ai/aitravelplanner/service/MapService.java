package com.ai.aitravelplanner.service;

import com.ai.aitravelplanner.config.AmapConfig;
import com.ai.aitravelplanner.dto.PlaceInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MapService {

    private final AmapConfig amapConfig;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * type 参数可用值: restaurant / hotel / traffic
     */
    public List<PlaceInfo> searchNearby(Double lng, Double lat, String type) {
        if (lng == null || lat == null || type == null) {
            return List.of();
        }

        // 将业务类型映射为高德 types code
        String typesCode;
        switch (type.toLowerCase()) {
            case "hotel":
                typesCode = "100000"; // 住宿服务
                break;
            case "traffic":
                typesCode = "150000"; // 交通设施服务
                break;
            case "restaurant":
            default:
                typesCode = "050000"; // 餐饮服务
                break;
        }

        String url = UriComponentsBuilder.fromHttpUrl("https://restapi.amap.com/v3/place/around")
                .queryParam("key", amapConfig.getKey())
                .queryParam("location", String.format("%s,%s", lng, lat))
                .queryParam("radius", 2000)       // 搜索半径 2km
                .queryParam("types", typesCode)
                .queryParam("offset", 10)         // 每页返回数量
                .queryParam("page", 1)
                .queryParam("extensions", "all")  // ✅ 获取详细信息（含电话、照片）
                .toUriString();

        try {
            ResponseEntity<String> resp = restTemplate.getForEntity(url, String.class);
            if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
                return List.of();
            }

            JsonNode root = objectMapper.readTree(resp.getBody());
            if (!"1".equals(root.path("status").asText())) {
                return List.of();
            }

            JsonNode pois = root.path("pois");
            if (pois == null || !pois.isArray() || pois.isEmpty()) {
                return List.of();
            }

            List<PlaceInfo> result = new ArrayList<>();
            for (JsonNode poi : pois) {
                String name = poi.path("name").asText(null);
                String address = poi.path("address").asText(null);
                String typeStr = poi.path("type").asText(null);
                String distance = poi.path("distance").asText(null);
                String tel = poi.path("tel").asText(null);

                // ✅ 提取首张图片（photos 是数组）
                String photoUrl = null;
                JsonNode photos = poi.path("photos");
                if (photos.isArray() && photos.size() > 0) {
                    photoUrl = photos.get(0).path("url").asText(null);
                }

                String location = poi.path("location").asText(null);
                Double poiLng = null, poiLat = null;
                if (location != null && location.contains(",")) {
                    String[] arr = location.split(",");
                    try {
                        poiLng = Double.parseDouble(arr[0]);
                        poiLat = Double.parseDouble(arr[1]);
                    } catch (NumberFormatException ignored) { }
                }

                PlaceInfo info = new PlaceInfo();
                info.setName(name);
                info.setAddress(address);
                info.setType(typeStr);
                info.setDistance(distance);
                info.setTel(tel);
                info.setPhotoUrl(photoUrl);
                info.setLng(poiLng);
                info.setLat(poiLat);

                result.add(info);
            }

            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            return List.of();
        }
    }
}
