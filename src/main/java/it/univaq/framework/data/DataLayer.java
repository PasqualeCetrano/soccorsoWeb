package it.univaq.framework.data;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;

public class DataLayer implements AutoCloseable {

    // il datasource contiene i dati per connettersi a MySQL
    private final DataSource datasource;
    private Connection connection;
    // mappa contenente le associazioni tra le classi e i relativi DAO, ovvero per
    // ogni classe vi è un DAO corrispondente
    private final Map<Class, DAO> daos;
    // la cache viene utilizzata per memorizzare gli oggetti Java che vengono
    // recuperati dal DB, in questo modo si evita di accedere continuamente al DB e
    // si migliora le prestazioni
    private final DataCache cache;

    public DataLayer(DataSource datasource) throws SQLException {
        super();
        this.datasource = datasource;
        this.connection = datasource.getConnection();
        this.daos = new HashMap<>();
        this.cache = new DataCache();
    }

    // serve a caricare in memoria tutti i dao per una richiesta appena arrivata,
    // per poi distruggerli liberando la memoria
    public void registerDAO(Class entityClass, DAO dao) throws DataException {
        daos.put(entityClass, dao);
        dao.init();
    }

    public DAO getDAO(Class entityClass) {
        return daos.get(entityClass);
    }

    public void init() throws DataException {
        // call registerDAO for your own DAOs
    }

    public void destroy() {
        try {
            if (connection != null) {
                connection.close();
                connection = null;
            }
        } catch (SQLException ex) {
            //
        }
    }

    public DataSource getDatasource() {
        return datasource;
    }

    public Connection getConnection() {
        return connection;
    }

    public DataCache getCache() {
        return cache;
    }

    // metodo dell'interfaccia AutoCloseable (permette di usare questa classe nei
    // try-with-resources), così da chiamare automaticamente il metodo close
    @Override
    public void close() throws Exception {
        destroy();
    }
}
