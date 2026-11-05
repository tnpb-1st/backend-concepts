package github.io.tnpb_1st.blogapi.repository;

import github.io.tnpb_1st.blogapi.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    @Query("""
            select p from Post p
            where lower(p.title) like lower(concat('%', :term, '%'))
               or lower(p.content) like lower(concat('%', :term, '%'))
               or lower(cast(p.category as string)) like lower(concat('%', :term, '%'))
            """)
    List<Post> search(@Param("term") String term);
}
