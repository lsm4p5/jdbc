package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class UncheckedTest {

    @Test
    void unchecked_catch() {
        Service service = new Service();
        service.callCatch();
    }

    @Test
    void unchecked_throw() {
        Service service = new Service();
        Assertions.assertThatThrownBy( () -> service.call_throw() ).isInstanceOf( MyUncheckedExcption.class );
    }

    /**
     * RunException(NullPointExcepion, IllegalArgumentException)을 상속받은 예외는 언체크 예외가 된다.
     */
    static class MyUncheckedExcption extends RuntimeException {
        public MyUncheckedExcption(String message) {
            super( message );
        }
    }

    /**
     * Unchecked 예외는
     * 예외를 잡거나, 던지지 않아도 된다
     * 예외를 잡지 않으면 자동으로 밖으로 던진다.
     */
    static class Service{
        Repository repository = new Repository();

        /**
         * 필요한 경우 예외를 잡아서 처리하면 된다.
         */
        public void callCatch() {
            try {
                repository.call();
            } catch (MyUncheckedExcption e) {
                //예외처리 로직
                log.info("예외처리, message={} ", e.getMessage(),e);
            }
        }

        /**
         * 언체크 예외를 밖으로 던지는 코드
         * 언체크 예외를 예외를 잡지 않고 밖으로 던지어도 throws 예외를 메서드에 필수로 선언하지 않아도 예외를 던진다.
         * * @throws MyUncheckedExcption
         */
        public void call_throw(){
            repository.call();
        }
    }

    static class Repository{
        public void call() {
            throw new MyUncheckedExcption( "ex" );
        }
    }
}
