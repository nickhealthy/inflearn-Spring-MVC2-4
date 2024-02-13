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
