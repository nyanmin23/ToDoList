package dev.jade.todolist.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "child_tasks")
public class ChildTask extends AbstractTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long childId;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "deadline")
    private Instant deadline;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = false)
    private ParentTask parentTask;

}
