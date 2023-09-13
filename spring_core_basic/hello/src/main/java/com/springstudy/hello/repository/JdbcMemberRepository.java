package com.springstudy.hello.repository;

import com.springstudy.hello.domain.Member;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
public class JdbcMemberRepository implements MemberRepository {
    private final DataSource dataSource;

    public JdbcMemberRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Member save(Member member) {
        String sql = "insert into member(name) values(?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null; //결과 받는 것
        try {
            conn = getConnection(); //connection 가져옴
            pstmt = conn.prepareStatement(sql,
                    Statement.RETURN_GENERATED_KEYS); //sql 넣음 + RETURN_GENERATED_KEYS로 키 자동 생성
            pstmt.setString(1, member.getName()); //sql의 ?에 들어갈 값
            pstmt.executeUpdate(); //실제 쿼리 보냄
            rs = pstmt.getGeneratedKeys(); //RETURN_GENERATED_KEYS가 있으므로 DB에서 key 반환 받음
            if (rs.next()) { //rs가 값을 받아와서 next()에 값이 있으면
                member.setId(rs.getLong(1)); //ID값 set
            } else {
                throw new SQLException("id 조회 실패");
            }
            return member;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            close(conn, pstmt, rs); //DB connection은 쓰고나면 바로 날려줘야 한다. (아니면 쌓여서 날리남...)
        }
    }

    @Override
    public Optional<Member> findById(Long id) {
        String sql = "select * from member where id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, id);
            rs = pstmt.executeQuery(); //조회는 executeQuery(), 생성은 executeUpdate()
            if (rs.next()) {
                Member member = new Member();
                member.setId(rs.getLong("id"));
                member.setName(rs.getString("name"));
                return Optional.of(member); //값 받아오면 객체에 담아서 넘김.
            } else {
                return Optional.empty(); //없으면 empty()
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }

    @Override
    public List<Member> findAll() {
        String sql = "select * from member";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            List<Member> members = new ArrayList<>();
            while (rs.next()) { //전체 데이터라 list로 받음.
                Member member = new Member();
                member.setId(rs.getLong("id"));
                member.setName(rs.getString("name"));
                members.add(member);
            }
            return members;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }

    @Override
    public Optional<Member> findByName(String name) {
        String sql = "select * from member where name = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                Member member = new Member();
                member.setId(rs.getLong("id"));
                member.setName(rs.getString("name"));
                return Optional.of(member);
            }
            return Optional.empty();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }

    private Connection getConnection() {
        return DataSourceUtils.getConnection(dataSource);
        //dataSource를 통해서도 connection을 얻을 수 있다 (근데, 그때는 계속 하나씩 만듬)
        //spring frameworksms datasourceutils를 지원해서 이걸로 connection을 생성한다.
        // (이걸 사용해야 DB transjection 발생시 DB 연결을 유지해준다)
    }

    private void close(Connection conn, PreparedStatement pstmt, ResultSet rs) {
        try { //close는 역순으로 진행된다 정도만 알고 넘아가라...
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (pstmt != null) {
                pstmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (conn != null) {
                close(conn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void close(Connection conn) throws SQLException {
        DataSourceUtils.releaseConnection(conn, dataSource);
        //닫을 때도 connection은 datasourceutiles를 통해 닫아야 한다.
    }
}