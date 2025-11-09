package kim.dataTest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class KamkoApiResponseDto {

    // 온비드 API응답용

    @JsonProperty("RNUM")  // ⭐ 대문자로 변경
    @JacksonXmlProperty(localName = "RNUM")  // ⭐ 추가
    private String rnum; // 순번

    @JsonProperty("CLTR_MNMT_NO")  // ⭐ 대문자 + 언더스코어
    @JacksonXmlProperty(localName = "CLTR_MNMT_NO")
    private String cltrMnmtNo; // 물건 관리번호

    @JsonProperty("CLTR_NM")  // ⭐ 대문자 + 언더스코어
    @JacksonXmlProperty(localName = "CLTR_NM")  // ⭐ 추가
    private String cltrNm; // 물건명

    @JsonProperty("DPSL_MTD_CD")  // ⭐ 대문자 + 언더스코어
    @JacksonXmlProperty(localName = "DPSL_MTD_CD")  // ⭐ 추가
    private String dpslMtdCd; // 처분방법 코드 (0001: 매각, 0002: 임대)

    @JsonProperty("CTGR_HIRK_ID")  // ⭐ 대문자 + 언더스코어
    @JacksonXmlProperty(localName = "CTGR_HIRK_ID")  // ⭐ 추가
    private String ctgrHirkId; // 카테고리 상위 ID

    @JsonProperty("CTGR_HIRK_ID_MID")  // ⭐ 대문자 + 언더스코어
    @JacksonXmlProperty(localName = "CTGR_HIRK_ID_MID")  // ⭐ 추가
    private String ctgrHirkIdMid; // 카테고리 중위 ID

    // sido, sgk, emd는 XML에 없으므로 수동으로 채워야 함
    private String sido; // 시도
    private String sgk; // 시군구
    private String emd; // 읍면동

    @JsonProperty("APSL_ASES_AVG_AMT")  // ⭐ 대문자 + 언더스코어
    @JacksonXmlProperty(localName = "APSL_ASES_AVG_AMT")  // ⭐ 추가
    private String goodsPriceStr; // 감정가 (문자열로 받기)

    @JsonProperty("MIN_BID_PRC")  // ⭐ 대문자 + 언더스코어
    @JacksonXmlProperty(localName = "MIN_BID_PRC")  // ⭐ 추가
    private String openPriceStr; // 최저 입찰가 (문자열로 받기)

    @JsonProperty("PBCT_BEGN_DTM")  // ⭐ 대문자 + 언더스코어
    @JacksonXmlProperty(localName = "PBCT_BEGN_DTM")  // ⭐ 추가
    private String pbctBegnDtm; // 공고 시작일

    @JsonProperty("PBCT_CLS_DTM")  // ⭐ 대문자 + 언더스코어
    @JacksonXmlProperty(localName = "PBCT_CLS_DTM")  // ⭐ 추가
    private String pbctClsDtm; // 공고 종료일(YYYYMMDD)

    // 실제 사용할 Long 타입 필드
    public Long getGoodsPrice() {
        try {
            return goodsPriceStr != null && !goodsPriceStr.trim().isEmpty()
                    ? Long.parseLong(goodsPriceStr.trim())
                    : 0L;
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    public Long getOpenPrice() {
        try {
            return openPriceStr != null && !openPriceStr.trim().isEmpty()
                    ? Long.parseLong(openPriceStr.trim())
                    : 0L;
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
}
