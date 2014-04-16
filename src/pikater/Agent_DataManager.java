package pikater;

import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.content.onto.basic.Result;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;
import jade.util.leap.ArrayList;
import jade.util.leap.Iterator;
import jade.util.leap.List;
import org.apache.commons.codec.digest.DigestUtils;
import pikater.agents.PikaterAgent;
import pikater.data.ConnectionProvider;
import pikater.data.schema.SqlQueryFactory;
import pikater.logging.Severity;
import pikater.ontology.messages.*;

import java.io.*;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.regex.Pattern;

public class Agent_DataManager extends PikaterAgent {
    private final String DEFAULT_CONNECTION_PROVIDER="defaultConnection";
    private final String QUERY_FACTORY_BEAN="queryFactory";
    private static final String CONNECTION_ARG_NAME="connection";
    private String connectionBean;
    private ConnectionProvider connectionProvider;
    private SqlQueryFactory sqlQueryFactory;
    private static final long serialVersionUID = 1L;
    Connection db;

    protected void CreateTablesIfNotInDB(java.util.List<String> tableNames)
    {
        LinkedList<String> tableNamesInDB = new LinkedList<>();
        String[] types = {"TABLE", "VIEW"};
        ResultSet tables;
        try {
            tables = db.getMetaData().getTables(null, connectionProvider.getSchema(), "%" ,types);
            while (tables.next()) {
                tableNamesInDB.add(tables.getString(3).toUpperCase());
            }
            for (String tableName:tableNames)
            {
                if (!tableNamesInDB.contains(tableName.toUpperCase()))
                {
                    log("Creating table "+tableName);
                    String createQuery=sqlQueryFactory.getCreateQuery(tableName);
                    db.createStatement().executeUpdate(createQuery);
                }
            }
        } catch (SQLException e) {
            logError("Error creating tables " + e.getMessage(),Severity.Critical);
        }
    }

    @Override
    protected void setup() {
        try {
    		initDefault();
    		registerWithDF();

            if (containsArgument(CONNECTION_ARG_NAME))
            {
                connectionBean=getArgumentValue(CONNECTION_ARG_NAME);
            }
            else
            {
                connectionBean=DEFAULT_CONNECTION_PROVIDER;
            }
            connectionProvider=(ConnectionProvider)context.getBean(connectionBean);
            sqlQueryFactory=(SqlQueryFactory)context.getBean(QUERY_FACTORY_BEAN);
        	
    		log("Connecting to " + connectionProvider.getConnectionInfo() + ".");
    		openDBConnection();
            java.util.List<String> tableNames=sqlQueryFactory.getTableNames();
            CreateTablesIfNotInDB(tableNames);

        } catch (Exception e) {
            e.printStackTrace();
        }

        LinkedList<String> tableNames = new LinkedList<>();
        LinkedList<String> triggerNames = new LinkedList<>();
        try {
            String[] types = {"TABLE", "VIEW"};
            ResultSet tables = db.getMetaData().getTables(null, connectionProvider.getSchema(), "%" ,types);
            while (tables.next()) {
                tableNames.add(tables.getString(3).toUpperCase());
            }

            ResultSet triggers = db.createStatement().executeQuery(
                    "SELECT trigger_name FROM INFORMATION_SCHEMA.TRIGGERS");
            while (triggers.next()) {
                triggerNames.add(triggers.getString("trigger_name"));
            }

        } catch (SQLException e) {
            logError("Error getting tables list: " + e.getMessage());
            e.printStackTrace();
        }

        StringBuilder sb=new StringBuilder("Found the following tables: ");
        for (String s : tableNames) {
            sb.append(s+" ");
        }
        log(sb.toString());

        sb=new StringBuilder("Found the following triggers: ");
        for (String s : triggerNames) {
            sb.append(s+" ");
        }
        log(sb.toString());

        File data = new File("data" + System.getProperty("file.separator") + "files" + System.getProperty("file.separator") + "temp");
        if (!data.exists()) {
            log("Creating directory data/files");
            if (data.mkdirs()) {
                log("Succesfully created directory data/files");
            } else {
                logError("Error creating directory data/files");
            }
        }
        
        try {
			db.close();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchOntology(ontology.getName()), MessageTemplate.MatchPerformative(ACLMessage.REQUEST));

        addBehaviour(new AchieveREResponder(this, mt) {

            private static final long serialVersionUID = 1L;

            @Override
            protected ACLMessage handleRequest(ACLMessage request)
                    throws NotUnderstoodException, RefuseException {
                try {
                    Action a = (Action) getContentManager().extractContent(
                            request);

                    if (a.getAction() instanceof ImportFile) {
                        return RespondToImportFile(request, a);
                    }
                    if (a.getAction() instanceof TranslateFilename) {

                        return RespondToTranslateFilename(request, a);

                    }
                    if (a.getAction() instanceof SaveResults) {
                        return RespondToSaveResults(request, a);
                    }
                    if (a.getAction() instanceof SaveMetadata) {

                        return RespondToGetAclMessage(request, a);
                    }
                    
                    if (a.getAction() instanceof GetMetadata) {
                        return ReplyToGetMetadata(request, a);
                    }
                    
                    if (a.getAction() instanceof GetAllMetadata) {
                        return RespondToGetAllMetadata(request, a);
                    }

                    if (a.getAction() instanceof GetTheBestAgent) {
                        return RespondToGetTheBestAgent(request, a);
                    }
                    if (a.getAction() instanceof GetFileInfo) {

                        return RespondToGetFileInfo(request, a);
                    }

                    if (a.getAction() instanceof UpdateMetadata) {

                        return ReplyToUpdateMetadata(request, a);
                    }
                    if (a.getAction() instanceof GetFiles) {

                        return RespondToGetFiles(request, a);
                    }

                    if (a.getAction() instanceof LoadResults) {

                        return RespondToLoadResults(request, a);
                    }

                    if (a.getAction() instanceof DeleteTempFiles) {

                        return RespondToDeleteTempFiles(request);
                    }
                    
                    if (a.getAction() instanceof ShutdownDatabase) {
                        return RespondToShutdownDatabase(request);
                    }                    
                    
                } catch (OntologyException e) {
                    e.printStackTrace();
                    logError("Problem extracting content: " + e.getMessage());
                } catch (CodecException e) {
                    e.printStackTrace();
                    logError("Codec problem: " + e.getMessage());
                } catch (SQLException e) {
                    e.printStackTrace();
                    logError("SQL error: " + e.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                ACLMessage failure = request.createReply();
                failure.setPerformative(ACLMessage.FAILURE);
                logError("Failure responding to request: " + request.getContent());
                return failure;
            }
        });

    }

    /************************************************************************************************
     * Obsolete methods
     *
     */
    private ACLMessage RespondToImportFile(ACLMessage request, Action a) throws IOException, CodecException, OntologyException, SQLException, ClassNotFoundException {
        ImportFile im = (ImportFile) a.getAction();

        String pathPrefix = System.getProperty("user.dir") + System.getProperty("file.separator") + "data" + System.getProperty("file.separator") + "files" + System.getProperty("file.separator") + "temp" + System.getProperty("file.separator");

        if (im.isTempFile()) {

            FileWriter fw = new FileWriter(pathPrefix + im.getExternalFilename());
            fw.write(im.getFileContent());
            fw.close();

            ACLMessage reply = request.createReply();
            reply.setPerformative(ACLMessage.INFORM);

            Result r = new Result(im, pathPrefix + im.getExternalFilename());
            getContentManager().fillContent(reply, r);

            return reply;

        }

        if (im.getFileContent() == null) {
            String path = System.getProperty("user.dir") + System.getProperty("file.separator");
            path += "incoming" + System.getProperty("file.separator") + im.getExternalFilename();

            String internalFilename = md5(path);

            emptyMetadataToDB(internalFilename, im.getExternalFilename());

            File f = new File(path);

            openDBConnection();
            Statement stmt = db.createStatement();
            String query = "SELECT COUNT(*) AS num FROM filemapping WHERE internalFilename = \'" + internalFilename + "\'";
            log("Executing query " + query);

            ResultSet rs = stmt.executeQuery(query);

            rs.next();
            int count = rs.getInt("num");

            stmt.close();

            if (count > 0) {
                f.delete();
                log("File " + internalFilename + " already present in the database");
            } else {

                stmt = db.createStatement();

                query = "INSERT into filemapping (userId, externalFilename, internalFilename) VALUES (" + im.getUserID() + ",\'" + im.getExternalFilename() + "\',\'" + internalFilename + "\')";
                log("Executing query: " + query);

                stmt.executeUpdate(query);

                stmt.close();

                // move the file to db\files directory
                String newName = System.getProperty("user.dir") + System.getProperty("file.separator") + "data" + System.getProperty("file.separator") + "files" + System.getProperty("file.separator") + internalFilename;
                move(f, new File(newName));

            }

            ACLMessage reply = request.createReply();
            reply.setPerformative(ACLMessage.INFORM);

            Result r = new Result(im, internalFilename);
            getContentManager().fillContent(reply, r);

            db.close();
            return reply;
        } else {

            String fileContent = im.getFileContent();
            String fileName = im.getExternalFilename();
            String internalFilename = DigestUtils.md5Hex(fileContent);

            emptyMetadataToDB(internalFilename, fileName);

            openDBConnection();
            Statement stmt = db.createStatement();
            String query = "SELECT COUNT(*) AS num FROM filemapping WHERE internalFilename = \'" + internalFilename + "\'";
            log("Executing query " + query);

            ResultSet rs = stmt.executeQuery(query);

            rs.next();
            int count = rs.getInt("num");

            stmt.close();

            if (count > 0) {
                log("File " + internalFilename + " already present in the database");
            } else {

                stmt = db.createStatement();

                log("Executing query: " + query);
                query = "INSERT into filemapping (userId, externalFilename, internalFilename) VALUES (" + im.getUserID() + ",\'" + im.getExternalFilename() + "\',\'" + internalFilename + "\')";

                stmt.executeUpdate(query);
                stmt.close();

                String newName = System.getProperty("user.dir") + System.getProperty("file.separator") + "data" + System.getProperty("file.separator") + "files" + System.getProperty("file.separator") + internalFilename;

                FileWriter file = new FileWriter(newName);
                file.write(fileContent);
                file.close();

                log("Created file: " + newName);
            }

            ACLMessage reply = request.createReply();
            reply.setPerformative(ACLMessage.INFORM);

            Result r = new Result(im, internalFilename);
            getContentManager().fillContent(reply, r);

            db.close();
            return reply;
        }
    }

    private ACLMessage RespondToTranslateFilename(ACLMessage request, Action a) throws SQLException, ClassNotFoundException, CodecException, OntologyException {
        TranslateFilename tf = (TranslateFilename) a.getAction();

        openDBConnection();
        Statement stmt = db.createStatement();

        String query;
        if (tf.getInternalFilename() == null) {
            query = "SELECT internalFilename AS filename FROM filemapping WHERE userID=" + tf.getUserID() + " AND externalFilename=\'" + tf.getExternalFilename() + "\'";
        } else {
            query = "SELECT externalFilename AS filename FROM filemapping WHERE userID=" + tf.getUserID() + " AND internalFilename=\'" + tf.getInternalFilename() + "\'";
        }

        log("Executing query: " + query);

        ResultSet rs = stmt.executeQuery(query);


        String internalFilename = "error";

        if (rs.next()) { // should return single line (or none,
            // if file does not exist)
            internalFilename = rs.getString("filename");

        }
        else
        {
            String pathPrefix = System.getProperty("user.dir") + System.getProperty("file.separator") + "data" + System.getProperty("file.separator") + "files" + System.getProperty("file.separator") + "temp" + System.getProperty("file.separator");

            String tempFileName = pathPrefix + tf.getExternalFilename();
            if (new File(tempFileName).exists())
                internalFilename = "temp" + System.getProperty("file.separator") + tf.getExternalFilename();
        }

        ACLMessage reply = request.createReply();
        reply.setPerformative(ACLMessage.INFORM);

        Result r = new Result(tf, internalFilename);
        getContentManager().fillContent(reply, r);

        db.close();
        return reply;
    }

    private ACLMessage RespondToSaveResults(ACLMessage request, Action a) throws SQLException, ClassNotFoundException {
        SaveResults sr = (SaveResults) a.getAction();
        Task res = sr.getTask();

        if (!(new File("studentMode").exists()) ) {

            openDBConnection();
            Statement stmt = db.createStatement();

            String query = "INSERT INTO results (userID, agentName, agentType, options, dataFile, testFile,"
                    + "errorRate, kappaStatistic, meanAbsoluteError, rootMeanSquaredError, relativeAbsoluteError,"
                    + "rootRelativeSquaredError, start, finish, duration, durationLR, objectFilename, experimentID, experimentName, note) VALUES ( 1, ";
            query += "\'" + res.getAgent().getName() + "\',";
            query += "\'" + res.getAgent().getType() + "\',";
            query += "\'" + res.getAgent().optionsToString() + "\',";
            query += "\'" + res.getData().removePath(res.getData().getTrain_file_name()) + "\',";
            query += "\'" + res.getData().removePath(res.getData().getTest_file_name()) + "\',";

            float Error_rate = Float.MAX_VALUE;
            float Kappa_statistic = Float.MAX_VALUE;
            float Mean_absolute_error = Float.MAX_VALUE;
            float Root_mean_squared_error = Float.MAX_VALUE;
            float Relative_absolute_error = Float.MAX_VALUE; // percent
            float Root_relative_squared_error = Float.MAX_VALUE; // percent
            int duration = Integer.MAX_VALUE; // miliseconds
            float durationLR = Float.MAX_VALUE;

            Iterator itr = res.getResult().getEvaluations().iterator();
            while (itr.hasNext()) {
                Eval next_eval = (Eval) itr.next();
                if (next_eval.getName().equals("error_rate")){
                    Error_rate = next_eval.getValue();
                }

                if (next_eval.getName().equals("kappa_statistic")){
                    Kappa_statistic = next_eval.getValue();
                }

                if (next_eval.getName().equals("mean_absolute_error")){
                    Mean_absolute_error = next_eval.getValue();
                }

                if (next_eval.getName().equals("root_mean_squared_error")){
                    Root_mean_squared_error = next_eval.getValue();
                }

                if (next_eval.getName().equals("relative_absolute_error")){
                    Relative_absolute_error = next_eval.getValue();
                }

                if (next_eval.getName().equals("root_relative_squared_error")){
                    Root_relative_squared_error = next_eval.getValue();
                }

                if (next_eval.getName().equals("duration")){
                    duration = (int)next_eval.getValue();
                }
                if (next_eval.getName().equals("durationLR")){
                    durationLR = (float)next_eval.getValue();
                }
            }
            String start = getDateTime();
            String finish = getDateTime();
            if (res.getStart() != null){ start = res.getStart(); }
            if (res.getFinish() != null){ finish = res.getFinish(); }
            query += Error_rate + ",";
            query += Kappa_statistic + ",";
            query += Mean_absolute_error + ",";
            query += Root_mean_squared_error + ",";
            query += Relative_absolute_error + ",";
            query += Root_relative_squared_error;

            query += ",";
            query += "\'" + Timestamp.valueOf(start) + "\',";
            query += "\'" + Timestamp.valueOf(finish) + "\',";

            query += "\'" + duration + "\',";
            query += "\'" + durationLR + "\',";

            query += "\'" + res.getResult().getObject_filename() + "\', ";
            query += "\'" + res.getId().getIdentificator() + "\',";  // TODO - pozor - neni jednoznacne, pouze pro jednoho managera
            query += "\'" + res.getProblem_name() + "\',";
            query += "\'" + res.getNote() + "\'";
            query += ")";

            log("Executing query: " + query);

            stmt.executeUpdate(query);
            db.close();
        }

        ACLMessage reply = request.createReply();
        reply.setPerformative(ACLMessage.INFORM);
        return reply;
    }

    private ACLMessage RespondToGetAclMessage(ACLMessage request, Action a) throws SQLException, ClassNotFoundException {
        SaveMetadata saveMetadata = (SaveMetadata) a.getAction();
        Metadata metadata = saveMetadata.getMetadata();

        openDBConnection();
        Statement stmt = db.createStatement();

        String query = "UPDATE metadata SET ";
        query += "numberOfInstances=" + metadata.getNumber_of_instances() + ", ";
        query += "numberOfAttributes=" + metadata.getNumber_of_attributes() + ", ";
        query += "missingValues=" + metadata.getMissing_values();
        if (metadata.getAttribute_type() != null) {
            query += ", attributeType=\'" + metadata.getAttribute_type() + "\' ";
        }
        if (metadata.getDefault_task() != null) {
            query += ", defaultTask=\'" + metadata.getDefault_task() + "\' ";
        }

        // the external file name contains part o the path
        // (db/files/name) -> split and use only the [2] part
        query += " WHERE internalFilename=\'" + metadata.getInternal_name().split(
                Pattern.quote(System.getProperty("file.separator")))[2] + "\'";

        log("Executing query: " + query);

        stmt.executeUpdate(query);

        ACLMessage reply = request.createReply();
        reply.setPerformative(ACLMessage.INFORM);

        db.close();
        return reply;
    }

    private ACLMessage RespondToGetAllMetadata(ACLMessage request, Action a) throws SQLException, ClassNotFoundException, CodecException, OntologyException {
        GetAllMetadata gm = (GetAllMetadata) a.getAction();

        openDBConnection();
        Statement stmt = db.createStatement();

        String query;
        if (gm.getResults_required()){
            query = "SELECT * FROM metadata WHERE EXISTS " +
                    "(SELECT * FROM results WHERE results.dataFile=metadata.internalFilename)";
            if (gm.getExceptions() != null){
                Iterator itr = gm.getExceptions().iterator();
                   while (itr.hasNext()) {
                       Metadata m = (Metadata) itr.next();
                    query += " AND ";
                       query += "internalFilename <> '" + new File(m.getInternal_name()).getName() + "'";
                }
            }
            query += " ORDER BY externalFilename";
        }
        else{
            query = "SELECT * FROM metadata";

            if (gm.getExceptions() != null){
                query += " WHERE ";
                boolean first = true;
                Iterator itr = gm.getExceptions().iterator();
                while (itr.hasNext()) {
                    Metadata m = (Metadata) itr.next();
                    if (!first){
                        query += " AND ";
                    }
                    query += "internalFilename <> '" + new File(m.getInternal_name()).getName() + "'";
                    first = false;
                }

}
            query += " ORDER BY externalFilename";
        }

        List allMetadata = new ArrayList();

        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            Metadata m = new Metadata();
            m.setAttribute_type(rs.getString("attributeType"));
            m.setDefault_task(rs.getString("defaultTask"));
            m.setExternal_name(rs.getString("externalFilename"));
            m.setInternal_name(rs.getString("internalFilename"));
            m.setMissing_values(rs.getBoolean("missingValues"));
            m.setNumber_of_attributes(rs.getInt("numberOfAttributes"));
            m.setNumber_of_instances(rs.getInt("numberOfInstances"));

            allMetadata.add(m);
        }

        log("Executing query: " + query);

        ACLMessage reply = request.createReply();
        reply.setPerformative(ACLMessage.INFORM);

        Result _result = new Result(a.getAction(), allMetadata);
        getContentManager().fillContent(reply, _result);

        db.close();
        return reply;
    }

    private ACLMessage RespondToGetTheBestAgent(ACLMessage request, Action a) throws SQLException, ClassNotFoundException, CodecException, OntologyException {
        GetTheBestAgent g = (GetTheBestAgent) a.getAction();
        String name = g.getNearest_file_name();

        openDBConnection();
        Statement stmt = db.createStatement();

        String query = "SELECT * FROM results " + "WHERE dataFile =\'" + name + "\'" + " AND errorRate = (SELECT MIN(errorRate) FROM results " + "WHERE dataFile =\'" + name + "\')";
        log("Executing query: " + query);

        ResultSet rs = stmt.executeQuery(query);
        if (!rs.isBeforeFirst()) {
            ACLMessage reply = request.createReply();
            reply.setPerformative(ACLMessage.FAILURE);
            reply.setContent("There are no results for this file in the database.");

            db.close();
            return reply;
        }
        rs.next();

        Agent agent = new Agent();
        agent.setName(rs.getString("agentName"));
        agent.setType(rs.getString("agentType"));
        agent.setOptions(agent.stringToOptions(rs.getString("options")));
        agent.setGui_id(rs.getString("errorRate"));

        ACLMessage reply = request.createReply();
        reply.setPerformative(ACLMessage.INFORM);

        Result _result = new Result(a.getAction(), agent);
        getContentManager().fillContent(reply, _result);

        db.close();
        return reply;
    }

    private ACLMessage RespondToGetFileInfo(ACLMessage request, Action a) throws SQLException, ClassNotFoundException, CodecException, OntologyException {
        GetFileInfo gfi = (GetFileInfo) a.getAction();

        String query = "SELECT * FROM filemetadata WHERE " + gfi.toSQLCondition();

        openDBConnection();
        Statement stmt = db.createStatement();

        log("Executing query: " + query);

        ResultSet rs = stmt.executeQuery(query);

        List fileInfos = new ArrayList();

        while (rs.next()) {
            Metadata m = new Metadata();
            m.setAttribute_type(rs.getString("attributeType"));
            m.setDefault_task(rs.getString("defaultTask"));
            m.setExternal_name(rs.getString("externalFilename"));
            m.setInternal_name(rs.getString("internalFilename"));
            m.setMissing_values(rs.getBoolean("missingValues"));
            m.setNumber_of_attributes(rs.getInt("numberOfAttributes"));
            m.setNumber_of_instances(rs.getInt("numberOfInstances"));
            fileInfos.add(m);
        }

        Result r = new Result(a.getAction(), fileInfos);
        ACLMessage reply = request.createReply();
        reply.setPerformative(ACLMessage.INFORM);

        getContentManager().fillContent(reply, r);

        db.close();
        return reply;
    }

    private ACLMessage RespondToGetFiles(ACLMessage request, Action a) throws SQLException, ClassNotFoundException, CodecException, OntologyException {
        GetFiles gf = (GetFiles) a.getAction();

        String query = "SELECT * FROM filemapping WHERE userid = " + gf.getUserID();

        log("Executing query: " + query);

        openDBConnection();
        Statement stmt = db.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        ArrayList files = new ArrayList();

        while (rs.next()) {
            files.add(rs.getString("externalFilename"));
        }

        Result r = new Result(a.getAction(), files);
        ACLMessage reply = request.createReply();
        reply.setPerformative(ACLMessage.INFORM);

        getContentManager().fillContent(reply, r);

        db.close();
        return reply;
    }

    private ACLMessage RespondToShutdownDatabase(ACLMessage request) throws SQLException, ClassNotFoundException {
        String query = "SHUTDOWN";
        log(query);
        openDBConnection();

        // Makes all changes made since the previous commit/rollback permanent
        // and releases any database locks currently held by this Connection object.
        db.commit();

        Statement stmt = db.createStatement();

        stmt.execute(query);

        ACLMessage reply = request.createReply();
        reply.setPerformative(ACLMessage.INFORM);
        return reply;
    }

    private ACLMessage RespondToDeleteTempFiles(ACLMessage request) {
        String path = "data" + System.getProperty("file.separator") + "files" + System.getProperty("file.separator") + "temp" + System.getProperty("file.separator");

        File tempDir = new File(path);
        String[] files = tempDir.list();

        if (files != null) {
            for (String file : files) {
                File d = new File(path + file);
                d.delete();
            }
        }

        ACLMessage reply = request.createReply();
        reply.setPerformative(ACLMessage.INFORM);

        return reply;
    }

    private ACLMessage RespondToLoadResults(ACLMessage request, Action a) throws SQLException, ClassNotFoundException, CodecException, OntologyException {
        LoadResults lr = (LoadResults) a.getAction();

        String query = "SELECT * FROM resultsExternal " + lr.asSQLCondition();
        log(query);

        openDBConnection();
        Statement stmt = db.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        ArrayList results = new ArrayList();

        while (rs.next()) {

            SavedResult sr = new SavedResult();

            sr.setAgentType(rs.getString("agentType"));
            sr.setAgentOptions(rs.getString("options"));
            sr.setTrainFile(rs.getString("trainFileExt"));
            sr.setTestFile(rs.getString("testFileExt"));
            sr.setErrorRate(rs.getDouble("errorRate"));
            sr.setKappaStatistic(rs.getDouble("kappaStatistic"));
            sr.setMeanAbsError(rs.getDouble("meanAbsoluteError"));
            sr.setRMSE(rs.getDouble("rootMeanSquaredError"));
            sr.setRootRelativeSquaredError(rs.getDouble("rootRelativeSquaredError"));
            sr.setRelativeAbsoluteError(rs.getDouble("relativeAbsoluteError"));
            sr.setDate("nodate");

            results.add(sr);
        }

        Result r = new Result(a.getAction(), results);
        ACLMessage reply = request.createReply();
        reply.setPerformative(ACLMessage.INFORM);

        getContentManager().fillContent(reply, r);

        db.close();
        return reply;
    }

    private void openDBConnection() throws SQLException, ClassNotFoundException {
        db=connectionProvider.getConnection();
    }
    private void emptyMetadataToDB(String internalFilename, String externalFilename) throws SQLException, ClassNotFoundException {
    	openDBConnection();
	    Statement stmt = db.createStatement();
	    
	    String query  = "SELECT COUNT(*) AS number FROM metadata WHERE internalFilename = \'" + internalFilename + "\'";
	    String query1 = "SELECT COUNT(*) AS number FROM filemapping WHERE internalFilename = \'" + internalFilename + "\'";
	    
	    log("Executing query " + query);
	    log("Executing query " + query1);
	    
	    ResultSet rs = stmt.executeQuery(query);
	    rs.next();
	    int isInMetadata = rs.getInt("number");
	    
	    ResultSet rs1 = stmt.executeQuery(query1);
		rs1.next();	    
	    int isInFileMapping = rs1.getInt("number");
	
	    if (isInMetadata == 0 && isInFileMapping == 1) {
	        log("Executing query: " + query);
	        query = "INSERT into metadata (externalFilename, internalFilename, defaultTask, " +
	        		"attributeType, numberOfInstances, numberOfAttributes, missingValues)" +
	        		"VALUES (\'" + externalFilename + "\',\'" + internalFilename + "\', null, " +
	        				"null, 0, 0, false)";
	        stmt.executeUpdate(query);       
	    }	        
        // stmt.close();
        db.close();
	}
    private ACLMessage ReplyToGetMetadata(ACLMessage request, Action a) throws SQLException, ClassNotFoundException, CodecException, OntologyException {
        GetMetadata gm = (GetMetadata) a.getAction();

        openDBConnection();
        Statement stmt = db.createStatement();

        String query = "SELECT * FROM metadata WHERE internalfilename = '" + gm.getInternal_filename() +"'";

        Metadata m = new Metadata();

        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            m.setAttribute_type(rs.getString("attributeType"));
            m.setDefault_task(rs.getString("defaultTask"));
            m.setExternal_name(rs.getString("externalFilename"));
            m.setInternal_name(rs.getString("internalFilename"));
            m.setMissing_values(rs.getBoolean("missingValues"));
            m.setNumber_of_attributes(rs.getInt("numberOfAttributes"));
            m.setNumber_of_instances(rs.getInt("numberOfInstances"));
        }

        log("Executing query: " + query);

        ACLMessage reply = request.createReply();
        reply.setPerformative(ACLMessage.INFORM);

        Result _result = new Result(a.getAction(), m);
        getContentManager().fillContent(reply, _result);

        db.close();
        return reply;
    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
        Date date = new Date();
        return dateFormat.format(date);
    }

    private ACLMessage ReplyToUpdateMetadata(ACLMessage request, Action a) throws SQLException, ClassNotFoundException {
        UpdateMetadata updateMetadata = (UpdateMetadata) a.getAction();
        Metadata metadata = updateMetadata.getMetadata();

        openDBConnection();
        Statement stmt = db.createStatement();

        String query = "UPDATE metadata SET ";
        query += "numberOfInstances=" + metadata.getNumber_of_instances() + ", ";
        query += "numberOfAttributes=" + metadata.getNumber_of_attributes() + ", ";
        query += "missingValues=" + metadata.getMissing_values() + "";
        if (metadata.getAttribute_type() != null) {
            query += ", attributeType=\'" + metadata.getAttribute_type() + "\' ";
        }
        if (metadata.getDefault_task() != null) {
            query += ", defaultTask=\'" + metadata.getDefault_task() + "\' ";
        }
        query += " WHERE internalFilename =\'" + metadata.getInternal_name() + "\'";

        log("Executing query: " + query);

        stmt.executeUpdate(query);

        ACLMessage reply = request.createReply();
        reply.setPerformative(ACLMessage.INFORM);

        db.close();
        return reply;
    }


    // Move file (src) to File/directory dest.
    public static synchronized void move(File src, File dest)
            throws FileNotFoundException, IOException {
        copy(src, dest);
        src.delete();
    }

    // Copy file (src) to File/directory dest.
    public static synchronized void copy(File src, File dest)
            throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dest);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    private String md5(String path) {

        StringBuffer sb = null;

        try {
            FileInputStream fs = new FileInputStream(path);
            sb = new StringBuffer();

            int ch;
            while ((ch = fs.read()) != -1) {
                sb.append((char) ch);
            }
            fs.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            logError("File not found: " + path + " -- " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            logError("Error reading file: " + path + " -- " + e.getMessage());
        }

        String md5 = DigestUtils.md5Hex(sb.toString());

        log("MD5 hash of file " + path + " is " + md5);

        return md5;
    }
    /*********************************************************************
     * End of obsolete methods
     */
}