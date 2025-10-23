package brainbrawl.dao;

import brainbrawl.model.GameResult;
import java.util.List;

/**
 * DAO interface for managing quiz result records.
 * Provides methods to save and retrieve game results from the database.
 */
public interface ResultDao {

    /**
     * Creates a new game result record in the database.
     *
     * @param r The GameResult object to insert.
     * @return The generated ID of the inserted result.
     */
    long create(GameResult r);

    /**
     * Retrieves a list of recent game results.
     *
     * @param limit The maximum number of results to return.
     * @return A list of the most recent GameResult objects.
     */
    List<GameResult> findRecent(int limit);
}