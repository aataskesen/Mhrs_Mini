package tr.mhrs.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.Instant;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String code,          // uygulama içi hata kodu (ErrorCode.name())
        String message,
        String path,          // request URI
        Map<String, Object> details
) {
    public static ApiError of(int status, String error, String code, String message, String path, Map<String,Object> details){
        return ApiError.builder()
                .timestamp(Instant.now())
                .status(status)
                .error(error)
                .code(code)
                .message(message)
                .path(path)
                .details(details)
                .build();
    }
}
