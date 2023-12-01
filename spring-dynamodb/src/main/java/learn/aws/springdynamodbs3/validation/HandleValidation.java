package learn.aws.springdynamodbs3.validation;

import learn.aws.springdynamodbs3.util.ResponseObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class HandleValidation {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseObject> handleValildationError(MethodArgumentNotValidException ex) {
        List<Map<String, String>> fieldErrorList = new ArrayList<>();
        Map<String, List<Map<String, String>>> errorMap = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            Map<String, String> errors = new HashMap<>();
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put("field_name", fieldName);
            errors.put("field_error_description", errorMessage);
            fieldErrorList.add(errors);
        });

        errorMap.put("fields", fieldErrorList);
        return new ResponseEntity<>(ResponseObject.builder()
                .setError(true)
                .setData(errorMap)
                .setMessage("Input field error")
                .build(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<ResponseObject> employeeNotFoundExceptionHandler(EmployeeNotFoundException ex) {

        return new ResponseEntity<>(ResponseObject.builder()
                .setError(true)
                .setMessage(ex.getMessage())
                .build(), new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ResponseObject> missingServletRequestParameterExceptionHandler(MissingServletRequestParameterException ex) {
        return new ResponseEntity<>(ResponseObject.builder()
                .setError(true)
                .setMessage(ex.getMessage())
                .build(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<ResponseObject> invalidInputExceptionHandler(InvalidInputException ex) {
        return new ResponseEntity<>(ResponseObject.builder()
                .setError(true)
                .setMessage(ex.getMessage())
                .build(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }
}
