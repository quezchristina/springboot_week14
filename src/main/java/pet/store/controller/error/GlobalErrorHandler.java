package pet.store.controller.error;

import java.util.NoSuchElementException;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalErrorHandler {
	
@Data
private class ExceptionMessage {
	private String message;
}

@ExceptionHandler(NoSuchElementException.class)
@ResponseStatus(code = HttpStatus.NOT_FOUND)
public ExceptionMessage handleNoSuchElementException (NoSuchElementException ex, WebRequest webRequest) {
	return buildExceptionMessage (ex, webRequest);
}

private ExceptionMessage buildExceptionMessage(NoSuchElementException ex, WebRequest webRequest) {
	String message = ex.toString();
	if (webRequest instanceof ServletWebRequest swr) {
		log.error("Exception: {}", ex.toString());
	} else {
		log.error("Exception: " , ex);
	}
	
	ExceptionMessage excMsg = new ExceptionMessage();
	
	excMsg.setMessage(message);
	
	return excMsg;
}
}

