package pikater.data.schema;

import java.io.*;
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
        File folder = new File(queryDirectoryPath);
        File[] listOfFiles = folder.listFiles();
        LinkedList<String> toReturn=new LinkedList<>();
        for (int i = 0; i < listOfFiles.length; i++)
        {
            if (listOfFiles[i].isFile())
            {
                String fileName = listOfFiles[i].getName();
                if (fileName.endsWith(".sql"))
                {
                    toReturn.add(fileName.substring(0,fileName.length()-4));
                }
            }
        }
        return  toReturn;
    }
}
