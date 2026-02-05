package dev.jade.todolist.repositories;

import dev.jade.todolist.models.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {

    List<Section> findByUser_UserIdOrderByDisplayOrder(Long userId);

    boolean existsBySectionId(Long sectionId);
}
