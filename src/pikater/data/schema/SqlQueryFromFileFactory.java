package pikater.data.schema;

import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * User: Kuba
 * Date: 26.11.13
 * Time: 11:51
 */
public class SqlQueryFromFileFactory implements SqlQueryFactory {
    private final String queryDirectoryPath;

    public SqlQueryFromFileFactory(String queryDirectoryPath) {
        this.queryDirectoryPath = queryDirectoryPath;
    }

    @Override
    public String getCreateQuery(String tableName) {
        String filePath = queryDirectoryPath+"/"+tableName+".sql";
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append('\n');
                line = br.readLine();
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    @Override
    public List<String> getTableNames() {
        return new LinkedList<>(Arrays.asList(
                "users",
                "global_metadata",
                "datasets",
                "attribute_metadata",
                "attribute_numerical_metadata",
                "attribute_categorical_metadata",
                "datasets_atrributes_mapping"
        ));
    }
}
