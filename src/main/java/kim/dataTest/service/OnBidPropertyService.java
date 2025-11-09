package kim.dataTest.service;



import kim.dataTest.domain.OnBidProperty;
import kim.dataTest.dto.*;
import kim.dataTest.mapper.OnBidPropertyMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OnBidPropertyService
{
    private final KamcoApiService kamcoApiService;
    private final OnBidPropertyMapper onBidPropertyMapper;
    private  final ModelMapper modelMapper;
//    Create
//    Api 에서 데이터 가져와서 DB에 저장
    @Transactional
    public int fetchAndSavePropertiesFromApi(String sido , int numOfRows , int pageNo) throws IOException
    {
//    Api 호출 (String으로 반환)
        String xmlResponse = kamcoApiService.fetchPropertiesBySido(sido, numOfRows, pageNo);
//       xml 파싱 (String -> List<DTO>)
        List<KamkoApiResponseDto>apiData = kamcoApiService.parseXmlResponse(xmlResponse); // TODO: XML 파싱 구현 필요
  int savedCount = 0;
  for (KamkoApiResponseDto dto : apiData)
  {
//      API 응답에슨ㄴ sido, sgk,emd가 분리 되어 있지 않으므로 수동으로 채워준다
//      만약 sido가 있다면
      if(sido != null && !sido.isEmpty()){
          dto.setSido(sido);
      }

//        중복 체크
      OnBidProperty existingProperty = onBidPropertyMapper.findOnBidPropertyByPbctNo(dto.getPbctNo());
      if(existingProperty == null){
//          DTO->Entity로 바꾼다음에 저장
          OnBidProperty entity = modelMapper.map(dto, OnBidProperty.class);
          onBidPropertyMapper.insert(entity);
          savedCount++;
          log.info("저장 완료: 공고번호={}, 물건관리번호={}", dto.getPbctNo(), dto.getCltrMnmtNo());
      }else {
          log.info("이미 존재하는 물건 : {}", dto.getPbctNo());
      }

  }
        log.info("총 {}건 저장 완료", savedCount);
        return savedCount;
    }

//    단건등록
    @Transactional
    public  Long create(OnBidPropertyCreateDto dto)
    {
        OnBidProperty entity = modelMapper.map(dto , OnBidProperty.class );
        onBidPropertyMapper.insert(entity);
        return entity.getId();
    }
//    다건 일괄등록
    @Transactional
    public  int createBatch(List<OnBidPropertyCreateDto> dtolist) {
List<OnBidProperty> entities = dtolist.stream()
        .map(dto -> modelMapper.map(dto, OnBidProperty.class))
        .collect(Collectors.toList());
return onBidPropertyMapper.insertBatch(entities);

    }
//    ==============READ===============
//    전체 목록 조회
    public   List<OnBidPropertyListDto> findAll(){
        List<OnBidProperty> properties = onBidPropertyMapper.findAll();
        return properties.stream()
                .map(property -> modelMapper.map(property, OnBidPropertyListDto.class))
                .collect(Collectors.toList());
    }
//    ID로 단건 조회
    public OnBidPropertyDetailDto findById(Long id){
        OnBidProperty onBidProperty = onBidPropertyMapper.findOnBidPropertyById(id);
        if(onBidProperty == null){
            throw  new IllegalArgumentException("존재 하지 않는 물건입니다 . ID : " + id);
        }
        return  modelMapper.map(onBidProperty, OnBidPropertyDetailDto.class);
    }
//    물건 관리 번호로 조회
    public OnBidPropertyDetailDto findByCltrMnmtNo(String cltrMnmtNo){
        OnBidProperty onBidProperty = onBidPropertyMapper.findOnBidPropertyByCltrMnmtNo(cltrMnmtNo);
        if(onBidProperty == null){
            throw  new IllegalArgumentException("존재 하지 않는 물건입니다 . 관리번호 : " + cltrMnmtNo);
        }
        return  modelMapper.map(onBidProperty, OnBidPropertyDetailDto.class);

    }
//    전체 건수 조회
    public  int countAll(){
        return onBidPropertyMapper.count();
    }
//    =============UPDATE================
//     수정
    @Transactional
    public  void  update( Long id , OnBidPropertyDetailDto dto){
        OnBidProperty existingProperty = onBidPropertyMapper.findOnBidPropertyById(id);
        if(existingProperty == null){
            throw  new IllegalArgumentException("존재 하지 않는 물건입니다 . ID : " + id);
        }
        OnBidProperty entity = modelMapper.map(dto, OnBidProperty.class);
        entity.setId(id);//        ID는 반드시 설정
        onBidPropertyMapper.updateOnBidProperty(entity);
    }
//    =============DELETE===============
//  전체  삭제
    @Transactional
    public  int deleteAll(){
        return onBidPropertyMapper.deleteAll();
    }
//    단건 삭제
    @Transactional
    public void  deleteById(Long id){
        OnBidProperty existingProperty = onBidPropertyMapper.findOnBidPropertyById(id);
        if(existingProperty == null){
            throw  new IllegalArgumentException("존재 하지 않는 물건입니다 . ID : " + id);
        }
     onBidPropertyMapper.deleteOnBidPropertyById(id);

    }
//    =========검색==============
//     동적 검색
    public  List<OnBidPropertyListDto> search (OnBidPropertySearchDto searchDto){
        Map<String ,Object> params = convertSearchDtoToMap(searchDto);
        List<OnBidProperty> properties = onBidPropertyMapper.searchOnBidProperty(params);
        return properties.stream()
                .map(property -> modelMapper.map(property, OnBidPropertyListDto.class))
                .collect(Collectors.toList());
    }
//    검색 결과 건수
    public  int countSearchResults(OnBidPropertySearchDto searchDto){
        Map<String ,Object> params = convertSearchDtoToMap(searchDto);
        return onBidPropertyMapper.countBySearch(params);
    }

//    =========정렬 ==============
//     가격순 정렬 조회
    public List<OnBidPropertyListDto> findAllOrderByPrice(String priceType , String order){
        List<OnBidProperty> properties = onBidPropertyMapper.findAllOrderByPrice(priceType, order);
        return properties.stream()
                .map(property -> modelMapper.map(property, OnBidPropertyListDto.class))
                .collect(Collectors.toList());

    }
//    ========페이징==============
//    페이징 처리된 목록 조회
    public List<OnBidPropertyListDto> findAllWithPaging(int offset , int limit){
        List<OnBidProperty> properties = onBidPropertyMapper.findAllWithPaging(offset, limit);
        return properties.stream()
                .map(property -> modelMapper.map(property, OnBidPropertyListDto.class))
                .collect(Collectors.toList());

    }

//    검색 + 페이징 조합
    public List<OnBidPropertyListDto> searchWithPaging(OnBidPropertySearchDto searchDto ,int offset , int limit )
    {Map<String ,Object> params = convertSearchDtoToMap(searchDto);
        List<OnBidProperty> properties = onBidPropertyMapper.searchWithPaging(params,offset,limit);
        return properties.stream()
                .map(property -> modelMapper.map(property, OnBidPropertyListDto.class))
                .collect(Collectors.toList());

    }

    //     ========== Private Helper ==========
// SearchDto를 Map으로 변환 (MyBatis에서 사용)
    private Map<String, Object> convertSearchDtoToMap(OnBidPropertySearchDto searchDto) {
       Map<String , Object> params = new HashMap<>();
        if(searchDto.getCltrNm() != null) params.put("cltrNm" , searchDto.getCltrNm());
        if(searchDto.getSido() != null) params.put("sido" , searchDto.getSido());
        if(searchDto.getSgk() != null) params.put("sgk" , searchDto.getSgk());
        if(searchDto.getEmd() != null) params.put("emd" , searchDto.getEmd());
        if(searchDto.getDpslMtdCd() != null) params.put("dpslMtdCd" , searchDto.getDpslMtdCd());
        if(searchDto.getGoodsPriceFrom() != null) params.put("goodsPriceFrom" , searchDto.getGoodsPriceFrom());
        if(searchDto.getGoodsPriceTo() != null) params.put("goodsPriceTo" , searchDto.getGoodsPriceTo());
        if(searchDto.getOpenPriceFrom() != null) params.put("openPriceFrom" , searchDto.getOpenPriceFrom());
        if(searchDto.getOpenPriceTo() != null) params.put("openPriceTo" , searchDto.getOpenPriceTo());






        return params;
    }

}
