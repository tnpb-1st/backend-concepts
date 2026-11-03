package github.io.tnpb_1st.blogapi.controller;

import github.io.tnpb_1st.blogapi.entity.Post;
import github.io.tnpb_1st.blogapi.exception.PostValidationException;
import github.io.tnpb_1st.blogapi.exception.PostNotFoundException;
import github.io.tnpb_1st.blogapi.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("posts")
public class PostController {

    private PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public List<Post> getAllPosts() {
        return postService.findAll();
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getPostById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(postService.findById(id));
        } catch (PostNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> savePost(@RequestBody Post post) {
        try {
            postService.validatePost(post);
            post.setPublishedAt(LocalDateTime.now());
            post.setUpdatedAt(post.getPublishedAt());
            return ResponseEntity.status(HttpStatus.CREATED).body(postService.savePost(post));
        } catch (PostValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("field", e.getField(), "message", e.getMessage()));
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updatePost(@PathVariable Integer id,
                                        @RequestBody Post post) {
        try {
            postService.findById(id);
            postService.validatePost(post);
            post.setId(id);
            post.setPublishedAt(LocalDateTime.now());
            post.setUpdatedAt(post.getPublishedAt());
            return ResponseEntity.status(HttpStatus.CREATED).body(postService.savePost(post));
        } catch (PostNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        } catch (PostValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("field", e.getField(), "message", e.getMessage()));
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deletePost(@PathVariable Integer id) {
        try {
            postService.findById(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }  catch (PostNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        }
    }
}
