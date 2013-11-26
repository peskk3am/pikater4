package pikater.data.schema;

import java.util.List;

/**
 * User: Kuba
 * Date: 26.11.13
 * Time: 11:50
 */
public interface SqlQueryFactory {
    String getCreateQuery(String tableName);
    List<String> getTableNames();
}
