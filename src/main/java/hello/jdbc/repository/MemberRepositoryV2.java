package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/**
 * JDBC- ConnectionParam 사용
 */
@Slf4j
public class MemberRepositoryV2 {

    private final DataSource dataSource;

    public MemberRepositoryV2(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Member save(Member member) throws SQLException {
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
            log.error("db error", e);
            throw e;
        }
        finally {
            close(con, pstm, null);
        }
    }

    public Member findById(String memberId) throws SQLException {
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
            throw e;
        }
        finally {
            close(con, pstm, rs);
        }
    }
    public Member findById(Connection con, String memberId) throws SQLException {
        String sql = "select * from member where member_id = ?";


        PreparedStatement pstm = null;
        ResultSet rs = null;


        try {

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
            throw e;
        }
        finally {
            // connection 을 여기서 닫지 않습니다.
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(pstm);
            //JdbcUtils.closeConnection(con);
        }
    }

    public void update(String memberId, int money) throws SQLException {

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
            throw e;
        }finally {
            close(con, pstm, null);
        }


    }
    public void update(Connection con, String memberId, int money) throws SQLException {

        String sql = "update member set money = ? where member_id = ?";


        PreparedStatement pstm = null;


        try {

            pstm = con.prepareStatement(sql);
            pstm.setInt(1, money);
            pstm.setString(2, memberId);

            int resultSize = pstm.executeUpdate();
            log.info("size={}", resultSize);
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        }finally {
            // connection 을 닫지 않습니다.
            //JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(pstm);
            //JdbcUtils.closeConnection(con);
        }


    }

    public void delete(String memberId) throws SQLException {
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
            throw e;
        }
        finally {
            close(con, pstm, null);
        }
    }

    private void close(Connection con, Statement stmt, ResultSet rs){
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        JdbcUtils.closeConnection(con);
    }

    private Connection getConnection() throws SQLException {
        Connection con = dataSource.getConnection();
        log.info("connection={}, class={}", con, con.getClass());
        return con;
    }
}
