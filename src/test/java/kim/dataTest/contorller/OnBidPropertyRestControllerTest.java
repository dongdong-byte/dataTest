package kim.dataTest.contorller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kim.dataTest.contorller.OnBidPropertyRestController;

import kim.dataTest.dto.OnBidPropertyCreateDto;
import kim.dataTest.dto.OnBidPropertyDetailDto;
import kim.dataTest.dto.OnBidPropertyListDto;
import kim.dataTest.service.OnBidPropertyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OnBidPropertyRestController.class)
class OnBidPropertyRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OnBidPropertyService service;

    @Test
    @DisplayName("전체 목록 조회 API")
    void findAllTest() throws Exception {
        // given
        List<OnBidPropertyListDto> properties = Arrays.asList(
                createListDto(1L, "물건1"),
                createListDto(2L, "물건2")
        );
        given(service.findAll()).willReturn(properties);

        // when & then
        mockMvc.perform(get("/api/onbidproperty"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    @DisplayName("ID로 단건 조회 API")
    void findByIdTest() throws Exception {
        // given
        OnBidPropertyDetailDto dto = createDetailDto(1L, "테스트 물건");
        given(service.findById(1L)).willReturn(dto);

        // when & then
        mockMvc.perform(get("/api/onbidproperty/{id}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.cltrNm").value("테스트 물건"));
    }

    @Test
    @DisplayName("물건 관리번호로 조회 API")
    void findByCltrMnmtNoTest() throws Exception {
        // given
        OnBidPropertyDetailDto dto = createDetailDto(1L, "테스트 물건");
        given(service.findByCltrMnmtNo("2024-TEST-001")).willReturn(dto);

        // when & then
        mockMvc.perform(get("/api/onbidproperty/managementNumber/{managementNumber}", "2024-TEST-001"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cltrMnmtNo").value("2024-TEST-001"));
    }

    @Test
    @DisplayName("전체 건수 조회 API")
    void countAllTest() throws Exception {
        // given
        given(service.countAll()).willReturn(10);

        // when & then
        mockMvc.perform(get("/api/onbidproperty/count"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(10));
    }

    @Test
    @DisplayName("단건 등록 API")
    void createTest() throws Exception {
        // given
        OnBidPropertyCreateDto createDto = OnBidPropertyCreateDto.builder()
                .cltrMnmtNo("2024-TEST-001")
                .cltrNm("테스트 물건")
                .dpslMtdCd("0001")
                .sido("서울")
                .goodsPrice(10000000L)
                .openPrice(8000000L)
                .build();

        given(service.create(any(OnBidPropertyCreateDto.class))).willReturn(1L);

        // when & then
        mockMvc.perform(post("/api/onbidproperty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("다건 일괄 등록 API")
    void createBatchTest() throws Exception {
        // given
        List<OnBidPropertyCreateDto> dtoList = Arrays.asList(
                createCreateDto("2024-TEST-001"),
                createCreateDto("2024-TEST-002")
        );

        given(service.createBatch(anyList())).willReturn(2);

        // when & then
        mockMvc.perform(post("/api/onbidproperty/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoList)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.count").value(2));
    }

    @Test
    @DisplayName("수정 API")
    void updateTest() throws Exception {
        // given
        OnBidPropertyDetailDto detailDto = createDetailDto(1L, "수정된 물건");
        willDoNothing().given(service).update(eq(1L), any(OnBidPropertyDetailDto.class));

        // when & then
        mockMvc.perform(post("/api/onbidproperty/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(detailDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("물건이 수정되었습니다"));
    }

    @Test
    @DisplayName("단건 삭제 API")
    void deleteByIdTest() throws Exception {
        // given
        willDoNothing().given(service).deleteById(1L);

        // when & then
        mockMvc.perform(delete("/api/onbidproperty/{id}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("물건이 삭제되었습니다"));
    }

    @Test
    @DisplayName("전체 삭제 API")
    void deleteAllTest() throws Exception {
        // given
        given(service.deleteAll()).willReturn(5);

        // when & then
        mockMvc.perform(delete("/api/onbidproperty"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.count").value(5));
    }

    @Test
    @DisplayName("페이징 처리된 목록 조회 API")
    void findAllWithPagingTest() throws Exception {
        // given
        List<OnBidPropertyListDto> properties = Arrays.asList(
                createListDto(1L, "물건1"),
                createListDto(2L, "물건2")
        );
        given(service.findAllWithPaging(0, 10)).willReturn(properties);

        // when & then
        mockMvc.perform(get("/api/onbidproperty/paging")
                        .param("offeset", "0")
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("가격순 정렬 조회 API")
    void findAllOrderByPriceTest() throws Exception {
        // given
        List<OnBidPropertyListDto> properties = Arrays.asList(
                createListDto(1L, "물건1")
        );
        given(service.findAllOrderByPrice("goods", "ASC")).willReturn(properties);

        // when & then
        mockMvc.perform(get("/api/onbidproperty/sorted")
                        .param("priceType", "goods")
                        .param("order", "ASC"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    private OnBidPropertyListDto createListDto(Long id, String cltrNm) {
        return OnBidPropertyListDto.builder()
                .id(id)
                .cltrMnmtNo("2024-TEST-001")
                .cltrNm(cltrNm)
                .sido("서울")
                .sgk("강남구")
                .goodsPrice(10000000L)
                .openPrice(8000000L)
                .pbctBegnDtm("20240101")
                .dpslMtdCd("0001")
                .build();
    }

    private OnBidPropertyDetailDto createDetailDto(Long id, String cltrNm) {
        return OnBidPropertyDetailDto.builder()
                .id(id)
                .cltrMnmtNo("2024-TEST-001")
                .cltrNm(cltrNm)
                .dpslMtdCd("0001")
                .ctgrHirkId("10000")
                .ctgrHirkIdMid("10100")
                .sido("서울")
                .sgk("강남구")
                .emd("역삼동")
                .goodsPrice(10000000L)
                .openPrice(8000000L)
                .pbctBegnDtm("20240101")
                .pbctClsDtm("20240131")
                .build();
    }

    private OnBidPropertyCreateDto createCreateDto(String cltrMnmtNo) {
        return OnBidPropertyCreateDto.builder()
                .cltrMnmtNo(cltrMnmtNo)
                .cltrNm("테스트 물건")
                .dpslMtdCd("0001")
                .sido("서울")
                .goodsPrice(10000000L)
                .openPrice(8000000L)
                .build();
    }
}
