package com.payment.checkout.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethodDTO {
    private String code;
    private String name;
    private int sort;
    private boolean available;
    private String icon;
    private String disabledReason;
    private List<FormFieldDTO> fields;

    public PaymentMethodDTO(String code, String name, int sort, boolean available) {
        this.code = code;
        this.name = name;
        this.sort = sort;
        this.available = available;
    }
}


