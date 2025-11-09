package kim.dataTest.contorller;


import kim.dataTest.dto.OnBidPropertyCreateDto;
import kim.dataTest.dto.OnBidPropertyDetailDto;
import kim.dataTest.dto.OnBidPropertyListDto;
import kim.dataTest.dto.OnBidPropertySearchDto;
import kim.dataTest.service.OnBidPropertyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/onbidproperty")
@Slf4j
@RequiredArgsConstructor
public class OnBidPropertyContorller {

    private final OnBidPropertyService onBidPropertyService;
//    ==========목록=============
//    전체 목록 조회
//    GET/onbidproperty
    @GetMapping
    public String list(Model model) {
        List<OnBidPropertyListDto> properties = onBidPropertyService.findAll(); // TODO: findAll 메서드 구현 필요
        int totalCount = onBidPropertyService.countAll();

        model.addAttribute("properties", properties);
        model.addAttribute("totalCount", totalCount);
        return "onbidproperty/list";
    }
//검색 결과 목록
//    GET/onbidproperty/search
    @GetMapping("/search")
    public String search(OnBidPropertySearchDto onBidPropertySearchDto, Model model) {
        List<OnBidPropertyListDto> properties= onBidPropertyService.search(onBidPropertySearchDto);
        int searchCount = onBidPropertyService.countSearchResults(onBidPropertySearchDto); // TODO: countSearchResults 메서드 구현 필요
        model.addAttribute("properties", properties);
        model.addAttribute("searchCount", searchCount);
        model.addAttribute("onBidPropertySearchDto", onBidPropertySearchDto);

        return "onbidproperty/list";

    }

//    페이징 처리된 목록
//    Get/onbidproperty/paging?page=1&size=10
    @GetMapping("/paging")
public String listWithPaging(
        @RequestParam(defaultValue = "1") int  page,
        @RequestParam(defaultValue = "10") int size,
        Model model
    ){
        int offset = (page - 1) * size;
        List<OnBidPropertyListDto> properties = onBidPropertyService.findAllWithPaging(offset ,size); // TODO: findAllWithPaging 메서드 구현 필요
        int  totalCount = onBidPropertyService.countAll();
        int totalPages = (int) Math.ceil((double) totalCount/ size);
        model.addAttribute("properties", properties);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", totalPages);

        return "onbidproperty/list";
    }
// 검색 + 페이징 조합
//    Get/onbidproperty/search/paging
@GetMapping("search/paging")
    public String searchWithPaging(
        @RequestParam(defaultValue = "1") int  page,
        @RequestParam(defaultValue = "10") int size,
        OnBidPropertySearchDto onBidPropertySearchDto,
        Model model
){
    int offset = (page - 1) * size;
    List<OnBidPropertyListDto> properties = onBidPropertyService.searchWithPaging(onBidPropertySearchDto ,offset,size); // TODO: findAllWithPaging 메서드 구현 필요
    int  searchCount = onBidPropertyService.countSearchResults(onBidPropertySearchDto); // TODO: countSearchResults 메서드 구현 필요
    int totalPages = (int) Math.ceil((double) searchCount/ size);
    model.addAttribute("properties", properties);
    model.addAttribute("searchCount", searchCount);
    model.addAttribute("searchDto", onBidPropertySearchDto);
    model.addAttribute("pageSize", size);
    model.addAttribute("totalPages", totalPages);
    model.addAttribute("currentPage", page);

    return "onbidproperty/list";


}
//========상세 ================
//    상세 조회
//    Get/onbidproperty/{id}
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id ,Model model){
        OnBidPropertyDetailDto property = onBidPropertyService.findById(id);
        model.addAttribute("property", property);
        return "onbidproperty/detail";
    }
//     물건 관리 번호로 상세 조회
//    GET /onbidproperty/cltr-mnmt-no/{cltrMnmtNo}
    @GetMapping("/cltr-mnmt-no/{cltrMnmtNo}")
    public String detailByCltrMnmtNo(@PathVariable String cltrMnmtNo ,Model model){
        OnBidPropertyDetailDto property = onBidPropertyService.findByCltrMnmtNo(cltrMnmtNo);
        model.addAttribute("property", property);
        return "onbidproperty/detail";

    }
//    =======등록==========
//    등록 폼
//    Get /onbidproperty/new
    @GetMapping("/new")
    public  String createForm(Model model){
        model.addAttribute("property", new OnBidPropertyCreateDto());
        return "onbidproperty/form";
    }
//    등록 처리
//    POST /onbidproperty
    @PostMapping
    public String create(
            @ModelAttribute OnBidPropertyCreateDto dto,
            RedirectAttributes redirectAttributes
    ) {
        Long id = onBidPropertyService.create(dto);
        redirectAttributes.addFlashAttribute("message", "물건이 등록되었습니다");
        return "redirect:/onbidproperty/" + id;
    }

//    API에서 데이터 가져오기
//    Post /onbidproperty/fetch
    @PostMapping("/fetch")
    public  String fetchFromApi(
            @RequestParam(required = false) String sido,
            @RequestParam(defaultValue = "10") int  numOfRows,
            @RequestParam(defaultValue = "1") int pageNo,
            RedirectAttributes redirectAttributes) throws IOException{

        try {
            int savedCount = onBidPropertyService.fetchAndSavePropertiesFromApi(sido, numOfRows, pageNo);
            redirectAttributes.addFlashAttribute("message", savedCount + "건이 저장되었습니다");
        } catch (IOException e) {
            log.error("API 호출 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("message", "API 호출 실패: " + e.getMessage());
        }
        return  "redirect:/onbidproperty";

    }
//            ==========수정=========
//    수정 폼
//    Get /onbidproperty/{id}/edit
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id , Model model){
        OnBidPropertyDetailDto property = onBidPropertyService.findById(id);
        model.addAttribute("property", property);
        return "onbidproperty/form";
    }
//    수정 처리
//    Put /onbidproperty/{id}/edit

    @PostMapping("/{id}/edit")
    public  String update(
            @PathVariable Long id,
            @ModelAttribute OnBidPropertyDetailDto dto,
            RedirectAttributes redirectAttributes
    ){
        onBidPropertyService.update(id, dto);
        redirectAttributes.addFlashAttribute("message","물건이 수정되었습니다");

        return  "redirect:/onbidproperty/"+id;

    }
//    ======삭제
//     단건 삭제
//    Post /onbidproperty/{id}/delete
    @PostMapping("/{id}/delete")
    public   String delete(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ){
        onBidPropertyService.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "물건이 삭제되었습니다");
        return  "redirect:/onbidproperty";
    }
//    전체 삭제 (위험! 실무에서는 사용주의)
//    * POST /onbidproperty/deleteAll
    @PostMapping("/deleteAll")
    public String deleteAll(
            RedirectAttributes redirectAttributes
    ){
        int count = onBidPropertyService.deleteAll();
        redirectAttributes.addFlashAttribute("message", count+ "건이 삭제되었습니다");
        return  "redirect:/onbidproperty";

    }
}

