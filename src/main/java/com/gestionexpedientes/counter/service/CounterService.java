package com.gestionexpedientes.counter.service;

import com.gestionexpedientes.counter.entity.CounterEntity;
import org.bson.Document;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
public class CounterService {

    public static final String ID_PREFIX = "id-";

    private final MongoTemplate mongoTemplate;

    public CounterService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public long next(String key) {
        CounterEntity counter = mongoTemplate.findAndModify(
                Query.query(Criteria.where("_id").is(key)),
                new Update().inc("seq", 1),
                FindAndModifyOptions.options().returnNew(true).upsert(true),
                CounterEntity.class);

        return counter != null ? counter.getSeq() : 1;
    }

    public int nextId(String collection) {
        String key = ID_PREFIX + collection;
        if (!mongoTemplate.exists(Query.query(Criteria.where("_id").is(key)), CounterEntity.class))
            startFromMaxId(key, collection);
        return (int) next(key);
    }

    private void startFromMaxId(String key, String collection) {
        Query query = new Query().with(Sort.by(Sort.Direction.DESC, "_id")).limit(1);
        query.fields().include("_id");
        Document last = mongoTemplate.findOne(query, Document.class, collection);
        long maxId = last == null ? 0 : ((Number) last.get("_id")).longValue();
        try {
            mongoTemplate.insert(new CounterEntity(key, maxId));
        } catch (DuplicateKeyException ignored) {
        }
    }
}
