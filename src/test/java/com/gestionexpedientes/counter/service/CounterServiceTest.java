package com.gestionexpedientes.counter.service;

import com.gestionexpedientes.counter.entity.CounterEntity;
import org.bson.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CounterServiceTest {

    @Mock private MongoTemplate mongoTemplate;

    @InjectMocks private CounterService counterService;

    @Test
    @DisplayName("nextId arranca desde el maximo id existente en la coleccion")
    void arrancaDesdeElMaximoExistente() {
        when(mongoTemplate.exists(any(Query.class), eq(CounterEntity.class))).thenReturn(false);
        when(mongoTemplate.findOne(any(Query.class), eq(Document.class), eq("demanda")))
                .thenReturn(new Document("_id", 17));
        when(mongoTemplate.findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class), eq(CounterEntity.class)))
                .thenReturn(new CounterEntity("id-demanda", 18));

        int id = counterService.nextId("demanda");

        ArgumentCaptor<CounterEntity> captor = ArgumentCaptor.forClass(CounterEntity.class);
        verify(mongoTemplate).insert(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo("id-demanda");
        assertThat(captor.getValue().getSeq()).isEqualTo(17);
        assertThat(id).isEqualTo(18);
    }

    @Test
    @DisplayName("Con el contador ya creado nextId solo incrementa")
    void noReinicializaElContador() {
        when(mongoTemplate.exists(any(Query.class), eq(CounterEntity.class))).thenReturn(true);
        when(mongoTemplate.findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class), eq(CounterEntity.class)))
                .thenReturn(new CounterEntity("id-demanda", 19));

        assertThat(counterService.nextId("demanda")).isEqualTo(19);
        verify(mongoTemplate, never()).insert(any(CounterEntity.class));
    }
}
