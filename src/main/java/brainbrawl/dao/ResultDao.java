package brainbrawl.dao;

import brainbrawl.model.GameResult;
import java.util.List;

public interface ResultDao {
    long create(GameResult r);                 // returns generated id
    List<GameResult> findRecent(int limit);    // newest first
}
