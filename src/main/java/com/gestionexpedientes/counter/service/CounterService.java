package com.gestionexpedientes.counter.service;

import com.gestionexpedientes.counter.entity.CounterEntity;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
public class CounterService {

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
}
