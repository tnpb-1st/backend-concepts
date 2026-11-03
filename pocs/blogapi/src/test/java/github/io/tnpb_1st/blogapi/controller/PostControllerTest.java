package github.io.tnpb_1st.blogapi.controller;

import github.io.tnpb_1st.blogapi.entity.Category;
import github.io.tnpb_1st.blogapi.entity.Post;
import github.io.tnpb_1st.blogapi.exception.PostNotFoundException;
import github.io.tnpb_1st.blogapi.exception.PostValidationException;
import github.io.tnpb_1st.blogapi.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
class PostControllerTest {

    private static final String VALID_POST_JSON = """
            {"title": "My title", "content": "My content", "category": "Technology", "tags": ["java", "spring"]}
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PostService postService;

    private Post samplePost(Integer id) {
        Post post = new Post("My title", "My content", Category.Technology, List.of("java", "spring"));
        post.setId(id);
        return post;
    }

    @Test
    void getAllPosts_returns200WithPosts() throws Exception {
        when(postService.findAll()).thenReturn(List.of(samplePost(1), samplePost(2)));

        mockMvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("My title"));
    }

    @Test
    void getPostById_whenFound_returns200WithPost() throws Exception {
        when(postService.findById(1)).thenReturn(samplePost(1));

        mockMvc.perform(get("/posts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("My title"))
                .andExpect(jsonPath("$.content").value("My content"))
                .andExpect(jsonPath("$.category").value("Technology"))
                .andExpect(jsonPath("$.tags[0]").value("java"));
    }

    @Test
    void getPostById_whenMissing_returns404WithMessage() throws Exception {
        when(postService.findById(99)).thenThrow(new PostNotFoundException(99));

        mockMvc.perform(get("/posts/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Post with id 99 not found"));
    }

    @Test
    void savePost_whenValid_returns201WithSavedPost() throws Exception {
        when(postService.savePost(any(Post.class))).thenReturn(samplePost(1));

        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_POST_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("My title"));
    }

    @Test
    void savePost_whenInvalid_returns400WithFieldAndMessage() throws Exception {
        doThrow(new PostValidationException("title", "Post title must not be empty"))
                .when(postService).validatePost(any(Post.class));

        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "", "content": "My content", "category": "Technology", "tags": ["java"]}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("title"))
                .andExpect(jsonPath("$.message").value("Post title must not be empty"));
    }

    @Test
    void deletePost_whenFound_returns204WithEmptyBodyAndDeletes() throws Exception {
        mockMvc.perform(delete("/posts/1"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(postService).deletePost(1);
    }

    @Test
    void deletePost_whenMissing_returns404WithMessage() throws Exception {
        doThrow(new PostNotFoundException(99)).when(postService).deletePost(99);

        mockMvc.perform(delete("/posts/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Post with id 99 not found"));
    }

    @Test
    void updatePost_whenMissing_returns404WithMessage() throws Exception {
        when(postService.findById(99)).thenThrow(new PostNotFoundException(99));

        mockMvc.perform(put("/posts/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_POST_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Post with id 99 not found"));
    }

    @Test
    void updatePost_whenInvalid_returns400WithFieldAndMessage() throws Exception {
        when(postService.findById(1)).thenReturn(samplePost(1));
        doThrow(new PostValidationException("tags", "Post tags must not be empty"))
                .when(postService).validatePost(any(Post.class));

        mockMvc.perform(put("/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_POST_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("tags"))
                .andExpect(jsonPath("$.message").value("Post tags must not be empty"));
    }
}
