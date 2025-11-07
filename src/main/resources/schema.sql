-- OnBidProperty 테이블 생성
CREATE TABLE IF NOT EXISTS onbidproperty (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cltr_mnmt_no VARCHAR(50) NOT NULL UNIQUE,
    cltr_nm VARCHAR(200) NOT NULL,
    dpsl_mtd_cd VARCHAR(10) NOT NULL,
    ctgr_hirk_id VARCHAR(20),
    ctgr_hirk_id_mid VARCHAR(20),
    sido VARCHAR(20),
    sgk VARCHAR(20),
    emd VARCHAR(20),
    goods_price BIGINT,
    open_price BIGINT,
    pbct_begn_dtm VARCHAR(20),
    pbct_cls_dtm VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);