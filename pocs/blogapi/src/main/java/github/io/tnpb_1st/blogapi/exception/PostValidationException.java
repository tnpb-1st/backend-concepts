package github.io.tnpb_1st.blogapi.exception;

public class PostValidationException extends RuntimeException {

    private final String field;

    public PostValidationException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
