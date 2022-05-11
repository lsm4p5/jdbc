package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션 - 파라메터 연동, 풀을 고려한 종료
 */
@RequiredArgsConstructor
@Slf4j
public class MemberServiceV2 {
    private final DataSource dataSource;
    private final MemberRepositoryV2 memberRepositoryV2;


    public void accountTransfer(String fromId, String toId, int money) throws SQLException {

        Connection con = dataSource.getConnection();

        try {
            con.setAutoCommit( false ); // 트랜잭션 시작
            //비지니스로직
            bizLogic( con, toId, money, fromId );
            con.commit();
        } catch (Exception e) {
            con.rollback();
            throw new IllegalStateException(e);
        }finally {
            release( con );
        }
    }

    private void bizLogic(Connection con, String toId, int money, String fromId) throws SQLException {
        Member fromMember = memberRepositoryV2.findById( con, fromId );
        Member toMember = memberRepositoryV2.findById( con, toId );

        memberRepositoryV2.update( con, fromId, fromMember.getMoney() - money );
        validation( toMember );
        memberRepositoryV2.update( con, toId, toMember.getMoney() + money );
    }

    private void release(Connection con) {
        if(con !=null){
            try{
                con.setAutoCommit( true );
                con.close();

            }catch(Exception e){
                log.info("error", e);
            }
        }
    }

    private void validation(Member toMember) {
        if(toMember.getMemberId().equals("ex")){
            throw new IllegalStateException("이체중 예외발생");
        }
    }
}
