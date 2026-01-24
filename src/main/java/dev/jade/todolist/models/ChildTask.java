package dev.jade.todolist.models;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "child_tasks")
public class ChildTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long childId;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "deadline")
    private Timestamp deadline;

    @Column(name = "priority", nullable = false)
    private Priority priority;

    @Column(name = "is_completed", nullable = false)
    private boolean isCompleted;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at", nullable = false)
    private Timestamp updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = false)
    private ParentTask parentTask;

}
