package dev.jade.todolist.common.exception;

/**
 * Exception thrown when a Section resource cannot be found.
 * HTTP Status: 404 NOT FOUND
 */
public class SectionNotFoundException extends ResourceNotFoundException {

    private static final String ERROR_CODE = "SECTION_NOT_FOUND";
    private static final String RESOURCE_TYPE = "Section";

    public SectionNotFoundException(Long sectionId) {
        super(
                ERROR_CODE,
                buildUserMessage(RESOURCE_TYPE),
                buildDeveloperMessage(RESOURCE_TYPE, "sectionId", sectionId)
        );
    }

    public SectionNotFoundException(Long sectionId, Long userId) {
        super(
                ERROR_CODE,
                buildUserMessage(RESOURCE_TYPE),
                String.format(
                        "Section not found with sectionId: %d for userId: %d",
                        sectionId,
                        userId
                )
        );
    }
}

/**
 * Exception thrown when a Task resource (Parent or Child) cannot be found.
 * HTTP Status: 404 NOT FOUND
 */
class TaskNotFoundException extends ResourceNotFoundException {

    private static final String ERROR_CODE = "TASK_NOT_FOUND";
    private static final String RESOURCE_TYPE = "Task";

    public TaskNotFoundException(String taskType, Long taskId) {
        super(
                ERROR_CODE,
                buildUserMessage(RESOURCE_TYPE),
                buildDeveloperMessage(taskType, "taskId", taskId)
        );
    }

    public TaskNotFoundException(String taskType, Long taskId, Long userId) {
        super(
                ERROR_CODE,
                buildUserMessage(RESOURCE_TYPE),
                String.format(
                        "%s not found with taskId: %d for userId: %d",
                        taskType,
                        taskId,
                        userId
                )
        );
    }
}

/**
 * Exception thrown when a User resource cannot be found.
 * HTTP Status: 404 NOT FOUND
 */
public class UserNotFoundException extends ResourceNotFoundException {

    private static final String ERROR_CODE = "USER_NOT_FOUND";
    private static final String RESOURCE_TYPE = "User";

    public UserNotFoundException(Long userId) {
        super(
                ERROR_CODE,
                buildUserMessage(RESOURCE_TYPE),
                buildDeveloperMessage(RESOURCE_TYPE, "userId", userId)
        );
    }

    public UserNotFoundException(String email) {
        super(
                ERROR_CODE,
                "User account not found.",
                buildDeveloperMessage(RESOURCE_TYPE, "email", email)
        );
    }
}