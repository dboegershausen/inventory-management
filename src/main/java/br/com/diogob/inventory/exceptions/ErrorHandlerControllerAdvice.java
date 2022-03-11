package br.com.diogob.inventory.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ErrorHandlerControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    FieldValidationErrorResponse onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        var error = new FieldValidationErrorResponse();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            error.getErrors().add(new FieldValidationError(fieldError.getField(), fieldError.getDefaultMessage()));
        }
        return error;
    }

}
