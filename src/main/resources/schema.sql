

use onbid_db;
DROP TABLE IF EXISTS onbidproperty;

CREATE TABLE IF NOT EXISTS onbidproperty (
   id BIGINT AUTO_INCREMENT PRIMARY KEY,
   pbct_no VARCHAR(50) NOT NULL UNIQUE,  -- ⭐ 추가: 공고번호를 고유키로
   cltr_mnmt_no VARCHAR(50) NOT NULL ,
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
   updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   INDEX idx_sido (sido),
   INDEX idx_dpsl_mtd_cd (dpsl_mtd_cd),
   INDEX idx_pbct_begn_dtm (pbct_begn_dtm)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;