package kim.dataTest.mapper;

import kim.dataTest.domain.OnBidProperty;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.jdbc.Sql;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class OnBidPropertyMapperTest {

    @Autowired
    private OnBidPropertyMapper mapper;

    @Test
    @DisplayName("단건 등록 테스트")
    void insertTest() {
        // given
        OnBidProperty property = createTestProperty();

        // when
        int result = mapper.insert(property);

        // then
        assertThat(result).isEqualTo(1);
        assertThat(property.getId()).isNotNull();
    }

    @Test
    @DisplayName("다건 일괄 등록 테스트")
    void insertBatchTest() {
        // given
        List<OnBidProperty> properties = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            properties.add(createTestProperty("TEST-" + i));
        }

        // when
        int result = mapper.insertBatch(properties);

        // then
        assertThat(result).isEqualTo(5);
    }

    @Test
    @DisplayName("전체 목록 조회 테스트")
    void findAllTest() {
        // given
        mapper.insert(createTestProperty());

        // when
        List<OnBidProperty> properties = mapper.findAll();

        // then
        assertThat(properties).isNotEmpty();
    }

    @Test
    @DisplayName("ID로 단건 조회 테스트")
    void findOnBidPropertyByIdTest() {
        // given
        OnBidProperty property = createTestProperty();
        mapper.insert(property);

        // when
        OnBidProperty found = mapper.findOnBidPropertyById(property.getId());

        // then
        assertThat(found).isNotNull();
        assertThat(found.getCltrMnmtNo()).isEqualTo(property.getCltrMnmtNo());
    }

    @Test
    @DisplayName("물건 관리번호로 조회 테스트")
    void findOnBidPropertyByCltrMnmtNoTest() {
        // given
        OnBidProperty property = createTestProperty();
        mapper.insert(property);

        // when
        OnBidProperty found = mapper.findOnBidPropertyByCltrMnmtNo(property.getCltrMnmtNo());

        // then
        assertThat(found).isNotNull();
        assertThat(found.getCltrNm()).isEqualTo(property.getCltrNm());
    }

    @Test
    @DisplayName("전체 건수 조회 테스트")
    void countTest() {
        // given
        mapper.insert(createTestProperty());
        mapper.insert(createTestProperty("TEST-2"));

        // when
        int count = mapper.count();

        // then
        assertThat(count).isGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("단건 수정 테스트")
    void updateOnBidPropertyTest() {
        // given
        OnBidProperty property = createTestProperty();
        mapper.insert(property);

        property.setCltrNm("수정된 물건명");
        property.setOpenPrice(5000000L);

        // when
        int result = mapper.updateOnBidProperty(property);

        // then
        assertThat(result).isEqualTo(1);
        OnBidProperty updated = mapper.findOnBidPropertyById(property.getId());
        assertThat(updated.getCltrNm()).isEqualTo("수정된 물건명");
        assertThat(updated.getOpenPrice()).isEqualTo(5000000L);
    }

    @Test
    @DisplayName("단건 삭제 테스트")
    void deleteOnBidPropertyByIdTest() {
        // given
        OnBidProperty property = createTestProperty();
        mapper.insert(property);

        // when
        int result = mapper.deleteOnBidPropertyById(property.getId());

        // then
        assertThat(result).isEqualTo(1);
        OnBidProperty deleted = mapper.findOnBidPropertyById(property.getId());
        assertThat(deleted).isNull();
    }

    @Test
    @DisplayName("전체 삭제 테스트")
    void deleteAllTest() {
        // given
        mapper.insert(createTestProperty());
        mapper.insert(createTestProperty("TEST-2"));

        // when
        int result = mapper.deleteAll();

        // then
        assertThat(result).isGreaterThanOrEqualTo(2);
        int count = mapper.count();
        assertThat(count).isEqualTo(0);
    }

    @Test
    @DisplayName("동적 검색 테스트 - 물건명")
    void searchOnBidPropertyByCltrNmTest() {
        // given
        OnBidProperty property = createTestProperty();
        property.setCltrNm("테스트 아파트");
        mapper.insert(property);

        Map<String, Object> params = new HashMap<>();
        params.put("cltrNm", "아파트");

        // when
        List<OnBidProperty> properties = mapper.searchOnBidProperty(params);

        // then
        assertThat(properties).isNotEmpty();
        assertThat(properties.get(0).getCltrNm()).contains("아파트");
    }

    @Test
    @DisplayName("동적 검색 테스트 - 시도")
    void searchOnBidPropertyBySidoTest() {
        // given
        OnBidProperty property = createTestProperty();
        property.setSido("서울");
        mapper.insert(property);

        Map<String, Object> params = new HashMap<>();
        params.put("sido", "서울");

        // when
        List<OnBidProperty> properties = mapper.searchOnBidProperty(params);

        // then
        assertThat(properties).isNotEmpty();
        assertThat(properties.get(0).getSido()).isEqualTo("서울");
    }

    @Test
    @DisplayName("동적 검색 테스트 - 가격 범위")
    void searchOnBidPropertyByPriceRangeTest() {
        // given
        OnBidProperty property = createTestProperty();
        property.setGoodsPrice(10000000L);
        mapper.insert(property);

        Map<String, Object> params = new HashMap<>();
        params.put("goodsPriceFrom", 5000000L);
        params.put("goodsPriceTo", 15000000L);

        // when
        List<OnBidProperty> properties = mapper.searchOnBidProperty(params);

        // then
        assertThat(properties).isNotEmpty();
        assertThat(properties.get(0).getGoodsPrice()).isBetween(5000000L, 15000000L);
    }

    @Test
    @DisplayName("가격순 정렬 조회 테스트 - 오름차순")
    void findAllOrderByPriceAscTest() {
        // given
        OnBidProperty property1 = createTestProperty("TEST-1");
        property1.setGoodsPrice(10000000L);
        mapper.insert(property1);

        OnBidProperty property2 = createTestProperty("TEST-2");
        property2.setGoodsPrice(5000000L);
        mapper.insert(property2);

        // when
        List<OnBidProperty> properties = mapper.findAllOrderByPrice("goods", "ASC");

        // then
        assertThat(properties).isNotEmpty();
        assertThat(properties.get(0).getGoodsPrice())
                .isLessThanOrEqualTo(properties.get(properties.size() - 1).getGoodsPrice());
    }

    @Test
    @DisplayName("페이징 처리된 목록 조회 테스트")
    void findAllWithPagingTest() {
        // given
        for (int i = 1; i <= 15; i++) {
            mapper.insert(createTestProperty("TEST-" + i));
        }

        // when
        List<OnBidProperty> page1 = mapper.findAllWithPaging(0, 10);
        List<OnBidProperty> page2 = mapper.findAllWithPaging(10, 10);

        // then
        assertThat(page1).hasSize(10);
        assertThat(page2).hasSizeLessThanOrEqualTo(10);
    }

    @Test
    @DisplayName("검색 + 페이징 조합 테스트")
    void searchWithPagingTest() {
        // given
        for (int i = 1; i <= 15; i++) {
            OnBidProperty property = createTestProperty("TEST-" + i);
            property.setSido("서울");
            mapper.insert(property);
        }

        Map<String, Object> params = new HashMap<>();
        params.put("sido", "서울");

        // when
        List<OnBidProperty> page1 = mapper.searchWithPaging(params, 0, 10);

        // then
        assertThat(page1).hasSize(10);
        assertThat(page1.get(0).getSido()).isEqualTo("서울");
    }

    @Test
    @DisplayName("검색 결과 건수 테스트")
    void countBySearchTest() {
        // given
        OnBidProperty property1 = createTestProperty("TEST-1");
        property1.setSido("서울");
        mapper.insert(property1);

        OnBidProperty property2 = createTestProperty("TEST-2");
        property2.setSido("서울");
        mapper.insert(property2);

        OnBidProperty property3 = createTestProperty("TEST-3");
        property3.setSido("부산");
        mapper.insert(property3);

        Map<String, Object> params = new HashMap<>();
        params.put("sido", "서울");

        // when
        int count = mapper.countBySearch(params);

        // then
        assertThat(count).isEqualTo(2);
    }

    private OnBidProperty createTestProperty() {
        return createTestProperty("2024-TEST-001");
    }

    private OnBidProperty createTestProperty(String cltrMnmtNo) {
        return OnBidProperty.builder()
                .cltrMnmtNo(cltrMnmtNo)
                .cltrNm("테스트 물건")
                .dpslMtdCd("0001")
                .ctgrHirkId("10000")
                .ctgrHirkIdMid("10100")
                .sido("경기도")
                .sgk("안성시")
                .emd("양성면")
                .goodsPrice(10000000L)
                .openPrice(8000000L)
                .pbctBegnDtm("20240101")
                .pbctClsDtm("20240131")
                .build();
    }
}
