package kim.dataTest.service;


import kim.dataTest.dto.KamkoApiResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class KamcoApiService {

    @Value("${kamco.api.service-key}")
    private String serviceKey;


//    기본검색 - 시도 별 조회

    public String fetchPropertiesBySido(String sido, int numOfRows, int pageNo) throws IOException {

            StringBuilder urlBuilder = new StringBuilder(
                    "http://openapi.onbid.co.kr/openapi/services/KamcoPblsalThingInquireSvc/getKamcoPbctCltrList");
// 요청 서비스키

            urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + serviceKey);
//            사용 되는 필드들
            urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" +
                    URLEncoder.encode(String.valueOf(numOfRows), "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" +
                    URLEncoder.encode(String.valueOf(pageNo), "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("DPSL_MTD_CD", "UTF-8") + "=" +
                    URLEncoder.encode("0001", "UTF-8"));
//            0001 매각, 0002 임대
            urlBuilder.append("&" + URLEncoder.encode("CTGR_HIRK_ID", "UTF-8") + "=" +
                    URLEncoder.encode("10000", "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("CTGR_HIRK_ID_MID", "UTF-8") + "=" +
                    URLEncoder.encode("10100", "UTF-8"));
//            선택적 파라미터-시도
            if (sido != null && !sido.isEmpty()) {
                urlBuilder.append("&" + URLEncoder.encode("SIDO", "UTF-8") + "=" +

                        URLEncoder.encode(sido, "UTF-8"));

            }

            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/xml");

            log.info("API요청 URL : {}", urlBuilder.toString());
            log.info("API요청 응답코드 : {}", conn.getResponseCode());
            BufferedReader rd;
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader((new InputStreamReader(conn.getInputStream())));
            } else {
                rd = new BufferedReader((new InputStreamReader(conn.getErrorStream())));
            }
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);

            }
            rd.close();
            conn.disconnect();
            String response = sb.toString();
            log.info("API 응답 : {}", response);

            return response;
        }
//상세 조건 검색

    public String fetchPropertiesWithConditions(
            String sido,
            String sgk,
            String emd,
            String goodsPriceForm,
            String goodsPriceTo,
            String openPriceForm,
            String openPriceTo,
            int numOfRows,
            String cltrNm,
            String pbctBegmDtm,
            String pbctClsDtm,
            int pageNo
    ) throws IOException{
//        요청 서비스키
        StringBuilder urlBuilder = new StringBuilder(

                "http://openapi.onbid.co.kr/openapi/services/KamcoPblsalThingInquireSvc/getKamcoPbctCltrList");
//        필수 파라미터
        urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + serviceKey);
        urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" +
                URLEncoder.encode(String.valueOf(numOfRows), "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" +
                URLEncoder.encode(String.valueOf(pageNo), "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("DPSL_MTD_CD", "UTF-8") + "=" +
                URLEncoder.encode("0001", "UTF-8"));
//            0001 매각, 0002 임대
        urlBuilder.append("&" + URLEncoder.encode("CTGR_HIRK_ID", "UTF-8") + "=" +
                URLEncoder.encode("10000", "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("CTGR_HIRK_ID_MID", "UTF-8") + "=" +
                URLEncoder.encode("10100", "UTF-8"));
//        선택적 파라미터들

        if (sido != null && !sido.isEmpty()) {
            urlBuilder.append("&" + URLEncoder.encode("SIDO", "UTF-8") + "=" +

                    URLEncoder.encode(sido, "UTF-8"));

        }
        if (sgk  != null && !sgk.isEmpty()) {
            urlBuilder.append("&" + URLEncoder.encode("SGK", "UTF-8") + "=" +

                    URLEncoder.encode(sgk, "UTF-8"));

        }
        if (emd != null && !emd.isEmpty()) {
            urlBuilder.append("&" + URLEncoder.encode("EMD", "UTF-8") + "=" +

                    URLEncoder.encode(emd, "UTF-8"));

        }
        if (goodsPriceForm != null && !goodsPriceForm.isEmpty()) {
            urlBuilder.append("&" + URLEncoder.encode("GOODS_PRICE_FROM", "UTF-8") + "=" +

                    URLEncoder.encode(goodsPriceForm, "UTF-8"));

        }
        if (goodsPriceTo != null && !goodsPriceTo.isEmpty()) {
            urlBuilder.append("&" + URLEncoder.encode("GOODS_PRICE_TO", "UTF-8") + "=" +

                    URLEncoder.encode(goodsPriceTo, "UTF-8"));

        }
        if (openPriceTo != null && !openPriceTo.isEmpty()) {
            urlBuilder.append("&" + URLEncoder.encode("OPEN_PRICE_TO", "UTF-8") + "=" +

                    URLEncoder.encode(openPriceTo, "UTF-8"));

        }
        if (openPriceForm != null && !openPriceForm.isEmpty()) {
            urlBuilder.append("&" + URLEncoder.encode("OPEN_PRICE_FROM", "UTF-8") + "=" +

                    URLEncoder.encode(openPriceForm, "UTF-8"));

        }
        if (cltrNm != null && !cltrNm.isEmpty()) {
            urlBuilder.append("&" + URLEncoder.encode("CLTR_NM", "UTF-8") + "=" +

                    URLEncoder.encode(cltrNm, "UTF-8"));

        }
        if (pbctBegmDtm != null && !pbctBegmDtm.isEmpty()) {
            urlBuilder.append("&" + URLEncoder.encode("PBCT_BEGN_DTM", "UTF-8") + "=" +

                    URLEncoder.encode(pbctBegmDtm, "UTF-8"));

        }
        if (pbctClsDtm != null && !pbctClsDtm.isEmpty()) {
            urlBuilder.append("&" + URLEncoder.encode("PBCT_CLS_DTM", "UTF-8") + "=" +

                    URLEncoder.encode(pbctClsDtm, "UTF-8"));

        }
        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/xml");

        log.info("API요청 URL : {}", urlBuilder.toString());
        log.info("API요청 응답코드 : {}", conn.getResponseCode());
        BufferedReader rd;
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader((new InputStreamReader(conn.getInputStream())));
        } else {
            rd = new BufferedReader((new InputStreamReader(conn.getErrorStream())));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);

        }
        rd.close();
        conn.disconnect();
        String response = sb.toString();
        log.info("API 응답 : {}", response);

        return response;

    }

//    XML 응답 파싱 (실제 구현 필요)
//     * TODO: 온비드 API의 실제 XML 구조에 맞게 파싱 로직 구현


    public List<KamkoApiResponseDto> parseXmlResponse(String xmlResponse){
        List<KamkoApiResponseDto> responseList = new ArrayList<>();

        log.warn("XML 파싱 로직 미구현 - 실제 API 응답 구조 확인 후 구현 필요");
        return responseList;


    }


    }





