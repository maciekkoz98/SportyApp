package pl.edu.pw.sportyapp.shared;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
public class SequenceGeneratorService {

    @Autowired
    MongoOperations mongoOperations;

    public long generateSequence(String sequenceName) {
        DBSequence counter = mongoOperations.findAndModify(query(where("_id").is(sequenceName)),
                new Update().inc("value", 1),
                options().returnNew(true).upsert(true),
                DBSequence.class);

        return Objects.isNull(counter) ? 1 : counter.getValue();
    }
}
