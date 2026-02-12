package dev.jade.todolist.section.entity;

import dev.jade.todolist.domain.base.RankedEntity;
import dev.jade.todolist.task.parent.entity.ParentTask;
import dev.jade.todolist.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "sections",
        indexes = {
                @Index(name = "idx_section_user_rank", columnList = "user_id, rank"),
                @Index(name = "idx_section_rank", columnList = "rank")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Section extends RankedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sectionId;

    @Column(name = "section_name", length = 50, nullable = false)
    private String sectionName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(
            mappedBy = "section",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @OrderBy("rank ASC")
    private List<ParentTask> parentTasks = new ArrayList<>();
}
