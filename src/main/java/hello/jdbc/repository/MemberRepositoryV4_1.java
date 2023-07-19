package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.ex.MyDbException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/**
 * 예외 누수 문제해결
 * SQLException 사라짐
 * 체크 예외를 RuntimeException 으로
 * MemberRepository 인터페이스 사용
 */
@Slf4j
//@Repository
public class MemberRepositoryV4_1 implements MemberRepository{

    private final DataSource dataSource;

    public MemberRepositoryV4_1(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Member save(Member member) {
        String sql = "insert into member(member_id, money) values (?,?)";

        Connection con = null;
        PreparedStatement pstm = null;

        try {
            con = getConnection();
            pstm = con.prepareStatement(sql);
            pstm.setString(1, member.getMemberId());
            pstm.setInt((2), member.getMoney());
            pstm.executeUpdate();
            return member;
        } catch (SQLException e) {
            throw new MyDbException(e);
        }
        finally {
            close(con, pstm, null);
        }
    }

    public Member findById(String memberId){
        String sql = "select * from member where member_id = ?";

        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;


        try {
            con = getConnection();
            pstm = con.prepareStatement(sql);

            pstm.setString(1, memberId);

            rs = pstm.executeQuery();

            if (rs.next()) {
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            }
            else{
                throw new NoSuchElementException("member not found " + memberId);
            }


        } catch (SQLException e) {
            log.error("db error", e);
            throw new MyDbException(e);
        }
        finally {
            close(con, pstm, rs);
        }
    }


    public void update(String memberId, int money){

        String sql = "update member set money = ? where member_id = ?";

        Connection con = null;
        PreparedStatement pstm = null;


        try {
            con = getConnection();
            pstm = con.prepareStatement(sql);
            pstm.setInt(1, money);
            pstm.setString(2, memberId);

            int resultSize = pstm.executeUpdate();
            log.info("size={}", resultSize);
        } catch (SQLException e) {
            log.error("db error", e);
            throw new MyDbException(e);
        }finally {
            close(con, pstm, null);
        }


    }


    public void delete(String memberId){
        String sql = "delete from member where member_id = ?";

        Connection con = null;
        PreparedStatement pstm = null;


        try {
            con = getConnection();
            pstm = con.prepareStatement(sql);

            pstm.setString(1, memberId);
            int resultSize = pstm.executeUpdate();
            log.info("size={}", resultSize);

        } catch (SQLException e) {
            log.error("db error", e);
            throw new MyDbException(e);
        }
        finally {
            close(con, pstm, null);
        }
    }

    private void close(Connection con, Statement stmt, ResultSet rs){
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        DataSourceUtils.releaseConnection(con, dataSource);
    }

    private Connection getConnection() throws SQLException {
        Connection con = DataSourceUtils.getConnection(dataSource);
        log.info("connection={}, class={}", con, con.getClass());
        return con;
    }
}
