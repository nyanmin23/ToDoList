package dev.jade.todolist.repositories;

import dev.jade.todolist.models.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {

    Optional<Section> findBySectionId(Long sectionId);

    //    @EntityGraph(attributePaths = {"parentTasks", "parentTasks.childTasks"})
    List<Section> findByUser_UserIdOrderByDisplayOrder(Long userId);

    boolean existsBySectionId(Long sectionId);
}
