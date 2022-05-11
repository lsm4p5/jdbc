package hello.jdbc.service;


import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import hello.jdbc.repository.MemberRepositoryV3;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;


/**
 *  트랜잭션 - 커넥션 파라메타 전달 방식 동기화
 */
class MemberServiceV3_1Test {

    public static final String MEMBER_A = "memberA";
    public static final String MEMBER_B = "memberB";
    public static final String MEMBER_EX = "ex";

    private MemberRepositoryV3 memberRepository;
    private MemberServiceV3_1 memberService;

    @BeforeEach
    void before(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource( URL, USERNAME, PASSWORD );
        memberRepository= new MemberRepositoryV3( dataSource );
        PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
        memberService = new MemberServiceV3_1(transactionManager, memberRepository );
    }

    @AfterEach
    void after() throws SQLException {
        memberRepository.delete( MEMBER_A );
        memberRepository.delete( MEMBER_B );
        memberRepository.delete( MEMBER_EX );
    }

    @Test
    @DisplayName("정상이체")
    void accountTransfer() throws SQLException {
        //given
        Member memberA = new Member( MEMBER_A, 10000 );
        Member memberB = new Member( MEMBER_B, 10000 );
        memberRepository.save( memberA );
        memberRepository.save( memberB );

        //when
        memberService.accountTransfer( memberA.getMemberId(), memberB.getMemberId(), 2000 );
        //then
        Member findMemberA = memberRepository.findById( memberA.getMemberId() );
        Member findMemberB = memberRepository.findById( memberB.getMemberId() );
        assertThat( findMemberA.getMoney() ).isEqualTo( 8000 );
        assertThat( findMemberB.getMoney() ).isEqualTo( 12000 );
    }

    @Test
    @DisplayName("이체중 예외 발생")
    void accountTransfer_ex() throws SQLException {
        //given
        Member memberA = new Member( MEMBER_A, 10000 );
        Member memberEX = new Member( MEMBER_EX, 10000 );
        memberRepository.save( memberA );
        memberRepository.save( memberEX );

        //when
       // memberServiceV1.accountTransfer( memberA.getMemberId(), memberEX.getMemberId(), 2000 );

        Assertions.assertThatThrownBy(()-> memberService.accountTransfer( memberA.getMemberId(), memberEX.getMemberId(), 2000 ))
                .isInstanceOf( IllegalStateException.class );
        //then
        Member findMemberA = memberRepository.findById( memberA.getMemberId() );
        Member findMemberEX = memberRepository.findById( memberEX.getMemberId() );
        assertThat( findMemberA.getMoney() ).isEqualTo( 10000 );
        assertThat( findMemberEX.getMoney() ).isEqualTo( 10000 );
    }
}