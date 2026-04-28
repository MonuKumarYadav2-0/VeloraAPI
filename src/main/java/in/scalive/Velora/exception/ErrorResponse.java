package in.scalive.Velora.exception;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@Builder
public class ErrorResponse{
  private LocalDateTime timeStamp;
  private int status;
  private String error;
  private String message;
  private String path;
  private Map<String, String> validationErrors;
  public ErrorResponse(LocalDateTime timeStamp, int status, String error, String message, String path,
		Map<String, String> validationErrors) {
	this.timeStamp = timeStamp;
	this.status = status;
	this.error = error;
	this.message = message;
	this.path = path;
	this.validationErrors = validationErrors;
  }
  
}
