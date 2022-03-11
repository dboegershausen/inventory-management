package br.com.diogob.inventory.exceptions;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FieldValidationErrorResponse {

    private List<FieldValidationError> errors = new ArrayList<>();

}
