package dev.jade.todolist.task.parent.entity;

import dev.jade.todolist.domain.base.BaseTaskEntity;
import dev.jade.todolist.section.entity.Section;
import dev.jade.todolist.task.child.entity.ChildTask;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "parent_tasks",
        indexes = {
                @Index(name = "idx_parent_task_section_rank", columnList = "section_id, rank"),
                @Index(name = "idx_parent_task_rank", columnList = "rank")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class ParentTask extends BaseTaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long parentTaskId;

    @Column(name = "parent_task_title", nullable = false)
    private String parentTaskTitle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;

    @OneToMany(
            mappedBy = "parentTask",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @OrderBy("rank ASC")
    private List<ChildTask> childTasks = new ArrayList<>();
}
