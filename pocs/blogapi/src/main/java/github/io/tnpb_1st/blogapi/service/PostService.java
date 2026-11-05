package github.io.tnpb_1st.blogapi.service;

import github.io.tnpb_1st.blogapi.entity.Post;
import github.io.tnpb_1st.blogapi.exception.PostValidationException;
import github.io.tnpb_1st.blogapi.exception.PostNotFoundException;
import github.io.tnpb_1st.blogapi.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    private PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<Post> findAll() {
        return postRepository.findAll();
    }

    public List<Post> search(String term) {
        return postRepository.search(term);
    }

    public Post findById(Integer id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id));
    }

    public Post savePost(Post post) {
        return postRepository.save(post);
    }

    public void deletePost(Integer id) {
        Post post = findById(id);
        postRepository.delete(post);
    }

    public void validatePost(Post post) {
        validateTitle(post);
        validateContent(post);
        validateCategory(post);
        validateTags(post);
    }

    public void validateTitle(Post post) {
        if (post.getTitle() == null || post.getTitle().isBlank()) {
            throw new PostValidationException("title", "Post title must not be empty");
        }
    }

    public void validateContent(Post post) {
        if (post.getContent() == null || post.getContent().isBlank()) {
            throw new PostValidationException("content", "Post content must not be empty");
        }
    }

    public void validateCategory(Post post) {
        if (post.getCategory() == null) {
            throw new PostValidationException("category", "Post category must not be null");
        }
    }

    public void validateTags(Post post) {
        if (post.getTags() == null || post.getTags().isEmpty()) {
            throw new PostValidationException("tags", "Post tags must not be empty");
        }
    }

}
