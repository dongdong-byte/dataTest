package kim.dataTest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(localName = "item")  // ⭐ 추가
public class KamkoApiResponseDto {

    @JsonProperty("RNUM")
    @JacksonXmlProperty(localName = "RNUM")
    private String rnum;

    @JsonProperty("PBCT_NO")  // ⭐ 추가
    @JacksonXmlProperty(localName = "PBCT_NO")
    private String pbctNo;

    @JsonProperty("CLTR_MNMT_NO")
    @JacksonXmlProperty(localName = "CLTR_MNMT_NO")
    private String cltrMnmtNo;

    @JsonProperty("CLTR_NM")
    @JacksonXmlProperty(localName = "CLTR_NM")
    private String cltrNm;

    @JsonProperty("DPSL_MTD_CD")
    @JacksonXmlProperty(localName = "DPSL_MTD_CD")
    private String dpslMtdCd;

    @JsonProperty("CTGR_HIRK_ID")
    @JacksonXmlProperty(localName = "CTGR_HIRK_ID")
    private String ctgrHirkId;

    @JsonProperty("CTGR_HIRK_ID_MID")
    @JacksonXmlProperty(localName = "CTGR_HIRK_ID_MID")
    private String ctgrHirkIdMid;

    private String sido;
    private String sgk;
    private String emd;

    @JsonProperty("APSL_ASES_AVG_AMT")
    @JacksonXmlProperty(localName = "APSL_ASES_AVG_AMT")
    private String goodsPriceStr;

    @JsonProperty("MIN_BID_PRC")
    @JacksonXmlProperty(localName = "MIN_BID_PRC")
    private String openPriceStr;

    @JsonProperty("PBCT_BEGN_DTM")
    @JacksonXmlProperty(localName = "PBCT_BEGN_DTM")
    private String pbctBegnDtm;

    @JsonProperty("PBCT_CLS_DTM")
    @JacksonXmlProperty(localName = "PBCT_CLS_DTM")
    private String pbctClsDtm;

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