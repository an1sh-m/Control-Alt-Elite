package brainbrawl.dao;

import brainbrawl.model.Question;
import java.util.List;
import java.util.Optional;

public interface QuestionDao {
    long create(Question q);
    Optional<Question> findById(long id);
    List<Question> findAll();
    // teammate will add: update(Question q), deleteById(long id)
}
