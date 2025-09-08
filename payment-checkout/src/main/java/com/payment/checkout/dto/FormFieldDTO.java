package com.payment.checkout.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormFieldDTO {
    private String name;
    private String label;
    private String type; // text, select, hidden
    private boolean required;
    private String placeholder;
}


