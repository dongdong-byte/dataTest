package kim.dataTest;

import kim.dataTest.domain.OnBidProperty;
import kim.dataTest.dto.OnBidPropertyCreateDto;
import kim.dataTest.dto.OnBidPropertyDetailDto;
import kim.dataTest.mapper.OnBidPropertyMapper;
import kim.dataTest.service.OnBidPropertyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@org.springframework.test.context.ActiveProfiles("test")
class OnBidPropertyIntegrationTest {

    @Autowired
    private OnBidPropertyService service;

    @Autowired
    private OnBidPropertyMapper mapper;

    @Test
    @DisplayName("통합 테스트 - 등록부터 삭제까지 전체 플로우")
    void fullFlowIntegrationTest() {
        // 1. 등록
        OnBidPropertyCreateDto createDto = OnBidPropertyCreateDto.builder()
                .cltrMnmtNo("2024-INTEGRATION-001")
                .cltrNm("통합 테스트 물건")
                .dpslMtdCd("0001")
                .sido("서울")
                .sgk("강남구")
                .emd("역삼동")
                .goodsPrice(10000000L)
                .openPrice(8000000L)
                .pbctBegnDtm("20240101")
                .pbctClsDtm("20240131")
                .build();

        Long id = service.create(createDto);
        assertThat(id).isNotNull();

        // 2. 조회
        OnBidPropertyDetailDto found = service.findById(id);
        assertThat(found).isNotNull();
        assertThat(found.getCltrNm()).isEqualTo("통합 테스트 물건");

        // 3. 수정
        OnBidPropertyDetailDto updateDto = OnBidPropertyDetailDto.builder()
                .id(found.getId())
                .cltrMnmtNo(found.getCltrMnmtNo())
                .cltrNm("수정된 물건명")
                .dpslMtdCd(found.getDpslMtdCd())
                .sido(found.getSido())
                .sgk(found.getSgk())
                .emd(found.getEmd())
                .goodsPrice(found.getGoodsPrice())
                .openPrice(7000000L)
                .pbctBegnDtm(found.getPbctBegnDtm())
                .pbctClsDtm(found.getPbctClsDtm())
                .build();
        service.update(id, updateDto);

        OnBidPropertyDetailDto updated = service.findById(id);
        assertThat(updated.getCltrNm()).isEqualTo("수정된 물건명");
        assertThat(updated.getOpenPrice()).isEqualTo(7000000L);

        // 4. 삭제
        service.deleteById(id);

        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("통합 테스트 - 페이징 기능")
    void pagingIntegrationTest() {
        // given - 15개 데이터 등록
        for (int i = 1; i <= 15; i++) {
            OnBidProperty property = OnBidProperty.builder()
                    .cltrMnmtNo("2024-PAGING-" + String.format("%03d", i))
                    .cltrNm("페이징 테스트 물건 " + i)
                    .dpslMtdCd("0001")
                    .sido("서울")
                    .goodsPrice(10000000L)
                    .openPrice(8000000L)
                    .pbctBegnDtm("20240101")
                    .pbctClsDtm("20240131")
                    .build();
            mapper.insert(property);
        }

        // when - 첫 번째 페이지 조회 (10개)
        var page1 = service.findAllWithPaging(0, 10);

        // then
        assertThat(page1).hasSize(10);
    }
}