package brainbrawl.service;

import brainbrawl.dao.ResultDao;
import brainbrawl.model.GameResult;

import java.util.List;

public class ResultService {
    private final ResultDao dao;

    public ResultService(ResultDao dao) { this.dao = dao; }

    public long save(GameResult r) { return dao.create(r); }

    public List<GameResult> recent(int limit) { return dao.findRecent(limit); }
}
