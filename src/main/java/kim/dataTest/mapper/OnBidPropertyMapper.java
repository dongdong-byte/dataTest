package kim.dataTest.mapper;


import kim.dataTest.domain.OnBidProperty;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Mapper
public interface OnBidPropertyMapper {
//    단건등록
    int insert(OnBidProperty onBidProperty);
//    다건 일괄등록
    int insertBatch(List<OnBidProperty> onBidPropertyList);
//    전체 목록 조회
    List<OnBidProperty> findAll();
//    ID로 단건조회
    OnBidProperty findOnBidPropertyById(Long id);
//    물건 관리 번호로 조회(중복 체크용)
    OnBidProperty findOnBidPropertyByCltrMnmtNo(String cltrMnmtNo);
//    전체 건수 조회
    int count();
//    UPDATE
//    단건수정
int updateOnBidProperty(OnBidProperty onBidProperty);
//DELETE
//    단건 삭제
    int deleteOnBidPropertyById(Long id);
//    전체 삭제
    int deleteAll();
//    검색 기능
//    동적 검색 (물건명, 시도, 시군구,처분방법 코드 , 가격대등)
//    @param->
//    사용 예: params.put("cltrNm", "아파트"), params.put("sido", "서울")
    List<OnBidProperty> searchOnBidProperty(Map<String , Object> params);
//    정렬 기능
//    가격순 정렬 조회
//    @param priceType - "goods" (감정가) 또는 "open" (최저입찰가)
//    @param order - "ASC" 또는 "DESC"
    List<OnBidProperty> findAllOrderByPrice(@Param("priceType") String priceType,
    @Param("order") String order);
//    페이징 기능
//    페이징 처리된 목록 조회
//     * @param offset - 시작 위치 (0부터 시작)
//     * @param limit - 조회할 개수
    List<OnBidProperty> findAllWithPaging(@Param("offset") int offset, @Param("limit") int limit);
//    검색 + 페이징 조합
List<OnBidProperty> searchWithPaging(@Param("params") Map<String ,Object> params ,@Param("offset") int offset, @Param("limit") int limit );

//검색결과 변수 (페이징용)
    int countBySearch(Map<String , Object> params);


}

