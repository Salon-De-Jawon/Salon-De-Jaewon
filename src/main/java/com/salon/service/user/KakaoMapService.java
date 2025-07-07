package com.salon.service.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.salon.dto.user.UserLocateDto;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class KakaoMapService {

    @Value("${kakao.rest.api.key}")
    private String kakaoRestKey;

    public UserLocateDto getUserAddress(double x, double y) {
        String url = "https://dapi.kakao.com/v2/local/geo/coord2address.json?x=" + x + "&y=" + y;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoRestKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, JsonNode.class);

            JsonNode document = response.getBody().path("documents").get(0).path("address");


            String addressName = document.path("address_name").asText();
            String region1 = document.path("region_1depth_name").asText();
            String region2 = document.path("region_2depth_name").asText();

            UserLocateDto dto = new UserLocateDto();
            dto.setUserAddress(addressName);
            dto.setRegion1depth(region1);
            dto.setRegion2depth(region2);
            dto.setUserLongitude(BigDecimal.valueOf(x));
            dto.setUserLatitude(BigDecimal.valueOf(y));

            return dto;
        } catch (Exception e) {
            throw new RuntimeException("Kakao 주소 조회 실패: " + e.getMessage());
        }

    }
}
