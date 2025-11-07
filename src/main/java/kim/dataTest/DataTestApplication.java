package kim.dataTest;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("kim.dataTest.mapper")
public class DataTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataTestApplication.class, args);
    }

}
