package github.io.tnpb_1st.blogapi.exception;

public class PostNotFoundException extends RuntimeException {

    public PostNotFoundException(Integer id) {
        super("Post with id " + id + " not found");
    }
}
