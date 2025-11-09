show databases ;


create database if not exists onBid_DB
default character set utf8mb4
collate utf8mb4_unicode_ci;

use onBid_DB;

drop  table if exists onBidProperty;

-- 테이블 재생성
create table onBidProperty(
                              id BIGINT auto_increment primary key comment '고유 ID',
                              cltr_mnmt_no varchar(100) unique comment '물건 관리번호',
                              cltr_nm varchar(200) comment '물건명',
                              dpsl_mtd_cd varchar(10) comment '처분방법 코드 (0001: 매각, 0002: 임대)',
                              ctgr_hirk_id varchar(20) comment '카테고리 상위 ID',
                              ctgr_hirk_id_mid varchar(20) comment '카테고리 중위 ID',
                              sido varchar(50) comment '시도',
                              sgk varchar(50) comment '시군구',
                              emd varchar(50) comment '읍면동',
                              goods_price BIGINT comment '감정가',
                              open_price BIGINT comment '최저 입찰가',
                              pbct_begn_dtm varchar(20) comment '공고 시작일(YYYYMMDDHHmmss)',  -- ⭐ 변경
                              pbct_cls_dtm VARCHAR(20) COMMENT '공고 종료일(YYYYMMDDHHmmss)',    -- ⭐ 변경
                              created_at timestamp default current_timestamp comment '등록 일시',
                              updated_at timestamp default current_timestamp on update current_timestamp comment '수정일시',
                              INDEX idx_sido (sido),
                              INDEX idx_dpsl_mtd_cd(dpsl_mtd_cd),
                              INDEX idx_pbct_begn_dtm(pbct_begn_dtm)
) ENGINE=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci comment='온비드 매물정보';

show tables;

select * from onBidProperty;


