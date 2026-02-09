package dev.jade.todolist.repositories;

import dev.jade.todolist.models.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {

    List<Section> findByUser_UserIdOrderByDisplayOrder(Long userId);

    @Query("""
                SELECT s FROM Section s
                WHERE s.sectionId = :sectionId
                AND s.user.userId = :userId
            """)
    Optional<Section> findByIdAndUserId(
            @Param("sectionId") Long sectionId,
            @Param("userId") Long userId
    );

    @Query("""
                SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END
                FROM Section s
                WHERE s.sectionId = :sectionId
                AND s.user.userId = :userId
            """)
    boolean existsByIdAndUserId(
            @Param("sectionId") Long sectionId,
            @Param("userId") Long userId
    );
}
