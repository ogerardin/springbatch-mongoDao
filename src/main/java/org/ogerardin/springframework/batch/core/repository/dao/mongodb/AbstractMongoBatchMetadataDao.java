package org.ogerardin.springframework.batch.core.repository.dao.mongodb;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteConcern;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Parent class for all DAO used to store SpringBatch Infrastructure data to Mongo DB.
 *
 * @author vfouzdar
 * @author Baruch S.
 * @author ogerardin
 */
public abstract class AbstractMongoBatchMetadataDao {

    private static final String COLLECTION_PREFIX = "batch_";

    static final String VERSION_KEY = "version";
    static final String START_TIME_KEY = "startTime";
    static final String END_TIME_KEY = "endTime";
    static final String EXIT_CODE_KEY = "exitCode";
    static final String EXIT_MESSAGE_KEY = "exitMessage";
    static final String LAST_UPDATED_KEY = "lastUpdated";
    static final String STATUS_KEY = "status";

    // Dot escaping
    static final String DOT_ESCAPE_STRING = "\\{dot\\}";
    static final String DOT_STRING = "\\.";

    // Job Constants    
    static final String JOB_NAME_KEY = "jobName";
    static final String JOB_INSTANCE_ID_KEY = "jobInstanceId";
    static final String JOB_KEY_KEY = "jobKey";
    static final String JOB_PARAMETERS_KEY = "jobParameters";

    // Job Execution Constants
    static final String JOB_EXECUTION_ID_KEY = "jobExecutionId";
    static final String CREATE_TIME_KEY = "createTime";

    // Job Execution Contexts Constants
    static final String STEP_EXECUTION_ID_KEY = "stepExecutionId";
    static final String TYPE_SUFFIX = "_TYPE";

    // Step Execution Constants
    static final String STEP_NAME_KEY = "stepName";
    static final String COMMIT_COUNT_KEY = "commitCount";
    static final String READ_COUNT_KEY = "readCount";
    static final String FILTER_COUT_KEY = "filterCout";
    static final String WRITE_COUNT_KEY = "writeCount";
    static final String READ_SKIP_COUNT_KEY = "readSkipCount";
    static final String WRITE_SKIP_COUNT_KEY = "writeSkipCount";
    static final String PROCESS_SKIP_COUT_KEY = "processSkipCout";
    static final String ROLLBACK_COUNT_KEY = "rollbackCount";

    // Sequences
    private static final String SEQUENCES_COLLECTION_NAME = "Sequences";
    private static final String ID_KEY = "_id";
    private static final String NS_KEY = "_ns";


    protected final MongoTemplate mongoTemplate;

    private final String collectionName;


    public static String collectionName(Class clazz) {
        return collectionName(clazz.getSimpleName());
    }

    public static String collectionName(String baseName) {
        return COLLECTION_PREFIX + baseName;
    }

    protected AbstractMongoBatchMetadataDao(MongoTemplate mongoTemplate, String collectionName) {
        this.mongoTemplate = mongoTemplate;
        this.collectionName = collectionName;
    }

    protected AbstractMongoBatchMetadataDao(MongoTemplate mongoTemplate, Class clazz) {
        this(mongoTemplate, collectionName(clazz));
    }

    DBCollection getCollection(){
        DBCollection collection = mongoTemplate.getCollection(collectionName);
        collection.setWriteConcern(WriteConcern.ACKNOWLEDGED);
        return collection;
    }

    Long getNextId(String name, MongoTemplate mongoTemplate) {
        DBCollection collection = mongoTemplate.getDb().getCollection(collectionName(SEQUENCES_COLLECTION_NAME));
        BasicDBObject sequence = new BasicDBObject("name", name);
        collection.update(sequence, new BasicDBObject("$inc", new BasicDBObject("value", 1L)), true, false);
        return (Long) collection.findOne(sequence).get("value");
    }

    void removeSystemFields(DBObject dbObject) {
        if (dbObject == null) {
            return;
        }
        dbObject.removeField(ID_KEY);
        dbObject.removeField(NS_KEY);
    }

    static BasicDBObject jobInstanceIdObj(Long id) {
        return new BasicDBObject(JOB_INSTANCE_ID_KEY, id);
    }

    static BasicDBObject jobExecutionIdObj(Long id) {
        return new BasicDBObject(JOB_EXECUTION_ID_KEY, id);
    }

}
