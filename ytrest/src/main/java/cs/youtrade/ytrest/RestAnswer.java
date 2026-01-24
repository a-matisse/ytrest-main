package cs.youtrade.ytrest;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RestAnswer<T> {
    private final int status;
    private final T response;

    public RestAnswer(int status) {
        this(status, null);
    }

    public static <T> RestAnswer<T> getErrorAns() {
        return new RestAnswer<>(500);
    }
}
