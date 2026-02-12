package dev.jade.todolist.common.pagination;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

/**
 * Generic paginated response with snapshot semantics.
 * Maintains consistency across pagination requests using list version timestamps.
 *
 * @param <T> Type of items in the page
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponse<T> {

    /**
     * List of items in current page
     */
    private List<T> items;

    /**
     * Snapshot version - timestamp when the list state was captured.
     * Client must send this back on subsequent requests to maintain consistency.
     */
    private Instant listVersion;

    /**
     * Cursor for next page (last item's rank)
     */
    private String nextCursor;

    /**
     * Whether there are more pages available
     */
    private boolean hasMore;

    /**
     * Total count (optional - may be expensive to compute)
     */
    private Long totalCount;

    /**
     * Current page size
     */
    private int pageSize;
}
