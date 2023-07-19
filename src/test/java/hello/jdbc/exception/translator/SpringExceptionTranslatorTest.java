package hello.jdbc.exception.translator;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Slf4j
public class SpringExceptionTranslatorTest {

    @Autowired
    DataSource dataSource;

    @Test
    void sqlExceptionErrorCode(){
        String sql = "select bad grammar";

        try {
            Connection con = dataSource.getConnection();
            PreparedStatement pstm = con.prepareStatement(sql);
            pstm.executeQuery();
        } catch (SQLException e) {
            assertThat(e.getErrorCode()).isEqualTo(42122);
            int errorCode = e.getErrorCode();
            log.info("errorCode={}", errorCode);
            log.info("error",e);
        }
    }

    @Test
    void exceptionTranslator(){
        String sql = "select bad grammar";

        try{
            Connection con = dataSource.getConnection();
            PreparedStatement pstm = con.prepareStatement(sql);
            pstm.executeQuery();
        }catch (SQLException e){
            SQLErrorCodeSQLExceptionTranslator exTranslator = new SQLErrorCodeSQLExceptionTranslator(dataSource);

            DataAccessException resultEx = exTranslator.translate("select", sql, e);

            log.info("result={}", resultEx);
            log.info("result={}",resultEx.getClass());
        }
    }
}
