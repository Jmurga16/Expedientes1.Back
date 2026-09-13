package com.gestionexpedientes.seed;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.common.StorageSharedKeyCredential;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Carga los datos base (catalogo, workflows y usuarios demo) en la base configurada en MONGODB_DATABASE.
 *
 * Uso:  mvn spring-boot:run -Dspring-boot.run.profiles=seed
 *
 * - Por defecto solo carga colecciones vacias. Con SEED_RESET=true borra y recarga.
 * - Usuarios demo con contrasenas definidas en seed/users.json (datos ficticios).
 * - Los BPMN de los workflows se suben al contenedor workflow-bpmn si no existen.
 */
@Component
@Profile("seed")
public class SeedRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(SeedRunner.class);

    private static final String WORKFLOW_CONTAINER = "workflow-bpmn";

    private final MongoTemplate mongoTemplate;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;
    private final ConfigurableApplicationContext context;

    @Value("${seed.reset:false}")
    private boolean reset;

    @Value("${azure.storage.account-name}")
    private String accountName;

    @Value("${azure.storage.account-key}")
    private String accountKey;

    public SeedRunner(MongoTemplate mongoTemplate, PasswordEncoder passwordEncoder, ObjectMapper objectMapper,
                      ConfigurableApplicationContext context) {
        this.context = context;
        this.mongoTemplate = mongoTemplate;
        this.passwordEncoder = passwordEncoder;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("Seed sobre base '{}' (reset={})", mongoTemplate.getDb().getName(), reset);

        seed("area", "com.gestionexpedientes.area.entity.AreaEntity", load("area"));
        seed("tipologia", "com.gestionexpedientes.tipologia.entity.TipologiaEntity", load("tipologia"));
        seed("subtipologia", "com.gestionexpedientes.subtipologia.entity.SubTipologiaEntity", load("subtipologia"));
        seed("workflow", "com.gestionexpedientes.workflow.entity.WorkflowEntity", workflows());
        seed("users", "com.gestionexpedientes.user.entity.UserEntity", users());

        createIndexes();
        logger.info("Seed finalizado.");
        System.exit(SpringApplication.exit(context, () -> 0));
    }

    private void seed(String collection, String entityClass, List<Map<String, Object>> rows) {
        boolean hasData = mongoTemplate.collectionExists(collection) && mongoTemplate.getCollection(collection).countDocuments() > 0;
        if (hasData && !reset) {
            logger.warn("  {}: ya tiene datos, se omite (usar SEED_RESET=true para recargar)", collection);
            return;
        }
        if (hasData)
            mongoTemplate.dropCollection(collection);

        List<Document> docs = rows.stream()
                .map(row -> new Document(row).append("_class", entityClass))
                .collect(java.util.stream.Collectors.toList());
        mongoTemplate.getCollection(collection).insertMany(docs);
        logger.info("  {}: {} documentos", collection, docs.size());
    }

    private List<Map<String, Object>> workflows() throws Exception {
        List<Map<String, Object>> rows = load("workflow");
        BlobContainerClient container = new BlobContainerClientBuilder()
                .endpoint(String.format("https://%s.blob.core.windows.net/%s", accountName, WORKFLOW_CONTAINER))
                .credential(new StorageSharedKeyCredential(accountName, accountKey))
                .buildClient();
        if (!container.exists())
            container.create();

        for (Map<String, Object> row : rows) {
            String blobName = (String) row.remove("bpmnBlob");
            BlobClient blob = container.getBlobClient(blobName);
            if (!blob.exists()) {
                ClassPathResource file = new ClassPathResource("seed/bpmn/" + blobName);
                try (InputStream in = file.getInputStream()) {
                    blob.upload(in, file.contentLength());
                }
                logger.info("  bpmn subido: {}", blobName);
            }
            row.put("bpmn", blob.getBlobUrl());
        }
        return rows;
    }

    private List<Map<String, Object>> users() throws Exception {
        List<Map<String, Object>> rows = load("users");
        rows.forEach(row -> {
            row.put("username", row.get("email"));
            row.put("password", passwordEncoder.encode((String) row.get("password")));
        });
        return rows;
    }

    private void createIndexes() {
        unique("area", "nombre");
        unique("tipologia", "nombre");
        unique("subtipologia", "nombre");
        unique("workflow", "nombre");
        mongoTemplate.indexOps("workflow").ensureIndex(new Index()
                .on("idTipoDemanda", Sort.Direction.ASC).on("idTipologia", Sort.Direction.ASC)
                .on("idSubtipologia", Sort.Direction.ASC).unique());
        unique("users", "email");
        unique("users", "dni");
        unique("demanda", "caratula");
        mongoTemplate.indexOps("demanda").ensureIndex(new Index().on("idUsuario", Sort.Direction.ASC));
        mongoTemplate.indexOps("historial_demanda").ensureIndex(new Index().on("idDemanda", Sort.Direction.ASC));
        logger.info("  indices creados");
    }

    private void unique(String collection, String field) {
        mongoTemplate.indexOps(collection).ensureIndex(new Index().on(field, Sort.Direction.ASC).unique());
    }

    private List<Map<String, Object>> load(String name) throws Exception {
        try (InputStream in = new ClassPathResource("seed/" + name + ".json").getInputStream()) {
            return objectMapper.readValue(in, new TypeReference<List<Map<String, Object>>>() {});
        }
    }
}
