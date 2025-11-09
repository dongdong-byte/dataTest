package kim.dataTest.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import kim.dataTest.dto.KamkoApiResponseDto;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class KamcoApiService {

    @Value("${kamco.api.service-key}")
    private String serviceKey;

    private XmlMapper xmlMapper;
    private final RestTemplate restTemplate;

    private final String BASE_URL = "http://openapi.onbid.co.kr/openapi/services/KamcoPblsalThingInquireSvc/getKamcoPbctCltrList";

    public KamcoApiService(RestTemplate restTemplate, XmlMapper xmlMapper) {
        this.restTemplate = restTemplate;
        this.xmlMapper = xmlMapper;
    }

    public String fetchPropertiesBySido(String sido, int numOfRows, int pageNo) throws IOException {
        UriComponentsBuilder builder = createBaseBuilder(numOfRows, pageNo);
        addQueryParamIfPresent(builder, "SIDO", sido);
        return callApi(builder);
    }

    public String fetchPropertiesWithConditions(
            String sido, String sgk, String emd,
            String goodsPriceForm, String goodsPriceTo,
            String openPriceForm, String openPriceTo,
            int numOfRows, String cltrNm,
            String pbctBegmDtm, String pbctClsDtm, int pageNo
    ) throws IOException {
        UriComponentsBuilder builder = createBaseBuilder(numOfRows, pageNo);
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

    private String callApi(UriComponentsBuilder builder) throws IOException {
        String url = builder.build(false).toUriString();
        log.info("API 요청 URL : {}", url);

        try {
            String response = restTemplate.getForObject(url, String.class);
            log.info("API 응답 수신 완료");
            log.debug("API 응답 : {}", response);

            try {
                java.nio.file.Files.write(
                        java.nio.file.Paths.get("api-response.xml"),
                        response.getBytes(java.nio.charset.StandardCharsets.UTF_8)
                );
                log.info("XML 파일 저장 완료: api-response.xml");
            } catch (Exception e) {
                log.warn("XML 파일 저장 실패", e);
            }

            return response;

        } catch (org.springframework.web.client.RestClientException e) {
            log.error("API 호출 실패: {}", e.getMessage());
            throw new IOException("API 호출 중 오류 발생", e);
        }
    }

    private UriComponentsBuilder createBaseBuilder(int numOfRows, int pageNo) {
        return UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .queryParam("serviceKey", serviceKey)
                .queryParam("numOfRows", String.valueOf(numOfRows))
                .queryParam("pageNo", String.valueOf(pageNo))
                .queryParam("DPSL_MTD_CD", "0001")
                .queryParam("CTGR_HIRK_ID", "10000")
                .queryParam("CTGR_HIRK_ID_MID", "10100");
    }

    private void addQueryParamIfPresent(UriComponentsBuilder builder, String key, String value) {
        if (value != null && !value.isEmpty()) {
            builder.queryParam(key, value);
        }
    }

    public List<KamkoApiResponseDto> parseXmlResponse(String xmlResponse) throws IOException {
        try {
            KamcoXmlResponse responseWrapper = xmlMapper.readValue(xmlResponse, KamcoXmlResponse.class);

            if (responseWrapper == null || responseWrapper.header == null || !"00".equals(responseWrapper.header.resultCode)) {
                String errorMsg = (responseWrapper != null && responseWrapper.header != null)
                        ? responseWrapper.header.resultMsg
                        : "응답이 없거나 header가 null입니다.";
                log.warn("API 응답 오류: {}", errorMsg);
                return new ArrayList<>();
            }

            if (responseWrapper.body != null && responseWrapper.body.items != null && responseWrapper.body.items.itemlist != null) {
                log.info("파싱 성공: {} 건의 데이터", responseWrapper.body.items.itemlist.size());
                return responseWrapper.body.items.itemlist;
            }

            if (responseWrapper.body != null && responseWrapper.body.totalCount == 0) {
                log.info("API 응답: 아이템이 없습니다 (totalCount=0)");
                return new ArrayList<>();
            }

            log.warn("API 응답은 정상(00)이었으나, item 리스트가 비어있습니다.");
            return new ArrayList<>();

        } catch (Exception e) {
            log.error("XML 파싱 중 오류 발생", e);
            log.error("파싱 실패한 XML: {}", xmlResponse);
            throw new IOException("XML 파싱 실패: " + e.getMessage(), e);
        }
    }

    // ⭐ 내부 클래스들 수정
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class KamcoXmlResponse {
        @JsonProperty("header")
        private KamcoXmlHeader header;

        @JsonProperty("body")
        private KamcoXmlBody body;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class KamcoXmlHeader {
        @JsonProperty("resultCode")
        private String resultCode;

        @JsonProperty("resultMsg")
        private String resultMsg;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class KamcoXmlBody {
        @JsonProperty("items")
        private KamcoXmlItems items;

        @JsonProperty("pageNo")
        private int pageNo;

        @JsonProperty("totalCount")
        private int totalCount;

        @JsonProperty("numOfRows")
        private int numOfRows;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class KamcoXmlItems {
        // ⭐ 이 부분이 핵심!
        @JsonProperty("item")
        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "item")
        private List<KamkoApiResponseDto> itemlist;
    }
}