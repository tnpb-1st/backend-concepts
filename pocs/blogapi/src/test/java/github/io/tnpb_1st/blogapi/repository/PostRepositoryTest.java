package github.io.tnpb_1st.blogapi.repository;

import github.io.tnpb_1st.blogapi.entity.Category;
import github.io.tnpb_1st.blogapi.entity.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void setUp() {
        postRepository.save(new Post("Learning Java", "Streams and records", Category.Technology, List.of("java")));
        postRepository.save(new Post("My trip", "Visited the mountains in JAVA island", Category.Travel, List.of("trip")));
        postRepository.save(new Post("Quarterly plan", "Budget and goals", Category.Business, List.of("plan")));
    }

    @Test
    void search_matchesTitleAndContentIgnoringCase() {
        List<Post> result = postRepository.search("java");

        assertThat(result).extracting(Post::getTitle)
                .containsExactlyInAnyOrder("Learning Java", "My trip");
    }

    @Test
    void search_matchesCategory() {
        List<Post> result = postRepository.search("busin");

        assertThat(result).extracting(Post::getTitle).containsExactly("Quarterly plan");
    }

    @Test
    void search_withNoMatch_returnsEmptyList() {
        assertThat(postRepository.search("zzz")).isEmpty();
    }
}
