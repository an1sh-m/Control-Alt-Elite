package brainbrawl.service;

import brainbrawl.dao.QuestionDao;
import brainbrawl.model.Question;

import java.util.List;

public class QuestionService {
    private final QuestionDao dao;

    public QuestionService(QuestionDao dao) { this.dao = dao; }

    public long addQuestion(Question q) {
        if (q.getCategory() == null || q.getCategory().isBlank()) throw new IllegalArgumentException("Category required");
        if (q.getText() == null || q.getText().isBlank()) throw new IllegalArgumentException("Question text required");
        if (q.getDifficulty() < 1 || q.getDifficulty() > 5) throw new IllegalArgumentException("Difficulty 1..5");
        switch (q.getType()) {
            case MCQ -> {
                if (q.getOptions() == null || q.getOptions().size() < 2) throw new IllegalArgumentException("At least 2 options");
                if (q.getCorrectIndex() == null || q.getCorrectIndex() < 0 || q.getCorrectIndex() >= q.getOptions().size())
                    throw new IllegalArgumentException("Correct index out of range");
            }
            case SHORT -> { /* no extra checks */ }
        }
        return dao.create(q);
    }

    public List<Question> listAll() { return dao.findAll(); }
}