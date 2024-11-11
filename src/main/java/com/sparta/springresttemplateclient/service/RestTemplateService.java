package com.sparta.springresttemplateclient.service;

import com.sparta.springresttemplateclient.dto.ItemDto;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class RestTemplateService {


    // 객체 선언하지 않고 주입하는 방식
    private final RestTemplate restTemplate;

    // 생성자. RestTemplate이 아니라 빌더가 들어간다. build()는 RestTemplate을 반환한다.
    public RestTemplateService(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }


    // 서버에서 하나만 받는 경우
    public ItemDto getCallObject(String query) {
        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("http://localhost:7070") // 서버(7070)에 보낼 준비
                .path("/api/server/get-call-obj") // 서버의 컨트롤러 부분
                .queryParam("query", query) // 동적으로 쿼리 보냄
                .encode()
                .build()
                .toUri();
        log.info("uri = " + uri);
                                                            // getForEntity : 서버에 get 요청 진행(ItemDto 객체를 읽을 수 있게 자동변환하여 uri에 전달)
        ResponseEntity<ItemDto> responseEntity = restTemplate.getForEntity(uri, ItemDto.class);

        // 서버 쪽에서 state code 받아서 로그 찍기
        log.info("statusCode = " + responseEntity.getStatusCode());

        return responseEntity.getBody();
    }

    // 여러 아이템 정보를 한번에 받는 경우 : json 형태의 데이터를 string 형식으로 받음
    public List<ItemDto> getCallList() {
        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("http://localhost:7070")
                .path("/api/server/get-call-list")
                .encode()
                .build()
                .toUri();
        log.info("uri = " + uri);

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);

        log.info("statusCode = " + responseEntity.getStatusCode());
        log.info("Body = " + responseEntity.getBody());

        return fromJSONtoItems(responseEntity.getBody());
    }

    public ItemDto postCall(String query) {
        return null;
    }

    public List<ItemDto> exchangeCall(String token) {
        return null;
    }







    // json 다루는 코드
    public List<ItemDto> fromJSONtoItems(String responseEntity) {
        JSONObject jsonObject = new JSONObject(responseEntity);
        JSONArray items  = jsonObject.getJSONArray("items"); // "items"라는 이름으로 json 데이터가 넘어옴
        List<ItemDto> itemDtoList = new ArrayList<>();

        for (Object item : items) {
            ItemDto itemDto = new ItemDto((JSONObject) item); // item을 JSONObject로 변환하여 ItemDto 객체에 넣음
            itemDtoList.add(itemDto);
        }

        return itemDtoList; // 리스트로 만들고 컨트롤러로 반환
    }
}