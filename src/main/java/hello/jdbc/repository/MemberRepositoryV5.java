package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * SQLExceptionTranslator 추가
 */
@Slf4j
public class MemberRepositoryV5 implements MemberRepository{

    private final JdbcTemplate jdbcTemplate;
    private final SQLExceptionTranslator exTranslator;

    public MemberRepositoryV5(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate( dataSource );
        this.exTranslator = new SQLErrorCodeSQLExceptionTranslator( dataSource );
    }

    @Override
    public Member save(Member member) {

        String sql = "insert into member(member_Id,money) values(?,?)";
        jdbcTemplate.update( sql, member.getMemberId(), member.getMoney() );
        return member;
    }

    @Override
    public Member findById(String memberId)  {
        String sql = "select * from member where member_id=?";
        Member member = jdbcTemplate.queryForObject( sql, memberRowMapper(), memberId );
        return member;
    }

    public List<Member> findAll() {
        String sql = "select * from member";
        List<Member> query_result = jdbcTemplate.query( sql,
                (rs, rownum) -> {
                    Member member = new Member();
                    member.setMemberId( rs.getString( "member_id" ) );
                    member.setMoney( rs.getInt( "money" ) );
                    return member;
                } );
        for (Member member : query_result) {
            System.out.println( "member = " + member );
        }
        return query_result;
    }

    public List<Member> findByMoney(int money) {

        String sql = "select * from member where money>=?";
        List<Member> query_result = jdbcTemplate.query( sql,
                (rs, rownum) -> {
                    Member member = new Member();
                    member.setMemberId( rs.getString( "member_id" ) );
                    member.setMoney( rs.getInt( "money" ) );
                    return member;
                }, money );


        for (Member member : query_result) {
            System.out.println( "member = " + member );
        }
        return query_result;
    }
    private RowMapper<Member> memberRowMapper() {
        return (rs,rowNum)->{
            Member member = new Member();
            member.setMemberId( rs.getString( "member_id" ) );
            member.setMoney( rs.getInt( "money" ) );
            return member;
        };
    }

    @Override
    public void update(String memberId, int money) {
        String sql = "update member set money=? where member_id=?";
        int update = jdbcTemplate.update( sql, money,memberId );
        log.info( "update count{}", update );
    }

    @Override
    public void delete(String memberId)  {
        String sql = "delete from member where member_id=?";
        jdbcTemplate.update( sql, memberId );
    }


}
