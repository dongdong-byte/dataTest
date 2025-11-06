package kim.dataTest.dto;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OnBidPropertySearchDto {
    private String cltrNm;//    물건명
    private String sido;//    시도
    private String sgk;                 // 시군구
    private String emd;//    읍면동
    private String dpslMtdCd;//    처분방법 코드 (0001: 매각 , 0002: 임대)
    private Long goodsPriceFrom;//    감정가하한
    private Long goodsPriceTo;//    감정가상한
    private Long openPriceFrom;//    최저 입찰가하한
    private Long openPriceTo; //    최저 입찰가상한
}
