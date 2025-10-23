package brainbrawl.service;

import brainbrawl.dao.ResultDao;
import brainbrawl.model.GameResult;

import java.util.List;

/**
 * Handles logic related to quiz result management.
 * <p>
 * Provides methods for saving completed game results and
 * retrieving recent player results.
 */
public class ResultService {
    private final ResultDao dao;

    /**
     * Constructs a new {@code ResultService}.
     *
     * @param dao the data access object responsible for result persistence
     */
    public ResultService(ResultDao dao) { this.dao = dao; }

    /**
     * Saves a completed game result to the database.
     *
     * @param r the {@link GameResult} to save
     * @return the generated database ID of the saved result
     */
    public long save(GameResult r) { return dao.create(r); }

    /**
     * Retrieves a list of the most recent results.
     *
     * @param limit the maximum number of results to return
     * @return a list of recent {@link GameResult} objects
     */
    public List<GameResult> recent(int limit) { return dao.findRecent(limit); }
}