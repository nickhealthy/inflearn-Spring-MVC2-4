package hello.itemservice.web.validation;


import hello.itemservice.domain.item.Item;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class ItemValidator implements Validator {

    /**
     * 해당 검증기를 지원하는 여부 확인
     *
     * @param clazz the {@link Class} that this {@link Validator} is
     *              being asked if it can {@link #validate(Object, Errors) validate}
     * @return
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return Item.class.isAssignableFrom(clazz);
    }

    /**
     * 검증 대상 객체와 BindingResult
     *
     * @param target the object that is to be validated
     * @param errors contextual state about the validation process
     */
    @Override
    public void validate(Object target, Errors errors) {
        Item item = (Item) target;

        // 검증 로직
        // 아래와 같음
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "itemName", "required");

//        // 검증 로직
//        if (!StringUtils.hasText(item.getItemName())) {
////            bindingResult.addError(new FieldError("item", "itemName", item.getItemName(), false, new String[]{"required.item.itemName"}, null, null));
//            errors.rejectValue("itemName", "required");
//        }

        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1_000_000) {
//            bindingResult.addError(new FieldError("item", "price", item.getPrice(), false, new String[]{"range.item.price"}, new Object[]{1000, 10000}, null));
            errors.rejectValue("price", "range", new Object[]{1000, 1000000}, null);
        }

        if (item.getQuantity() == null || item.getQuantity() > 9999) {
//            bindingResult.addError(new FieldError("item", "quantity", item.getQuantity(), false, new String[]{"max.item.quantity"}, new Object[]{9999}, null));
            errors.rejectValue("quantity", "max", new Object[]{9999}, null);

        }

        // 특정 필드가 아닌 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10_000) {
//                bindingResult.addError(new ObjectError("item", new String[]{"totalPriceMin"}, new Object[]{10000, resultPrice}, null));
                errors.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }

    }
}
