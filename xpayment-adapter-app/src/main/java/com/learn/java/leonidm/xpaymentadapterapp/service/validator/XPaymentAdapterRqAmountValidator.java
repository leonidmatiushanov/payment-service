package com.learn.java.leonidm.xpaymentadapterapp.service.validator;

import java.math.BigDecimal;

public class XPaymentAdapterRqAmountValidator {

    /**
     * Проверка с отрицательной суммой, отсутствующей суммой/валютой или некорректным количеством знаков после запятой
     *
     * @param amount сумма
     * @return да/нет
     */
    public static boolean validate(BigDecimal amount, String currency) {
        if (amount == null || currency == null || currency.isBlank()) {
            return false;
        }

        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            return false;
        }

        if (amount.scale() != 2) {
            return false;
        }

        return true;
    }
}
