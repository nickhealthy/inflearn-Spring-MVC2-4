package hello.itemservice.domain.item;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class Item {

    private Long id;

    @NotBlank // 빈값 + 공백만 있는 경우를 허용하지 않는다.
    private String itemName;

    @NotNull // null 를 허용하지 않는다.
    @Range(min = 1_000, max = 1_000_000) // 범위 안의 값이어야 한다.
    private Integer price;

    @NotNull
    @Max(9_999) // 최대 9999까지만 허용한다.
    private Integer quantity;

    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
