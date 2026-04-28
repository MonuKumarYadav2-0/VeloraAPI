package in.scalive.Velora.exception;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import in.scalive.Velora.dto.response.ApiResponseDTO;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex,HttpServletRequest request){
		ErrorResponse error=ErrorResponse.builder()
				             .timeStamp(LocalDateTime.now())
				             .status(HttpStatus.NOT_FOUND.value())
				             .error("Not Found")
				             .message(ex.getMessage())
				             .path(request.getRequestURI())
				             .build();
		return new ResponseEntity<>(error,HttpStatus.NOT_FOUND);	
	}
    
    @ExceptionHandler(DuplicateResourceException.class)
	public ResponseEntity<ErrorResponse> handleDuplicateResourceException(DuplicateResourceException ex,HttpServletRequest request){
		ErrorResponse error=ErrorResponse.builder()
				             .timeStamp(LocalDateTime.now())
				             .status(HttpStatus.CONFLICT.value())
				             .error("Conflict")
				             .message(ex.getMessage())
				             .path(request.getRequestURI())
				             .build();
		return new ResponseEntity<>(error,HttpStatus.CONFLICT);	
	}
    @ExceptionHandler(InsufficientStockException.class)
	public ResponseEntity<ErrorResponse> handleInsufficientStockException(InsufficientStockException ex,HttpServletRequest request){
		ErrorResponse error=ErrorResponse.builder()
				             .timeStamp(LocalDateTime.now())
				             .status(HttpStatus.BAD_REQUEST.value())
				             .error("Insufficient Stock")
				             .message(ex.getMessage())
				             .path(request.getRequestURI())
				             .build();
		return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);	
	}
    
    @ExceptionHandler(EmptyCartException.class)
	public ResponseEntity<ErrorResponse> handleEmptyCartException(EmptyCartException ex,HttpServletRequest request){
		ErrorResponse error=ErrorResponse.builder()
				             .timeStamp(LocalDateTime.now())
				             .status(HttpStatus.BAD_REQUEST.value())
				             .error("Empty Cart")
				             .message(ex.getMessage())
				             .path(request.getRequestURI())
				             .build();
		return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);	
	}
    
    @ExceptionHandler(InvalidOperationException.class)
	public ResponseEntity<ErrorResponse> handleInvalidOperationException(InvalidOperationException ex,HttpServletRequest request){
		ErrorResponse error=ErrorResponse.builder()
				             .timeStamp(LocalDateTime.now())
				             .status(HttpStatus.BAD_REQUEST.value())
				             .error("Invalid Operation")
				             .message(ex.getMessage())
				             .path(request.getRequestURI())
				             .build();
		return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);	
	}
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex,HttpServletRequest request){
	Map<String, String>validationErrors=new HashMap<>();
	for(FieldError error:ex.getBindingResult().getFieldErrors()) {
	      validationErrors.put(error.getField(),error.getDefaultMessage());
	}
    	ErrorResponse error=ErrorResponse.builder()
				             .timeStamp(LocalDateTime.now())
				             .status(HttpStatus.BAD_REQUEST.value())
				             .error("Validation Failed")
				             .message("Input Validation Failed. Please check the error")
				             .validationErrors(validationErrors)
				             .path(request.getRequestURI())
				             .build();
		return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);	
	}
    
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorResponse> handleTypeMismatchException(MethodArgumentTypeMismatchException ex,HttpServletRequest request){
	Class c=ex.getRequiredType();
	String message=String.format("Invalid value '%s' for the parameter '%s'. Excepted type: '%s'",ex.getValue(),ex.getName(),c!=null?c.getSimpleName():"unknown");
    	ErrorResponse error=ErrorResponse.builder()
				             .timeStamp(LocalDateTime.now())
				             .status(HttpStatus.BAD_REQUEST.value())
				             .error("Type Mismatch")
				             .message(message)
				             .path(request.getRequestURI())
				             .build();
		return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);	
	}
    
    @ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex,HttpServletRequest request){
    	ErrorResponse error=ErrorResponse.builder()
				             .timeStamp(LocalDateTime.now())
				             .status(HttpStatus.BAD_REQUEST.value())
				             .error("Wrong Argument")
				             .message(ex.getMessage())
				             .path(request.getRequestURI())
				             .build();
		return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);	
	}
    
    @ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleException(Exception ex,HttpServletRequest request){
    	ErrorResponse error=ErrorResponse.builder()
				             .timeStamp(LocalDateTime.now())
				             .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
				             .error("Internal Server Error")
				             .message(ex.getMessage())
				             .path(request.getRequestURI())
				             .build();
		return new ResponseEntity<>(error,HttpStatus.INTERNAL_SERVER_ERROR);	
	}
    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request) {

        ErrorResponse error = ErrorResponse.builder()
                .timeStamp(LocalDateTime.now())
                .status(403)
                .error("Forbidden")
                .message("Access Denied")
                .path(request.getRequestURI())
                .validationErrors(null)
                .build();

        return ResponseEntity.status(403).body(error);
    }
    
    
    @ExceptionHandler(PaymentVerificationException.class)
    public ResponseEntity<Object> handlePaymentException(PaymentVerificationException ex,
                                                         HttpServletRequest request) {

        Map<String, Object> error = new HashMap<>();
        error.put("timeStamp", LocalDateTime.now());
        error.put("status", 400);
        error.put("error", "Payment Verification Failed");
        error.put("message", ex.getMessage());
        error.put("path", request.getRequestURI());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
