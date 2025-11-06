package kim.dataTest.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OnBidPropertyDetailDto {
//    상세/ 수정화면용
    private Long id;//    고유 ID
    private String cltrMnmtNo;//    물건 관리번호
    private String cltrNm;//    물건명
    private String dpslMtdCd;//    처분방법 코드 (0001: 매각 , 0002: 임대)
    private  String ctgrHirkId;//    카테고리 상위 ID
    private String ctgrHirkIdMid;//    카테고리 중위 ID
    private String sido;//    시도
    private String sgk;                 // 시군구
    private String emd;//    읍면동
    private Long goodsPrice;//    감정가
    private Long openPrice;//    최저 입찰가
    private  String pbctBegnDtm;//    공고 시작일
    private  String pbctClsDtm; //    공고 종료일(YYYYMMDD)

}
