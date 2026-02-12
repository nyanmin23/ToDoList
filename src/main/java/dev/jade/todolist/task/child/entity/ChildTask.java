package dev.jade.todolist.task.child.entity;

import dev.jade.todolist.domain.base.BaseTaskEntity;
import dev.jade.todolist.task.parent.entity.ParentTask;
import jakarta.persistence.Column;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@jakarta.persistence.Entity
@Table(
        name = "child_tasks",
        indexes = {
                @Index(name = "idx_child_task_parent_rank", columnList = "parent_task_id, rank"),
                @Index(name = "idx_child_task_rank", columnList = "rank")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class ChildTask extends BaseTaskEntity {

    @jakarta.persistence.Id
    @jakarta.persistence.GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long childTaskId;

    @Column(name = "child_task_title", nullable = false)
    private String childTaskTitle;

    @jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @jakarta.persistence.JoinColumn(name = "parent_task_id", nullable = false)
    private ParentTask parentTask;
}

