package kim.dataTest.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import kim.dataTest.dto.KamkoApiResponseDto;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate; // RestTemplate 사용
import org.springframework.web.util.UriComponentsBuilder; // URL 빌더 사용

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class KamcoApiService {

    @Value("${kamco.api.service-key}")
    private String serviceKey;

    private  XmlMapper xmlMapper;
    private final RestTemplate restTemplate;

    // API 기본 URL
    private final String BASE_URL = "http://openapi.onbid.co.kr/openapi/services/KamcoPblsalThingInquireSvc/getKamcoPbctCltrList";

    // 생성자를 통해 Bean으로 등록된 RestTemplate과 XmlMapper를 주입받습니다.
    public KamcoApiService(RestTemplate restTemplate, XmlMapper xmlMapper) {
        this.restTemplate = restTemplate;
        this.xmlMapper = xmlMapper;
    }

    /**
     * 기본검색 - 시도 별 조회
     */
    public String fetchPropertiesBySido(String sido, int numOfRows, int pageNo) throws IOException {

        UriComponentsBuilder builder = createBaseBuilder(numOfRows, pageNo);

        // 선택적 파라미터 - 시도
        addQueryParamIfPresent(builder, "SIDO", sido);

        return callApi(builder);
    }

    /**
     * 상세 조건 검색
     */
    public String fetchPropertiesWithConditions(
            String sido, String sgk, String emd,
            String goodsPriceForm, String goodsPriceTo,
            String openPriceForm, String openPriceTo,
            int numOfRows, String cltrNm,
            String pbctBegmDtm, String pbctClsDtm, int pageNo
    ) throws IOException {

        UriComponentsBuilder builder = createBaseBuilder(numOfRows, pageNo);

        // 선택적 파라미터들
        addQueryParamIfPresent(builder, "SIDO", sido);
        addQueryParamIfPresent(builder, "SGK", sgk);
        addQueryParamIfPresent(builder, "EMD", emd);
        addQueryParamIfPresent(builder, "GOODS_PRICE_FROM", goodsPriceForm);
        addQueryParamIfPresent(builder, "GOODS_PRICE_TO", goodsPriceTo);
        addQueryParamIfPresent(builder, "OPEN_PRICE_TO", openPriceTo);
        addQueryParamIfPresent(builder, "OPEN_PRICE_FROM", openPriceForm);
        addQueryParamIfPresent(builder, "CLTR_NM", cltrNm);
        addQueryParamIfPresent(builder, "PBCT_BEGN_DTM", pbctBegmDtm);
        addQueryParamIfPresent(builder, "PBCT_CLS_DTM", pbctClsDtm);

        return callApi(builder);
    }

    /**
     * RestTemplate을 사용해 실제 API를 호출하는 공통 메서드
     */
    private String callApi(UriComponentsBuilder builder) throws IOException {
        // serviceKey에 인코딩된 문자가 포함될 수 있으므로 build(false) 사용
        String url = builder.build(false).toUriString();
        log.info("API 요청 URL : {}", url);

        try {
            // API 호출 (GET 요청)
            String response = restTemplate.getForObject(url, String.class);
            log.info("API 응답 수신 완료");
            // 응답 본문은 매우 길 수 있으므로 DEBUG 레벨로 로깅하는 것을 권장
            log.debug("API 응답 : {}", response);

            return response;

        } catch (org.springframework.web.client.RestClientException e) {
            log.error("API 호출 실패: {}", e.getMessage());
            throw new IOException("API 호출 중 오류 발생", e);
        }
    }

    /**
     * API 호출을 위한 기본 UriComponentsBuilder를 생성합니다. (공통 파라미터 포함)
     */
    private UriComponentsBuilder createBaseBuilder(int numOfRows, int pageNo) {
        return UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .queryParam("serviceKey", serviceKey) // 인코딩 자동 처리
                .queryParam("numOfRows", String.valueOf(numOfRows))
                .queryParam("pageNo", String.valueOf(pageNo))
                .queryParam("DPSL_MTD_CD", "0001")
                .queryParam("CTGR_HIRK_ID", "10000")
                .queryParam("CTGR_HIRK_ID_MID", "10100");
    }

    /**
     * 값이 null이 아니고 비어있지 않을 때만 query-param을 추가하는 헬퍼 메서드
     */
    private void addQueryParamIfPresent(UriComponentsBuilder builder, String key, String value) {
        if (value != null && !value.isEmpty()) {
            builder.queryParam(key, value);
        }
    }


    /**
     * XML 응답 파싱
     */
    public List<KamkoApiResponseDto> parseXmlResponse(String xmlResponse) throws IOException {
        // 1. xml 문자열을 래퍼 객체 (KamkoXmlResponse)로 파싱합니다.
        KamcoXmlResponse responseWrapper = xmlMapper.readValue(xmlResponse, KamcoXmlResponse.class);

        // 2. 응답코드가 정상이 아닌경우 (헤더가 없거나, resultCode가 "00"이 아닌 경우)
        if (responseWrapper == null || responseWrapper.header == null || !"00".equals(responseWrapper.header.resultCode)) {
            String errorMsg = (responseWrapper != null && responseWrapper.header != null)
                    ? responseWrapper.header.resultMsg
                    : "응답이 없거나 header가 null입니다.";
            log.warn("API 응답 오류: {}", errorMsg);
            return new ArrayList<>(); // 오류 시 빈 리스트 반환
        }

        // 3. 실제 아이템 리스트 반환
        if (responseWrapper.body != null && responseWrapper.body.items != null && responseWrapper.body.items.itemlist != null) {
            return responseWrapper.body.items.itemlist;
        }

        // 4. 아이템이 없는 정상 응답 (totalCount=0)
        if (responseWrapper.body != null && responseWrapper.body.totalCount == 0) {
            log.info("API 응답: 아이템이 없습니다 (totalCount=0)");
            return new ArrayList<>();
        }

        // 5. 그 외의 이유로 아이템이 없는 경우 (예: body.items가 null)
        log.warn("API 응답은 정상(00)이었으나, item 리스트가 비어있습니다.");
        return new ArrayList<>();
    }

    // --- XML 파싱을 위한 내부 래퍼 클래스들 ---
    // (기존 코드와 동일, 변경 없음)
    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class KamcoXmlResponse {
        @JsonProperty("header")
        public KamcoXmlHeader header;
        @JsonProperty("body")
        public KamcoXmlBody body;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class KamcoXmlHeader {
        @JsonProperty("resultCode")
        public String resultCode;
        @JsonProperty("resultMsg")
        public String resultMsg;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class KamcoXmlBody {
        @JsonProperty("items")
        public KamcoXmlItems items;
        @JsonProperty("pageNo")
        public int pageNo;
        @JsonProperty("totalCount")
        public int totalCount;
        @JsonProperty("numOfRows")
        public int numOfRows;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class KamcoXmlItems {
        @JsonProperty("item")
        public List<KamkoApiResponseDto> itemlist;
    }
}