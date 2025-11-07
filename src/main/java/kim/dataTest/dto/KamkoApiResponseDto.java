package kim.dataTest.dto;



import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


@Getter
@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class KamkoApiResponseDto {
    
//    @JsonCreator
//    public static KamkoApiResponseDto fromString(String value) {
//        return new KamkoApiResponseDto();
//    }
//    온비드 API응답용

    @JsonProperty("RNUM")
    private String rnum;//    순번

    @JsonProperty("CLTR_MNMT_NO")
    private String cltrMnmtNo;//    물건 관리번호

    @JsonProperty("CLTR_NM")
    private String cltrNm;//    물건명

    @JsonProperty("DPSL_MTD_CD")
    private String dpslMtdCd;//    처분방법 코드 (0001: 매각 , 0002: 임대)

    @JsonProperty("CTGR_HIRK_ID")
    private  String ctgrHirkId;//    카테고리 상위 ID

    @JsonProperty("CTGR_HIRK_ID_MID")
    private String ctgrHirkIdMid;//    카테고리 중위 ID

    //    sido.sgk,emd에는 xml응답에 별도 태그가 없으므로,
//    OnBidPropertyService 에서 파싱후 수동으로 채워야한다
//    (LDNM_ADRS를 파싱하거나, API 요청 시 사용한 sido 값을 활용)
    private String sido;//    시도
    private String sgk;                 // 시군구
    private String emd;//    읍면동
    @JsonProperty("APSL_ASES_AVG_AMT")
    private Long goodsPrice;//    감정가

    @JsonProperty("MIN_BID_PRC")
    private Long openPrice;//    최저 입찰가

    @JsonProperty("PBCT_BEGN_DTM")
    private  String pbctBegnDtm;//    공고 시작일

    @JsonProperty("PBCT_CLS_DTM")
    private  String pbctClsDtm; //    공고 종료일(YYYYMMDD)
}


