package kim.dataTest.service;

import kim.dataTest.domain.OnBidProperty;
import kim.dataTest.dto.*;
import kim.dataTest.mapper.OnBidPropertyMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class OnBidPropertyServiceTest {

    @Mock
    private OnBidPropertyMapper mapper;

    @Mock
    private KamcoApiService kamcoApiService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private OnBidPropertyService service;

    private OnBidProperty testProperty;
    private OnBidPropertyCreateDto createDto;
    private OnBidPropertyDetailDto detailDto;

    @BeforeEach
    void setUp() {
        testProperty = OnBidProperty.builder()
                .id(1L)
                .cltrMnmtNo("2024-TEST-001")
                .cltrNm("테스트 물건")
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

        createDto = OnBidPropertyCreateDto.builder()
                .cltrMnmtNo("2024-TEST-001")
                .cltrNm("테스트 물건")
                .dpslMtdCd("0001")
                .sido("서울")
                .goodsPrice(10000000L)
                .openPrice(8000000L)
                .build();

        detailDto = OnBidPropertyDetailDto.builder()
                .id(1L)
                .cltrMnmtNo("2024-TEST-001")
                .cltrNm("테스트 물건")
                .build();
    }

    @Test
    @DisplayName("단건 등록 성공")
    void createTest() {
        // given
        given(modelMapper.map(createDto, OnBidProperty.class)).willReturn(testProperty);
        given(mapper.insert(any(OnBidProperty.class))).willReturn(1);

        // when
        Long id = service.create(createDto);

        // then
        assertThat(id).isEqualTo(1L);
        verify(modelMapper).map(createDto, OnBidProperty.class);
        verify(mapper).insert(any(OnBidProperty.class));
    }

    @Test
    @DisplayName("다건 일괄 등록 성공")
    void createBatchTest() {
        // given
        List<OnBidPropertyCreateDto> dtoList = Arrays.asList(createDto, createDto);
        List<OnBidProperty> entities = Arrays.asList(testProperty, testProperty);

        given(modelMapper.map(any(OnBidPropertyCreateDto.class), eq(OnBidProperty.class)))
                .willReturn(testProperty);
        given(mapper.insertBatch(anyList())).willReturn(2);

        // when
        int count = service.createBatch(dtoList);

        // then
        assertThat(count).isEqualTo(2);
        verify(mapper).insertBatch(anyList());
    }

    @Test
    @DisplayName("전체 목록 조회 성공")
    void findAllTest() {
        // given
        List<OnBidProperty> properties = Arrays.asList(testProperty, testProperty);
        given(mapper.findAll()).willReturn(properties);
        given(modelMapper.map(any(OnBidProperty.class), eq(OnBidPropertyListDto.class)))
                .willReturn(new OnBidPropertyListDto());

        // when
        List<OnBidPropertyListDto> result = service.findAll();

        // then
        assertThat(result).hasSize(2);
        verify(mapper).findAll();
    }

    @Test
    @DisplayName("ID로 단건 조회 성공")
    void findByIdTest() {
        // given
        given(mapper.findOnBidPropertyById(1L)).willReturn(testProperty);
        given(modelMapper.map(testProperty, OnBidPropertyDetailDto.class)).willReturn(detailDto);

        // when
        OnBidPropertyDetailDto result = service.findById(1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(mapper).findOnBidPropertyById(1L);
    }

    @Test
    @DisplayName("ID로 단건 조회 실패 - 존재하지 않는 ID")
    void findByIdNotFoundTest() {
        // given
        given(mapper.findOnBidPropertyById(999L)).willReturn(null);

        // when & then
        assertThatThrownBy(() -> service.findById(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재 하지 않는 물건입니다");
    }

    @Test
    @DisplayName("물건 관리번호로 조회 성공")
    void findByCltrMnmtNoTest() {
        // given
        given(mapper.findOnBidPropertyByCltrMnmtNo("2024-TEST-001")).willReturn(testProperty);
        given(modelMapper.map(testProperty, OnBidPropertyDetailDto.class)).willReturn(detailDto);

        // when
        OnBidPropertyDetailDto result = service.findByCltrMnmtNo("2024-TEST-001");

        // then
        assertThat(result).isNotNull();
        verify(mapper).findOnBidPropertyByCltrMnmtNo("2024-TEST-001");
    }

    @Test
    @DisplayName("전체 건수 조회 성공")
    void countAllTest() {
        // given
        given(mapper.count()).willReturn(10);

        // when
        int count = service.countAll();

        // then
        assertThat(count).isEqualTo(10);
        verify(mapper).count();
    }

    @Test
    @DisplayName("수정 성공")
    void updateTest() {
        // given
        given(mapper.findOnBidPropertyById(1L)).willReturn(testProperty);
        given(modelMapper.map(detailDto, OnBidProperty.class)).willReturn(testProperty);
        given(mapper.updateOnBidProperty(any(OnBidProperty.class))).willReturn(1);

        // when
        service.update(1L, detailDto);

        // then
        verify(mapper).findOnBidPropertyById(1L);
        verify(mapper).updateOnBidProperty(any(OnBidProperty.class));
    }

    @Test
    @DisplayName("수정 실패 - 존재하지 않는 ID")
    void updateNotFoundTest() {
        // given
        given(mapper.findOnBidPropertyById(999L)).willReturn(null);

        // when & then
        assertThatThrownBy(() -> service.update(999L, detailDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재 하지 않는 물건입니다");
    }

    @Test
    @DisplayName("전체 삭제 성공")
    void deleteAllTest() {
        // given
        given(mapper.deleteAll()).willReturn(5);

        // when
        int count = service.deleteAll();

        // then
        assertThat(count).isEqualTo(5);
        verify(mapper).deleteAll();
    }

    @Test
    @DisplayName("단건 삭제 성공")
    void deleteByIdTest() {
        // given
        given(mapper.findOnBidPropertyById(1L)).willReturn(testProperty);
        given(mapper.deleteOnBidPropertyById(1L)).willReturn(1);

        // when
        service.deleteById(1L);

        // then
        verify(mapper).findOnBidPropertyById(1L);
        verify(mapper).deleteOnBidPropertyById(1L);
    }

    @Test
    @DisplayName("단건 삭제 실패 - 존재하지 않는 ID")
    void deleteByIdNotFoundTest() {
        // given
        given(mapper.findOnBidPropertyById(999L)).willReturn(null);

        // when & then
        assertThatThrownBy(() -> service.deleteById(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재 하지 않는 물건입니다");
    }

    @Test
    @DisplayName("동적 검색 성공")
    void searchTest() {
        // given
        OnBidPropertySearchDto searchDto = OnBidPropertySearchDto.builder()
                .sido("서울")
                .build();

        List<OnBidProperty> properties = Arrays.asList(testProperty);
        given(mapper.searchOnBidProperty(anyMap())).willReturn(properties);
        given(modelMapper.map(any(OnBidProperty.class), eq(OnBidPropertyListDto.class)))
                .willReturn(new OnBidPropertyListDto());

        // when
        List<OnBidPropertyListDto> result = service.search(searchDto);

        // then
        assertThat(result).hasSize(1);
        verify(mapper).searchOnBidProperty(anyMap());
    }

    @Test
    @DisplayName("검색 결과 건수 조회")
    void countSearchResultsTest() {
        // given
        OnBidPropertySearchDto searchDto = OnBidPropertySearchDto.builder()
                .sido("서울")
                .build();

        given(mapper.countBySearch(anyMap())).willReturn(5);

        // when
        int count = service.countSearchResults(searchDto);

        // then
        assertThat(count).isEqualTo(5);
        verify(mapper).countBySearch(anyMap());
    }

    @Test
    @DisplayName("가격순 정렬 조회")
    void findAllOrderByPriceTest() {
        // given
        List<OnBidProperty> properties = Arrays.asList(testProperty);
        given(mapper.findAllOrderByPrice("goods", "ASC")).willReturn(properties);
        given(modelMapper.map(any(OnBidProperty.class), eq(OnBidPropertyListDto.class)))
                .willReturn(new OnBidPropertyListDto());

        // when
        List<OnBidPropertyListDto> result = service.findAllOrderByPrice("goods", "ASC");

        // then
        assertThat(result).hasSize(1);
        verify(mapper).findAllOrderByPrice("goods", "ASC");
    }

    @Test
    @DisplayName("페이징 처리된 목록 조회")
    void findAllWithPagingTest() {
        // given
        List<OnBidProperty> properties = Arrays.asList(testProperty);
        given(mapper.findAllWithPaging(0, 10)).willReturn(properties);
        given(modelMapper.map(any(OnBidProperty.class), eq(OnBidPropertyListDto.class)))
                .willReturn(new OnBidPropertyListDto());

        // when
        List<OnBidPropertyListDto> result = service.findAllWithPaging(0, 10);

        // then
        assertThat(result).hasSize(1);
        verify(mapper).findAllWithPaging(0, 10);
    }

    @Test
    @DisplayName("검색 + 페이징 조합")
    void searchWithPagingTest() {
        // given
        OnBidPropertySearchDto searchDto = OnBidPropertySearchDto.builder()
                .sido("서울")
                .build();

        List<OnBidProperty> properties = Arrays.asList(testProperty);
        given(mapper.searchWithPaging(anyMap(), eq(0), eq(10))).willReturn(properties);
        given(modelMapper.map(any(OnBidProperty.class), eq(OnBidPropertyListDto.class)))
                .willReturn(new OnBidPropertyListDto());

        // when
        List<OnBidPropertyListDto> result = service.searchWithPaging(searchDto, 0, 10);

        // then
        assertThat(result).hasSize(1);
        verify(mapper).searchWithPaging(anyMap(), eq(0), eq(10));
    }
}
