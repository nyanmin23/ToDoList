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

    Optional<Section> findBySectionId(Long sectionId);

    @Query("SELECT s FROM Section s WHERE s.user.userId = :userId")
    List<Section> findAllByUser_UserId(@Param("userId") Long userId);
}
