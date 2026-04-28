package in.scalive.Velora.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) //to pass only non null values in json format 
public class ApiResponseDTO<T> {
     private Boolean success;
     private String message;
     private LocalDateTime timeStamp;
     private T data;
     
     public static <T> ApiResponseDTO<T> success(String message,T data){
    	       return ApiResponseDTO.<T>builder()
    	    		      .success(true)
    	    		      .message(message)
    	    		      .timeStamp(LocalDateTime.now())
    	    		      .data(data)
    	    		      .build();
     }
     
     public static <T> ApiResponseDTO<T> success(String message){
	       return ApiResponseDTO.<T>builder()
	    		      .success(true)
	    		      .message(message)
	    		      .timeStamp(LocalDateTime.now())
	    		      .build();
     }
     
     public static <T> ApiResponseDTO<T> success(T data){
	       return ApiResponseDTO.<T>builder()
	    		      .success(true)
	    		      .data(data)
	    		      .timeStamp(LocalDateTime.now())
	    		      .build();
   }
}
