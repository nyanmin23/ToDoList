# API Contract

## Endpoints
- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/logout`
- `GET /api/sections`
- `POST /api/sections`
- `PUT /api/sections/{sectionId}`
- `DELETE /api/sections/{sectionId}`
- `GET /api/sections/{sectionId}/parent-tasks`
- `POST /api/sections/{sectionId}/parent-tasks`
- `PUT /api/sections/{sectionId}/parent-tasks/{parentTaskId}`
- `DELETE /api/sections/{sectionId}/parent-tasks/{parentTaskId}`
- `GET /api/sections/{sectionId}/parent-tasks/{parentTaskId}/child-tasks`
- `POST /api/sections/{sectionId}/parent-tasks/{parentTaskId}/child-tasks`
- `PUT /api/sections/{sectionId}/parent-tasks/{parentTaskId}/child-tasks/{childTaskId}`
- `DELETE /api/sections/{sectionId}/parent-tasks/{parentTaskId}/child-tasks/{childTaskId}`

## Response Shapes

### AuthResponse
```json
{
  "userId": 1,
  "username": "jade",
  "email": "jade@example.com",
  "createdAt": "2026-02-13T00:00:00Z",
  "updatedAt": "2026-02-13T00:00:00Z"
}
```

### SectionResponse
```json
{
  "sectionId": 10,
  "sectionName": "Draft",
  "createdAt": "2026-02-13T00:00:00Z",
  "updatedAt": "2026-02-13T00:00:00Z"
}
```

### ParentTaskResponse
```json
{
  "parentTaskId": 100,
  "parentTaskTitle": "outline structure",
  "deadline": "2026-02-20T10:00:00Z",
  "priority": "LOW",
  "isCompleted": false,
  "createdAt": "2026-02-13T00:00:00Z",
  "updatedAt": "2026-02-13T00:00:00Z",
  "completedAt": null
}
```

### ChildTaskResponse
```json
{
  "childTaskId": 200,
  "childTaskTitle": "motivation/why"
}
```

### ApiErrorResponse
```json
{
  "timestamp": "2026-02-13T00:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "code": "VALIDATION_ERROR",
  "message": "Validation failed",
  "details": {
    "email": "Email is required"
  },
  "path": "/api/auth/register"
}
```
