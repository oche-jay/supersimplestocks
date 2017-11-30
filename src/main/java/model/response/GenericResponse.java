package model.response;

import lombok.Data;

@Data
public class GenericResponse {
    String message;
    int httpStatus;

    public GenericResponse(String message, int httpStatus) {
        setMessage(message);
        setHttpStatus(httpStatus);
    }
}
