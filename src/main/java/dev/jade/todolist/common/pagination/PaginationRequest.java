package dev.jade.todolist.common.pagination;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Request parameters for snapshot-based pagination.
 * Ensures consistency across paginated requests using list version snapshots.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginationRequest {

    /**
     * Page size (number of items per page)
     * Default: 20, Min: 1, Max: 100
     */
    @Min(value = 1, message = "Page size must be at least 1")
    @Max(value = 100, message = "Page size cannot exceed 100")
    @Builder.Default
    private int pageSize = 20;

    /**
     * Cursor for keyset pagination (LexoRank of last item from previous page)
     * Null for first page
     */
    private String cursor;

    /**
     * Snapshot version from first request.
     * All subsequent requests in pagination session must use same version.
     * Null for first request - server will generate and return.
     */
    private Instant listVersion;

    /**
     * Whether to include total count in response.
     * Setting to false improves performance for large datasets.
     */
    @Builder.Default
    private boolean includeTotalCount = false;
}
