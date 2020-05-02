package edu.uky.cs405g.sample.database;

// Used with permission from Dr. Bumgardner

import org.apache.commons.dbcp2.*;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DBEngine {
    private DataSource ds;
    public boolean isInit = false;
    public DBEngine(String host, String database, String login, 
		String password) {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            String dbConnectionString = null;
            if(database == null) {
                dbConnectionString ="jdbc:mysql://" + host + "?" 
					+"user=" + login  +"&password=" + password 
					+"&useUnicode=true&useJDBCCompliantTimezoneShift=true"
					+"&useLegacyDatetimeCode=false&serverTimezone=UTC"; 
			} else {
                dbConnectionString ="jdbc:mysql://" + host + "/" + database
				+ "?" + "user=" + login  +"&password=" + password 
				+ "&useUnicode=true&useJDBCCompliantTimezoneShift=true"
				+ "&useLegacyDatetimeCode=false&serverTimezone=UTC";
            }
            ds = setupDataSource(dbConnectionString);
            isInit = true;
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    } // DBEngine()

    public static DataSource setupDataSource(String connectURI) {
        //
        // First, we'll create a ConnectionFactory that the
        // pool will use to create Connections.
        // We'll use the DriverManagerConnectionFactory,
        // using the connect string passed in the command line
        // arguments.
        //
        ConnectionFactory connectionFactory = null;
            connectionFactory = 
				new DriverManagerConnectionFactory(connectURI, null);
        //
        // Next we'll create the PoolableConnectionFactory, which wraps
        // the "real" Connections created by the ConnectionFactory with
        // the classes that implement the pooling functionality.
        //
        PoolableConnectionFactory poolableConnectionFactory =
                new PoolableConnectionFactory(connectionFactory, null);

        //
        // Now we'll need a ObjectPool that serves as the
        // actual pool of connections.
        //
        // We'll use a GenericObjectPool instance, although
        // any ObjectPool implementation will suffice.
        //
        ObjectPool<PoolableConnection> connectionPool =
                new GenericObjectPool<>(poolableConnectionFactory);

        // Set the factory's pool property to the owning pool
        poolableConnectionFactory.setPool(connectionPool);

        //
        // Finally, we create the PoolingDriver itself,
        // passing in the object pool we created.
        //
        PoolingDataSource<PoolableConnection> dataSource =
                new PoolingDataSource<>(connectionPool);

        return dataSource;
    } // setupDataSource()

    public Map<String,String> getUsers() {
        Map<String,String> userIdMap = new HashMap<>();

        PreparedStatement stmt = null;
        try
        {
            Connection conn = ds.getConnection();
            String queryString = null;
            queryString = "SELECT * FROM Identity";
            stmt = conn.prepareStatement(queryString);
			// No parameters, so no binding needed.
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String userId = Integer.toString(rs.getInt("idnum"));
                String userName = rs.getString("handle");
                userIdMap.put(userId, userName);
            }
            rs.close();
            stmt.close();
            conn.close();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return userIdMap;
    } // getUsers()

    public Map<String,String> getBDATE(String idnum) {
        Map<String,String> userIdMap = new HashMap<>();

        PreparedStatement stmt = null;
        Integer id = Integer.parseInt(idnum);
        try
        {
            Connection conn = ds.getConnection();
            String queryString = null;
// Here is a statement, but we want a prepared statement.
//            queryString = "SELECT bdate FROM Identity WHERE idnum = "+id;
//            
            queryString = "SELECT bdate FROM Identity WHERE idnum = ?";
// ? is a parameter placeholder
            stmt = conn.prepareStatement(queryString);
			stmt.setInt(1,id);
// 1 here is to denote the first parameter.
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String bdate = rs.getString("bdate");
                userIdMap.put("bdate", bdate);
            }
            rs.close();
            stmt.close();
            conn.close();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return userIdMap;
    } // getBDATE()

Public Map<String, String> timeline(String handle, String password, String newest, String oldest, String tstamp){
Map<String, String> userIdMap = new HashMap<>();
PreparedStatement stmt = null;
try{
Connection conn = ds.getConnection();
string queryString = null;
queryString = "SELECT FROM Story VALUES(Identity.idnum, Story.idnum, ?) WHERE tstamp IS GREATER THAN ? AND LESS THAN ?";
stmt = conn.PreparedStatement(queryString);
stmt.setString(1, tstamp);
stmt.setString(2, oldest);
stmt.setString(3, newest);
ResultSet rs = stmt.executeQuery();
while (rs.next()){
                String idnum = Integer.toString(rs.getInt("idnum"));
                userIdMap.put("idnum", idnum);
            }
 String idnum = userIdMap.get("idnum");
            stmt.executeQuery();

rs.close()
smt.close();
conn.close();
}
catch(Exception ex){
	ex.printStackTrace();

}
return userIdMap;
}

    public Map<String,String> block(String handle, String password){
        Map<String, String> userIdMap = new HashMap<>();
        PreparedStatement stmt = null;
        try{
            Connection conn = ds.getConnection();
            String queryString = null;
            queryString = "SELECT * FROM Identity WHERE handle = ? AND password = ?";
            stmt = conn.prepareStatement(queryString);
            stmt.setString(1, handle);
            stmt.setString(2, password);
            stmt.executeQuery(queryString);
            queryString = "INSERT INTO Block VALUES(?, ?)";
            ResultSet rs = stmt.executeQuery(queryString);
            String username = rs.getString("handle");
            password = rs.getString("password");
            userIdMap.put("handle", username);
            userIdMap.put("password", password);
            while(rs.next()){
                String idnum = Integer.toString(rs.getInt("idnum"));
                userIdMap.put("idnum", idnum);
            }
            rs.close();
            stmt.close();
            conn.close();
        }
        catch(Exception ex){
	        ex.printStackTrace();

        }
        return userIdMap;
    }



    public Map<String,String> unfollow(String handle, String password){
        Map<String, String> userIdMap = new HashMap<>();
        PreparedStatement stmt = null;
        try{
            Connection conn = ds.getConnection();
            String queryString = null;
            queryString = "SELECT * FROM Identity WHERE handle = ? AND password = ?";
            stmt = conn.prepareStatement(queryString);
            stmt.setString(1, handle);
            stmt.setString(2, password);
            stmt.executeQuery(queryString);
            queryString = "REMOVE FROM Follows VALUES(?, ?)";
            ResultSet rs = stmt.executeQuery(queryString);
            String username = rs.getString("handle");
            password = rs.getString("password");
            userIdMap.put("handle", username);
            userIdMap.put("password", password);
            while(rs.next()){
                String idnum = Integer.toString(rs.getInt("idnum"));
                userIdMap.put("idnum", idnum);
            }
            rs.close();
            stmt.close();
            conn.close();
        }
        catch(Exception ex){
            ex.printStackTrace();

        }
        return userIdMap;
    }

    public Map<String,String> follow(String handle, String password){
        Map<String, String> userIdMap = new HashMap<>();
        PreparedStatement stmt = null;
        try{
            Connection conn = ds.getConnection();
            String queryString = null;
            queryString = "SELECT * FROM Identity WHERE handle = ? AND password = ?";
            stmt = conn.prepareStatement(queryString);
            stmt.setString(1, handle);
            stmt.setString(2, password);
            stmt.executeQuery(queryString);
            queryString = "INSERT INTO Follows VALUES(?, ?)";
            ResultSet rs = stmt.executeQuery(queryString);
            String username = rs.getString("handle");
            password = rs.getString("password");
            userIdMap.put("handle", username);
            userIdMap.put("password", password);
            while(rs.next()){
                String idnum = Integer.toString(rs.getInt("idnum"));
                userIdMap.put("idnum", idnum);
            }
            rs.close();
            stmt.close();
            conn.close();
        }
        catch(Exception ex){
            ex.printStackTrace();

        }
        return userIdMap;
    }

 //   Input: curl -d '{"handle":"@cooldude42", "password":"mysecret!", "fullname":"Angus Mize", "location":"Kentucky", "xmail":"none@nowhere.com", "bdate":"1970-07-01"}'
    public Map<String, String> createuser(String handle, String password, String fullname, String location, String xmail, String bdate, String joined) {
        Map<String, String> userIdMap = new HashMap<>();

        PreparedStatement stmt = null;
        try
        {
            Connection conn = ds.getConnection();
            String queryString = null;
            //queryString to insert into the database
            queryString = "INSERT INTO Identity VALUES(?, ?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(queryString);
            stmt.setString(1, handle);
            stmt.setString(2, password);
            stmt.setString(3, fullname);
            stmt.setString(4, location);
            stmt.setString(5, xmail);
            stmt.setString(6, bdate);
            stmt.setString(7, joined);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                String idnum = Integer.toString(rs.getInt("idnum"));
                userIdMap.put("idnum", idnum);
            }
            rs.close();
            stmt.close();
            conn.close();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return userIdMap;
    }

    public Map<String, String> seeUser(String handle, String password) {
        Map<String, String> userIdMap = new HashMap<>();

        PreparedStatement stmt = null;
        try {
            Connection conn = ds.getConnection();
            String queryString = null;
            //query string to get user information from database
            queryString = "SELECT * from Identity WHERE handle = ? AND password = ?";
            stmt = conn.prepareStatement(queryString);
            stmt.setString(1, handle);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String idnum = Integer.toString(rs.getInt("idnum"));
                String username = rs.getString("handle");
                String fullname = rs.getString("fullname");
                String location = rs.getString("location");
                String xmail = rs.getString("xmail");
                String bdate = rs.getString("bdate");
                String joined = rs.getString("joined");
                userIdMap.put("idnum", idnum);
                userIdMap.put("handle", username);
                userIdMap.put("fullname", fullname);
                userIdMap.put("location", location);
                userIdMap.put("xmail", xmail);
                userIdMap.put("bdate", bdate);
                userIdMap.put("joined", joined);
            }
            rs.close();
            stmt.close();
            conn.close();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
        return userIdMap;
    }


    public Map<String, String> poststory(String handle, String password, String chapter, String url, String expires, String tstamp) {
        Map<String, String> userIdMap = new HashMap<>();

        PreparedStatement stmt = null;
        try {
            Connection conn = ds.getConnection();
            String queryString = null;
            //query string to post a story for the user
            queryString = "SELECT idnum FROM Identity WHERE handle = ? AND password = ?";
            stmt = conn.prepareStatement(queryString);
            stmt.setString(1, handle);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                String idnum = Integer.toString(rs.getInt("idnum"));
                userIdMap.put("idnum", idnum);
            }
            String idnum = userIdMap.get("idnum");
            stmt.close();
            rs.close();
            queryString = "INSERT INTO Story VALUES(?, ?, ?, ?, ?) WHERE ? ";
            stmt = conn.prepareStatement(queryString);
            stmt.setString(1, idnum);
            stmt.setString(2, chapter);
            stmt.setString(3, url);
            stmt.setString(4, expires);
            stmt.setString(5, tstamp);
            stmt.executeQuery();
            stmt.close();
            conn.close();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
        return userIdMap;
    }

    public Map<String, String> reprint(String handle, String password, Boolean like, String tstamp) {
        Map<String, String> userIdMap = new HashMap<>();

        PreparedStatement stmt = null;
        try {
            Connection conn = ds.getConnection();
            String queryString = null;
            //query string to post a story for the user
            queryString = "INSERT INTO Reprint VALUES(Identity.idnum, Story.idnum, ?, ? WHERE Identity.handle=?";
            stmt = conn.prepareStatement(queryString);
            stmt.setBoolean(1, like);
            stmt.setString(2, tstamp);
            stmt.setString(3, handle);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                String idnum = Integer.toString(rs.getInt("idnum"));
                userIdMap.put("idnum", idnum);
            }
            String idnum = userIdMap.get("idnum");
            stmt.executeQuery();
            stmt.close();
            conn.close();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
        return userIdMap;
    }

    public Map<String,String> sugUsers() {
        Map<String,String> userIdMap = new HashMap<>();

        PreparedStatement stmt = null;
        try
        {
            Connection conn = ds.getConnection();
            String queryString = null;
            queryString = "SELECT idnum AND handle FROM Identity LEFT JOIN Follows ON Identity.idnum = Follow.idnum WHERE Follow.idnum IS NULL";
            stmt = conn.prepareStatement(queryString);
            // No parameters, so no binding needed.
            ResultSet rs = stmt.executeQuery();
            int i = 0;
            while (rs.next()) {
                    if (i < 4) {
                    String userId = Integer.toString(rs.getInt("idnum"));
                    String userName = rs.getString("handle");
                    i++;
                }
            }
            rs.close();
            stmt.close();
            conn.close();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
        return userIdMap;
    }
} // class DBEngine
