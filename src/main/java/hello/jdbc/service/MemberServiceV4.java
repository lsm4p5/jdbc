package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepository;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

/**
 * 예외 누수 문제 해결
 * SQLException 제거
 *
 * MeberRepository 인터페이스에 의존
 */
@Slf4j
public class MemberServiceV4 {

    private final MemberRepository memberRepository;

    public MemberServiceV4(MemberRepository memberRepository) {

        this.memberRepository = memberRepository;
    }


    @Transactional
    public void accountTransfer(String fromId, String toId, int money) {

        //비지니스로직
        bizLogic(toId, money, fromId );

    }

    private void bizLogic( String toId, int money, String fromId) {
        Member fromMember = memberRepository.findById(  fromId );
        Member toMember = memberRepository.findById(  toId );

        memberRepository.update(  fromId, fromMember.getMoney() - money );
        validation( toMember );
        memberRepository.update(  toId, toMember.getMoney() + money );
    }



    private void validation(Member toMember) {
        if(toMember.getMemberId().equals("ex")){
            throw new IllegalStateException("이체중 예외발생");
        }
    }
}
