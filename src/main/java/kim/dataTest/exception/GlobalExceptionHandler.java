package kim.dataTest.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
//전역 예외처리기 GlobalExceptionHandler
//    역할 : 코드 유효성검사 오류만 전담으로한다
//    왜 필요한가? : 지금은 컨트롤러에 메서드가 별로 없어서 메서드마다 try-catch로 예외처리를했지만 메서드가 많다면 일일히 다 할수가 없다
//    그래서 전체 컨트롤러에서 코드 유효성만 전담하는 예외처리기가 필요하다
//    비유를하자면 이전까지는  RestController가 요리 + 재료 품질 검사를 했다면 전역 예외 처리기라는 재료 품질검사 전문가를 도입한거다 그리고 rest는 요리만 전념할수 있게 되었고
//    지금은 RestController가 try-catch로 예외처리를했서 예외처리기는 안전망역할을한다.


//    필요한개념
//    1.@Slf4j-> log란 이름에 logger객체가 생성된다. 이걸로 log.info를 logger객체 작성할 필요 없이 작성한다
//    2.@ControllerAdvice-> 모든 컨트롤러 (REst 포함)에서 발생하는 예외를 전역적으로 처리하는 역할을 하도록 spring에 알려준다
//    3.@ExceptionHandler -> @ControllerAdvice 클래스내에서 특정타입의 예외를 처리할 메서드를 지정한다.
//    괄호안에 지정도니 예외 클래스IllegalAccessException.class 와 일치 하거나 그 클래스의 자식 클래스인 예외가 발생하면 , sts가 이 어노테이션이 붙은 메서드를호풀하여 예외를 처리한다.


//    핸들러 메서드
//    1.



//    1.사원을 찾을수 없을때 발생하는 예외처리(404notfound)
//
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String ,Object>> handleException(IllegalArgumentException exception){
        log.warn("Global Exception: 리소스를 찾을 수 없음 (404) - {}",exception.getMessage());
        Map<String ,Object> response = new HashMap<>();
        response.put("success",false);
        response.put("message", exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

//    2.@valid유효성 검사 실패 예외 처리(400 Bad Request)
//    컨트롤러에ㅓ 처리하기 어려운 유효성 검사 예외를 여기서 일괄처리합니다.
//    @valid 제약 조건에 붙은 조건을 만족하지 못할(나이가 1000살 인경우)경우 발동
//    응답: HTTP 상태 코드 **400 Bad Request**를 반환하며, 응답 본문에 어떤 필드에서 어떤 오류가 발생했는지(errors Map)를 담아 클라이언트에게 전달합니다. 이는 유효성 검사 오류에 대한 표준적인 REST API 응답 방식입니다.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String ,String>>handleValidationExceptions(MethodArgumentNotValidException exception ){
        Map<String ,String> errors = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField() ,error.getDefaultMessage());
        } );
        log.warn("Global Exception: 유효성 검사 실패 (400) - {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
//    3.그외 처리못한 모든 예외 처리(500 Internal Server Error)
//    가장 최상위 클래스인 Exception.class
//    1,2에서 명시하지 못한 오류 발생시 호출되는 최종 안전망
//    응답: HTTP 상태 코드 **500 Internal Server Error**를 반환합니다. 클라이언트에게는 자세한 오류 내용 대신 일반적인 오류 메시지를 전달하여 서버 내부 정보를 숨기는 것이 보안상 좋습니다.
    @ExceptionHandler(IOException.class)
    public ResponseEntity<Map<String, Object>>handleException(IOException exception){
        log.error("Global Exception: 처리되지 않은 서버 오류 (500)", exception);
        Map<String ,Object> response = new HashMap<>();
        response.put("success",false);
        response.put("message", "API 호출 중 오류가 발생했습니다:"+exception.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);

    }

//    4.정적 리소스무시 404
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Void> handleNoResourceFoundException(NoResourceFoundException ex) {
//        favicon 같은 정적 리소스 404는 무시
        if (ex.getMessage().contains("favicon")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        log.warn("Global Exception: 리소스를 찾을 수 없음 (404) - {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    // NoResourceFoundException은 처리하지 않도록 제외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,Object>> handleGlobalException(Exception ex) {


        log.error("Global Exception: 처리되지 않은 서버 오류 (500)", ex);
        Map<String ,Object> response = new HashMap<>();
        response.put("success",false);
        response.put("message", "서버 오류가 발생했습니다:"+ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}
