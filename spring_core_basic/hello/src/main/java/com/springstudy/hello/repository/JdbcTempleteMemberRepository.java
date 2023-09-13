package com.springstudy.hello.repository;

import com.springstudy.hello.domain.Member;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class JdbcTempleteMemberRepository implements MemberRepository{

    private final JdbcTemplate jdbcTemplate;
    //생성자가 1개일 때 spring bean으로 등록되는 객체에서 @Autowired는 생략 가능
    //jdbcTemplate는 Spring bean으로 자동 등록되지 않기 때문에 DataSource를 가져와서 Jdbc에 연결.
    public JdbcTempleteMemberRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Member save(Member member) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName("member").usingGeneratedKeyColumns("id");
        //JDBC template가 제공하는 간단하게 테이블을 생성하는 코드이다 (테이블 이름과 id를 key로 사용하도록 설정)

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", member.getName());
        //추가로 테이블에 name col를 만들기 위해 설정

        Number key = jdbcInsert.executeAndReturnKey(new
                MapSqlParameterSource(parameters));
        //코드를 실행하고, (즉, DB에 실제로 테이블을 생성하고, 키값을 return 받음.)
        member.setId(key.longValue()); //리턴된 키 값 할당...
        return member;
    }

    @Override
    public Optional<Member> findById(Long id) {
        List<Member> results = jdbcTemplate.query("select * from member where id = ?", memberRowMapper(), id);
        return results.stream().findAny();
    }

    @Override
    public Optional<Member> findByName(String name) {
        List<Member> results = jdbcTemplate.query("select * from member where name = ?", memberRowMapper(), name);
        return results.stream().findAny();
    }

    @Override
    public List<Member> findAll() {
        return jdbcTemplate.query("select * from member", memberRowMapper());
    }                                                       //memberRowMapper()를 callback 해서 거기서 객체 생성. (mapping)

    private RowMapper<Member> memberRowMapper(){
        /*//기본 스타일
        return new RowMapper<Member>() {
            @Override
            public Member mapRow(ResultSet rs, int rowNum) throws SQLException {
                //ResultSet : JDBC에서 값을 받아오던 객체 (DB에서 결과값을 저장)
                Member member = new Member();
                member.setId(rs.getLong("id"));
                member.setName(rs.getString("name"));
                return member;
            };
        }*/
        //람다 스타일
        return (rs, rowNum) -> {
            Member member = new Member();
            member.setId(rs.getLong("id"));
            member.setName(rs.getString("name"));
            return member;
        };
    }
}
