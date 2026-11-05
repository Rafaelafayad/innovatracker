package hiragi.innovatracker.exception;


import com.fasterxml.jackson.annotation.JsonInclude;


import java.time.LocalDateTime;
import java.util.Map;


@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
        LocalDateTime timestamp,
        int status,
        String erro,
        String mensagem,
        String path,
        Map<String, String> campos
) {
    public static ApiError of(int status, String erro, String mensagem, String path) {
        return new ApiError(LocalDateTime.now(), status, erro, mensagem, path, null);
    }


    public static ApiError of(int status, String erro, String mensagem, String path,
                              Map<String, String> campos) {
        return new ApiError(LocalDateTime.now(), status, erro, mensagem, path, campos);
    }
}
