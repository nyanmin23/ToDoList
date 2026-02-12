package dev.jade.todolist.common.pagination;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * Service for handling snapshot-based pagination.
 * Ensures consistency across pagination requests by locking to a specific list version.
 */
@Slf4j
@Service
public class PaginationService {

    // Snapshot expiration time - after this, client must restart pagination
    private static final Duration SNAPSHOT_EXPIRATION = Duration.ofMinutes(15);

    /**
     * Create a paginated response with snapshot semantics
     *
     * @param items          List of items in current page
     * @param request        Pagination request parameters
     * @param currentVersion Current database timestamp for list version
     * @param hasMore        Whether there are more pages
     * @param totalCount     Total count (optional)
     * @param <T>            Type of items
     * @return Paginated response with snapshot version
     */
    public <T> PagedResponse<T> createPagedResponse(
            List<T> items,
            PaginationRequest request,
            Instant currentVersion,
            boolean hasMore,
            Long totalCount
    ) {
        // Determine snapshot version
        Instant snapshotVersion = request.getListVersion() != null
                ? request.getListVersion()
                : currentVersion;

        // Validate snapshot hasn't expired
        if (request.getListVersion() != null) {
            validateSnapshotVersion(request.getListVersion());
        }

        // Determine next cursor (last item's rank if items exist)
        String nextCursor = null;
        if (!items.isEmpty() && hasMore) {
            // This would need to be extracted from the last item
            // Implementation depends on how rank is stored in item
            // For now, placeholder - should be overridden in specific implementations
            nextCursor = request.getCursor(); // Placeholder
        }

        return PagedResponse.<T>builder()
                .items(items)
                .listVersion(snapshotVersion)
                .nextCursor(nextCursor)
                .hasMore(hasMore)
                .totalCount(request.isIncludeTotalCount() ? totalCount : null)
                .pageSize(request.getPageSize())
                .build();
    }

    /**
     * Validate that snapshot version hasn't expired
     */
    public void validateSnapshotVersion(Instant listVersion) {
        if (listVersion == null) {
            return;
        }

        Instant now = Instant.now();
        Duration age = Duration.between(listVersion, now);

        if (age.compareTo(SNAPSHOT_EXPIRATION) > 0) {
            throw new SnapshotExpiredException(listVersion, SNAPSHOT_EXPIRATION);
        }
    }

    /**
     * Build keyset pagination WHERE clause for LexoRank ordering
     *
     * @param cursor      Last rank from previous page (null for first page)
     * @param listVersion Snapshot version to lock pagination to
     * @return SQL condition for keyset pagination
     */
    public String buildKeysetCondition(String cursor, Instant listVersion) {
        if (cursor == null) {
            // First page - just use list version
            return String.format("updated_at <= '%s'", listVersion);
        }

        // Subsequent pages - items after cursor AND within snapshot
        return String.format(
                "rank > '%s' AND updated_at <= '%s'",
                cursor,
                listVersion
        );
    }
}

/**
 * Exception thrown when pagination snapshot has expired.
 * Client must restart pagination from the first page.
 */
class SnapshotExpiredException extends RuntimeException {

    private final Instant expiredVersion;
    private final Duration maxAge;

    public SnapshotExpiredException(Instant expiredVersion, Duration maxAge) {
        super(String.format(
                "Pagination snapshot expired. Version %s is older than %d minutes.",
                expiredVersion,
                maxAge.toMinutes()
        ));
        this.expiredVersion = expiredVersion;
        this.maxAge = maxAge;
    }

    public Instant getExpiredVersion() {
        return expiredVersion;
    }

    public Duration getMaxAge() {
        return maxAge;
    }
}

