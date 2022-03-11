package br.com.diogob.inventory.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FieldValidationError {

    private String fieldName;

    private String message;

}
