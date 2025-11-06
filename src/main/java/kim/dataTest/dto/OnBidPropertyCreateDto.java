package kim.dataTest.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OnBidPropertyCreateDto {
@NotBlank(message = "물건 관리 번호는 필수입니다.")
    private String cltrMnmtNo;//    물건 관리번호
    @NotBlank(message = "물건명은 필수입니다.")
    private String cltrNm;//    물건명
    @NotBlank(message = "처분방법 코드는 필수입니다.")
    private String dpslMtdCd;//    처분방법 코드 (0001: 매각 , 0002: 임대)
    private  String ctgrHirkId;//    카테고리 상위 ID
    private String ctgrHirkIdMid;//    카테고리 중위 ID
    @NotBlank(message = "시도는 필수입니다.")
    private String sido;//    시도
    private String sgk;                 // 시군구
    private String emd;//    읍면동
    @NotBlank(message = "감정가는 필수입니다.")
    @Min(value = 0 , message = "감정가는  0 이상이어야 합니다." )
    private Long goodsPrice;//    감정가
    @NotBlank(message = "최저 입찰가는 필수입니다.")
    @Min(value = 0 , message = "최저 입찰가는  0 이상이어야 합니다." )
    private Long openPrice;//    최저 입찰가
    private  String pbctBegnDtm;//    공고 시작일
    private  String pbctClsDtm; //    공고 종료일(YYYYMMDD)
}
