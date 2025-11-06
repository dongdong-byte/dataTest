package kim.dataTest.contorller;


import kim.dataTest.dto.OnBidPropertyCreateDto;
import kim.dataTest.dto.OnBidPropertyDetailDto;
import kim.dataTest.dto.OnBidPropertyListDto;
import kim.dataTest.dto.OnBidPropertySearchDto;
import kim.dataTest.service.OnBidPropertyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/onbidproperty")
@RequiredArgsConstructor
@Slf4j
public class OnBidPropertyRestController {
    private  final OnBidPropertyService onBidPropertyService;
//    =======create==========
//    api에서 데이터를 가져와서 저장
//     POST /api/onbidproperties/fetch?sido=서울&numOfRows=10&pageNo=1
@PostMapping("/fetch")
public ResponseEntity<Map<String ,Object>> fetchFromApi(
        @RequestParam(required = false)String sido,
        @RequestParam(defaultValue = "10")int numOfRows,
        @RequestParam(defaultValue = "1")int pageNo
) {
    try {
        int savedCount = onBidPropertyService.fetchAndSavePropertiesFromApi(sido,numOfRows ,pageNo);
        Map<String , Object> response = new HashMap<>();
        response.put("success",true);
        response.put("message",savedCount + "건이 저장되었습니다");
        response.put("savedCount" ,savedCount);
        return ResponseEntity.ok(response);
    }catch (IOException e)
    {
        log.error("API호출중 오류 발생",e);
        Map<String , Object> response = new HashMap<>();
        response.put("success",false);
        response.put("message","API호출 실해 했습니다." +e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

}
// 단건 등록
//    Post/api/onbidproperties
    @PostMapping
    public ResponseEntity<Map<String ,Object>> create(
            @RequestBody OnBidPropertyCreateDto dto
    ){
    Long id = onBidPropertyService.create(dto);

        Map<String , Object> response = new HashMap<>();
        response.put("success",true);
        response.put("message","물건이 등록되었습니다");
        response.put("id" ,id);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }
//다건 일괄등록
//    Post/api/onbidproperties/batch
    @PostMapping("/batch")
    public ResponseEntity<Map<String ,Object>> createBetch(
            @RequestBody List<OnBidPropertyCreateDto> dtoList
            ){
        int count = onBidPropertyService.createBatch(dtoList);

        Map<String , Object> response = new HashMap<>();
        response.put("success",true);
        response.put("message","물건이 등록되었습니다");
        response.put("count" ,count);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }
// ==========Read==============
//     전체 목록 조회
//    GET /api/onbidproperties
    @GetMapping
    public ResponseEntity<List<OnBidPropertyListDto>> findAll(){
    List<OnBidPropertyListDto> properties = onBidPropertyService.findAll();
    return ResponseEntity.ok(properties);
    }
//    ID로 단건조회
//    GET /api/properties/{id}
@GetMapping("/{id}")
public ResponseEntity<OnBidPropertyDetailDto> findById(@PathVariable Long id){
    OnBidPropertyDetailDto properties = onBidPropertyService.findById(id);
    return ResponseEntity.ok(properties);
}
//물건관리 번호로 조회
//GET /api/onbidproperties/managementNumber/{managementNumber}
@GetMapping("/managementNumber/{managementNumber}")
public ResponseEntity<OnBidPropertyDetailDto> findByCltrMnmNo(@PathVariable String managementNumber){
    OnBidPropertyDetailDto properties = onBidPropertyService.findByCltrMnmtNo(managementNumber);
    return ResponseEntity.ok(properties);
}
//전체 건수 조회
//GET /api/onbidproperties/count
   @GetMapping("/count")
    public  ResponseEntity<Map<String , Integer>> countAll(){
    int count = onBidPropertyService.countAll();
    Map<String ,Integer> response = new HashMap<>();
    response.put("count", count);
    return ResponseEntity.ok(response);
   }
//   ==========UPDATE=============
//수정
//    PUT /api/onbidproperties/{id}

    @PostMapping("/{id}")
    public  ResponseEntity<Map<String,Object>> update(
            @PathVariable Long id,
            @RequestBody OnBidPropertyDetailDto dto
    ){
    onBidPropertyService.update(id, dto);
        Map<String ,Object> response = new HashMap<>();
        response.put("success",true);
        response.put("message","물건이 수정되었습니다");
    return ResponseEntity.ok(response);
    }
//    ===========DELETE============
//    삭제
//DELETE /api/onbidproperties/{id}
    @DeleteMapping("/{id}")
    public  ResponseEntity<Map<String,Object>> deleteById(
            @PathVariable Long id){
    onBidPropertyService.deleteById(id);
        Map<String ,Object> response = new HashMap<>();
        response.put("success",true);
        response.put("message","물건이 삭제되었습니다");
        return ResponseEntity.ok(response);

}

//전체 삭제
//DELETE /api/onbidproperties
@DeleteMapping
public  ResponseEntity<Map<String,Object>> deleteAll(){
    int count = onBidPropertyService.deleteAll();

    Map<String ,Object> response = new HashMap<>();
    response.put("success",true);
    response.put("message",count +  "물건이 삭제되었습니다");
    response.put("count", count);
    return ResponseEntity.ok(response);

}
//=======검색========
//     동적 검색
//    Get /api/onbidproperties/search
    @GetMapping("/search")
    public  ResponseEntity<List<OnBidPropertyListDto>> search(OnBidPropertySearchDto onBidPropertySearchDto){
List<OnBidPropertyListDto> properties = onBidPropertyService.search(onBidPropertySearchDto);
return ResponseEntity.ok(properties);

    }
//    검색 결과 건수
//     Get /api/onbidproperties/search/count
    public  ResponseEntity<Map<String,Object>> countSearchResults(OnBidPropertySearchDto onBidPropertySearchDto)
    {
        int count = onBidPropertyService.countSearchResults(onBidPropertySearchDto);
        Map<String ,Object> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }
//가격순 정렬 조회
//    Get /api/onbidproperties/sorted?priceType=goods&order=ASC
    @GetMapping("/sorted")
    public  ResponseEntity<List<OnBidPropertyListDto>> findAllOrderByPrice(
            @RequestParam(defaultValue = "goods") String priceType,
            @RequestParam(defaultValue = "ASC")String order
    ){
        List<OnBidPropertyListDto> properties = onBidPropertyService.findAllOrderByPrice( priceType,order);
        return ResponseEntity.ok(properties);
    }
//    ===========페이징=============
//    페이징 처리된 목록 조회
//     Get /api/onbidproperties/paging?offset=0&limit=10
@GetMapping("/paging")
    public ResponseEntity<List<OnBidPropertyListDto>> findAllWithPaging(
        @RequestParam(defaultValue = "0") int offeset,
        @RequestParam(defaultValue = "10")int limit
){
    List<OnBidPropertyListDto> properties = onBidPropertyService.findAllWithPaging(offeset, limit);
    return ResponseEntity.ok(properties);

}
//검색 + 페이징 조합
//    Get /api/onbidproperties/search/paging
    @GetMapping("/search/paging")
    public ResponseEntity<List<OnBidPropertyListDto>> searchWithPaging(
            OnBidPropertySearchDto onBidPropertySearchDto,
            @RequestParam(defaultValue = "0") int offeset,
            @RequestParam(defaultValue = "10")int limit
    ){
        List<OnBidPropertyListDto> properties = onBidPropertyService.searchWithPaging(onBidPropertySearchDto, offeset, limit);
        return ResponseEntity.ok(properties);

    }



}
