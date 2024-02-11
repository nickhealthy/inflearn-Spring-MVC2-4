package hello.itemservice.validation;

import org.junit.jupiter.api.Test;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.validation.MessageCodesResolver;

import static org.assertj.core.api.Assertions.assertThat;

public class MessageCodesResolverTest {

    // 검증 오류 코드로 메시지 코드들을 생성한다.
    /**
     * DefaultMessageCodesResolver 의 기본 메시지 생성 규칙
     * <p>
     * [객체 오류] 다음 순서로 2가지 생성
     * 1. errorCode + '.' + object name
     * 2. errorCode
     * <p>
     * [필드 오류] 다음 순서로 4가지 메서드 코드 생성
     * 1. errorCode + '.' + object name + '.' + field
     * 2. errorCode + '.' + field
     * 3. errorCode + '.' field type
     * 4. errorCode
     */

    MessageCodesResolver codesResolver = new DefaultMessageCodesResolver();

    @Test
    void messageCodesResolverObject() {
        String[] messageCodes = codesResolver.resolveMessageCodes("required", "item");
        assertThat(messageCodes).containsExactly("required.item", "required");
    }

    @Test
    void messageCodesResolverField() {
        String[] messageCodes = codesResolver.resolveMessageCodes("required", "item", "itemName", String.class);
        for (String messageCode : messageCodes) {
            System.out.println("messageCode = " + messageCode);
        }
        assertThat(messageCodes).containsExactly(
                "required.item.itemName",
                "required.itemName",
                "required.java.lang.String",
                "required"
        );

    }


}

