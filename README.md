## 인프런 강의

해당 저장소의 `README.md`는 인프런 김영한님의 SpringBoot 강의 시리즈를 듣고 Spring 프레임워크의 방대한 기술들을 복기하고자 공부한 내용을 가볍게 정리한 것입니다.

문제가 될 시 삭제하겠습니다.



## 검증2 - Bean Validation

### Bean Validation - 소개

#### Bean Validation 이란?

* 이전까지 검증1 에서 진행한 Validation을 애너테이션으로 간단하게 설정할 수 있다.
* Bean Validation은 특정한 구현체가 아니라 Bean Validation 2.0(JSR-380)이라는 표준 기술이다.
  * JSR(Java Specification Requests)은 자바 플랫폼에 대한 규격을 제안하거나 기술한 것
* 다양한 구현체들이 있지만 주로 하이버네이트 Validator 를 사용함



#### 검증 애노테이션

* `@NotBlank`: 빈값 + 공백만 있는 경우를 허용하지 않음
* `@NotNull`: null 값을 허용하지 않음
* `@Range(min = 1000, max = 1000000)`: 범위 안의 값이어야 한다.
* `@Max(9999)`: 최대 9999까지만 허용한다.



### Bean Validation - 스프링 적용

#### 스프링MVC가 Bean Validator 를 사용하는 방법

1. 스프링 부트가 `spring-boot-starter-validation` 라이브러리를 넣으면 자동으로 Bean Validator 를 인지하고 스프링에 통합한다.
2. `LocalValidatorFactoryBean` 을 글로벌 Validator 로 등록한다.
3. 검증 애노테이션을 보고 검증을 수행한다.
   * 단, `@Valid, @Validated` 가 적용되어 있어야 함



#### 검증 순서

1. `ModelAttribute` 각각의 필드에 타입 변환을 시도
2. 성공하면 Validator 적용
3. 실패 시 검증 자체의 과정이 필요없으므로 `typeMismatch` 로 `FieldError` 추가



### Bean Validation - 에러코드

Bean Validation 이 기본으로 제공하는 오류 메시지의 등록 과정은 다음과 같다.

* 오류 코드가 애노테이션 이름으로 등록된다. 
  * 마치 스프링의 기본 타입 에러 메시지 처리인 `typeMismatch` 와 유사하다.
  * ex) `[NotNull.item.quantity, NotNull.quantity, NotNull.java.lang.Integer, NotNull]`
  * `errors.properties` 와 같은 messageSource 에서 위와 같은 명명 규칙을 적용하면 에러 메시지를 변경할 수 있다.



#### BeanValidation이 메시지를 찾는 순서

1. 생성된 메시지 코드 순서대로 messageSource 에서 메시지 찾기
2. 애노테이션의 `message` 속성 사용
3. 라이브러리가 제공하는 기본 값 사용



### Bean Validation - 오브젝트 오류

Bean Validation에서 특정 필드(`FieldError`) 가 아닌 해당 오브젝트 관련 오류(`ObjectError`) 처리는 `@ScriptAssert()` 를 아래와 같이 사용하면 된다.

```java
 @Data
 @ScriptAssert(lang = "javascript", script = "_this.price * _this.quantity >=
 10000")
 public class Item {
//...
}
```



하지만 제약 사항이 많고 복잡하며, 검증 기능이 해당 객체의 범위를 넘어서며 다른 객체와 같이 검증해야 하는 경우도 생기는데 이럴 경우 대응이 어려우므로 **오브젝트 오류 관련 부분만 직접 자바 코드로 컨트롤러에 작성하는 것이 더 올바르다.**

```java
// 특정 필드 예외가 아닌 전체 예외
if (item.getPrice() != null && item.getQuantity() != null) {
    int resultPrice = item.getPrice() * item.getQuantity();
    if (resultPrice < 10000) {
        bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
    }
}

if (bindingResult.hasErrors()) {
    log.info("errors = {}", bindingResult);
    return "validation/v3/editForm";
}
```



### Bean Validation - groups

동일한 모델 객체를 등록할 때와 수정할 때 각각 다르게 검증하는 방법



#### 방법 2가지

* BeanValidation의 groups 기능을 사용한다.
* 도메인 객체를 직접 사용하지 않고, ItemSaveForm, ItemUpdateForm 같은 폼 전송을 위한 별도의 모델 객체를 만들어서 사용한다.



#### 예시

Item - groups 적용

```java
package hello.itemservice.domain.item;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class Item {

    @NotNull(groups = UpdateCheck.class) // 수정 시에만 적용
    private Long id;

    @NotBlank(groups = {SaveCheck.class, UpdateCheck.class}) // 빈값 + 공백만 있는 경우를 허용하지 않는다.
    private String itemName;

    @NotNull(groups = {SaveCheck.class, UpdateCheck.class}) // null 를 허용하지 않는다.
    @Range(min = 1_000, max = 1_000_000, groups = {SaveCheck.class, UpdateCheck.class}) // 범위 안의 값이어야 한다.
    private Integer price;

    @NotNull(groups = {SaveCheck.class, UpdateCheck.class})
    @Max(value = 9_999, groups = SaveCheck.class) // 최대 9999까지만 허용한다, 등록시에만 적용
    private Integer quantity;

    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}

```



저장 로직에 SaveCheck Groups 적용

```java
 @PostMapping("/add")
 public String addItemV2(@Validated(SaveCheck.class) @ModelAttribute Item item,
 BindingResult bindingResult, RedirectAttributes redirectAttributes) {
//...
}
```



수정 로직에 UpdateCheck Groups 적용

```java
 @PostMapping("/{itemId}/edit")
 public String editV2(@PathVariable Long itemId, @Validated(UpdateCheck.class)
 @ModelAttribute Item item, BindingResult bindingResult) {
//...
}
```

