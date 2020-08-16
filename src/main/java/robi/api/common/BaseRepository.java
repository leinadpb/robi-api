package robi.api.common;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.postgresql.util.PGobject;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import robi.api.common.exception.NotFoundException;
import robi.api.common.exception.RepositoryException;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public abstract class BaseRepository<T> {

    private Logger logger = LogManager.getLogger(BaseRepository.class);

    private final JdbcTemplate jdbcTemplate;
    private SimpleJdbcCall jdbcCall;
    private ObjectMapper mapper;

    public BaseRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.jdbcTemplate.setQueryTimeout(20000);
        mapper = new ObjectMapper();
    }

    private SimpleJdbcCall getSimpleJdbcCall(String functionName) {
        return new SimpleJdbcCall(this.jdbcTemplate)
                .withoutProcedureColumnMetaDataAccess()
                .withSchemaName("public")
                .withFunctionName(functionName);
    }

    private Object makeCall(String functionName, Object JSONRequest, String paramName, Integer type) throws NotFoundException {
        Map<String, Object> execute;
        try {
            jdbcCall = getSimpleJdbcCall(functionName);

            if (type == null) {
                jdbcCall.declareParameters(new SqlOutParameter("document", Types.OTHER));
            } else {
                jdbcCall.declareParameters(new SqlOutParameter("document", type));
            }

            if (JSONRequest != null) {
                jdbcCall.declareParameters(new SqlParameter("in_json", Types.OTHER));

                MapSqlParameterSource source = new MapSqlParameterSource();

                String jsonStrRequest;
                if (paramName != null) {
                    jsonStrRequest = (new EntityIDRequest(paramName, JSONRequest)).toString();
                } else {
                    jsonStrRequest = mapper.writeValueAsString(JSONRequest);
                }

                source.addValue("in_json", jsonStrRequest);

                execute = jdbcCall.execute(source);
            } else {
                execute = jdbcCall.execute();
            }

            return execute.get("document");
        } catch (Exception e) {
            if (e instanceof DataIntegrityViolationException && e.getMessage().contains("nothing returned")) {
                return null;
            }
            logger.error(e.getMessage());
            throw new RepositoryException(e.getMessage());
        }
    }

    protected List<T> all(String functionName, Object JSONRequest) throws RepositoryException {
        List<T> results = new ArrayList<>();
        try {
            PGobject jsonArray = (PGobject) makeCall(functionName, JSONRequest, null, null);
            results = getResults(jsonArray);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RepositoryException(e.getMessage());
        }
        return results;
    }

    protected List<T> all(String functionName, Object JSONRequest, String paramName) throws RepositoryException {
        List<T> results = new ArrayList<>();
        try {
            PGobject jsonArray = (PGobject) makeCall(functionName, JSONRequest, paramName, null);
            results = getResults(jsonArray);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RepositoryException(e.getLocalizedMessage());
        }
        return results;
    }

    protected List<T> all(String functionName) throws RepositoryException {
        List<T> results;
       try {
           PGobject jsonArray = (PGobject) makeCall(functionName, null, null, null);
           results = getResults(jsonArray);
       } catch (Exception e) {
           logger.error(e.getMessage());
           throw new RepositoryException(e.getMessage());
       }
       return results;
    }

    private List<T> getResults (PGobject jsonArray) throws JsonProcessingException {
        if (jsonArray == null) return new ArrayList<>();
        return mapper.readValue(jsonArray.getValue(), List.class);
    }

    private Object getResult (PGobject jsonObject) throws JsonProcessingException {
        if (jsonObject == null) return null;
        return mapper.readValue(jsonObject.getValue(), Object.class);
    }

    private Object getResult (PGobject jsonObject, Class responseClass) throws JsonProcessingException {
        if (jsonObject == null) return null;
        return mapper.readValue(jsonObject.getValue(), responseClass);
    }

    protected Object execute(String functionName, Object JSONRequest) {
        Object result;
        try {
            PGobject jsonArray = (PGobject) makeCall(functionName, JSONRequest, null, null);
            if (jsonArray == null) return null;
            result = getResult(jsonArray);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RepositoryException(e.getMessage());
        }
        return result;
    }

    protected Object execute(String functionName, Object JSONRequest, Integer type) {
        try {
            return makeCall(functionName, JSONRequest, null, type);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RepositoryException(e.getMessage());
        }
    }

    protected Object execute(String functionName, Object JSONRequest, String paramName) {
        try {
            PGobject pGobject = (PGobject) makeCall(functionName, JSONRequest, paramName, null);
            if (pGobject == null) return null;
            return getResult(pGobject);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RepositoryException(e.getMessage());
        }
    }

    protected Object execute(String functionName, Object JSONRequest, String paramName, Integer type) {
        try {
            return makeCall(functionName, JSONRequest, paramName, type);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RepositoryException(e.getMessage());
        }
    }

    protected Object execute(Class responseClass, String functionName, Object JSONRequest, String paramName) {
        try {
            PGobject pGobject = (PGobject) makeCall(functionName, JSONRequest, paramName, null);
            if (pGobject == null) return null;
            return getResult(pGobject, responseClass);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RepositoryException(e.getMessage());
        }
    }

    protected Object execute(Class responseClass, String functionName, Object JSONRequest) {
        try {
            PGobject pGobject = (PGobject) makeCall(functionName, JSONRequest, null, null);
            if (pGobject == null) return null;
            return getResult(pGobject, responseClass);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RepositoryException(e.getMessage());
        }
    }
}