package edu.uky.cs405g.sample.httpcontrollers;
//
// Sample code used with permission from Dr. Bumgardner
//
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import edu.uky.cs405g.sample.Launcher;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.UUID;

@Path("/api")
public class API {

    private Type mapType;
    private Gson gson;

    public API() {
        mapType = new TypeToken<Map<String, String>>() {
        }.getType();
        gson = new Gson();
    }

    //curl http://localhost:9990/api/status
    //{"status_code":1}
    @GET
    @Path("/status")
    @Produces(MediaType.APPLICATION_JSON)
    public Response healthcheck() {
        String responseString = "{\"status_code\":0}";
        try {
            //Here is where you would put your system test, 
			//but this is not required.
            //We just want to make sure your API is up and active/
            //status_code = 0 , API is offline
            //status_code = 1 , API is online
            responseString = "{\"status_code\":1}";
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            ex.printStackTrace();
            return Response.status(500).entity(exceptionAsString).build();
        }
        return Response.ok(responseString)
				.header("Access-Control-Allow-Origin", "*").build();
    } // healthcheck()

    //curl http://localhost:9998/api/listusers
    //{"1":"@paul","2":"@chuck"}
    @GET
    @Path("/listusers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listUsers() {
        String responseString = "{}";
        try {
            Map<String, String> teamMap = Launcher.dbEngine.getUsers();
            responseString = Launcher.gson.toJson(teamMap);
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            ex.printStackTrace();
            return Response.status(500).entity(exceptionAsString).build();
        }
        return Response.ok(responseString)
                .header("Access-Control-Allow-Origin", "*").build();
    } // listUsers()

    //curl -d '{"foo":"silly1","bar":"silly2"}' \
	//     -H "Content-Type: application/json" \
    //     -X POST  http://localhost:9990/api/exampleJSON
	//
    //{"status_code":1, "foo":silly1, "bar":silly2}
    @POST
    @Path("/exampleJSON")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response exampleJSON(InputStream inputData) {
        String responseString = "{\"status_code\":0}";
        StringBuilder crunchifyBuilder = new StringBuilder();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(inputData));
            String line = null;
            while ((line=in.readLine()) != null) {
                crunchifyBuilder.append(line);
            }
            String jsonString = crunchifyBuilder.toString();

            Map<String, String> myMap = gson.fromJson(jsonString, mapType);
            String fooval = myMap.get("foo");
            String barval = myMap.get("bar");
            //Here is where you would put your system test,
            //but this is not required.
            //We just want to make sure your API is up and active/
            //status_code = 0 , API is offline
            //status_code = 1 , API is online
            responseString = "{\"status_code\":1, "
					+"\"foo\":\""+fooval+"\", "
					+"\"bar\":\""+barval+"\"}";
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            ex.printStackTrace();
            return Response.status(500).entity(exceptionAsString).build();
        }
        return Response.ok(responseString)
                .header("Access-Control-Allow-Origin", "*").build();
    } // exampleJSON()

    //curl http://localhost:9990/api/exampleGETBDATE/2
    //{"bdate":"1968-01-26"}
    @GET
    @Path("/exampleGETBDATE/{idnum}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response exampleBDATE(@PathParam("idnum") String idnum) {
        String responseString = "{\"status_code\":0}";
        StringBuilder crunchifyBuilder = new StringBuilder();
        try {
            Map<String,String> teamMap = Launcher.dbEngine.getBDATE(idnum);
            responseString = Launcher.gson.toJson(teamMap);
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            ex.printStackTrace();
            return Response.status(500).entity(exceptionAsString).build();
        }
        return Response.ok(responseString)
                .header("Access-Control-Allow-Origin", "*").build();
    } // exampleBDATE


	 // create a new Instatwitsnapbook user
	// Input: curl -d '{"handle":"@cooldude42", "password":"mysecret!", "fullname":"Angus Mize", "location":"Kentucky", "xmail":"none@nowhere.com", "bdate":"1970-07-01"}' -H "Content-Type: application/json" -X POST http://localhost:9990/api/createuser (Links to an external site.)
	// Output: {"status":"4"} // positive number is the Identity.idnum created.
	// Output: {"status":"-2", "error":"SQL Constraint Exception"}. [EDIT 04/14]
// etc.
	@GET
	@Path("/createuser")
	@Produces(MediaType.APPLICATION_JSON)
	public Response createuser(InputStream inputData){
        String responseString = "{\"status_code\":0}";
        StringBuilder crunchifyBuilder = new StringBuilder();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(inputData));
            String line = null;
            while ((line=in.readLine()) != null) {
                crunchifyBuilder.append(line);
            }
            String jsonString = crunchifyBuilder.toString();

            Map<String, String> userMap = gson.fromJson(jsonString, mapType);
            String handle = userMap.get("handle");
            String password = userMap.get("password");
            String fullname = userMap.get("fullname");
            String location = userMap.get("location");
            String xmail = userMap.get("xmail");
            String bdate = userMap.get("bdate");
            java.time.LocalDate now = java.time.LocalDate.now();
            String joined = now.toString();
            Map<String, String> myMap = Launcher.dbEngine.createuser(handle, password, fullname, location, xmail, bdate, joined);
            String idnum = myMap.get("idnum");
            responseString = "{\"status\":\""+idnum+"\"}";
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            ex.printStackTrace();
            return Response.status(500).entity(exceptionAsString).build();
        }
        return Response.ok(responseString)
                .header("Access-Control-Allow-Origin", "*").build();
	}
        
		

    // Input: curl -d '{"handle":"@cooldude42", "password":"mysecret!"}' -H "Content-Type: application/json" -X 
    //POST http://localhost:9990/api/seeuser/2 (Links to an external site.)
    // 2 = Identity.idnum
    // Output: {"status":"1", "handle":"@carlos", "fullname":"Carlos Mize", "location":"Kentucky", "email":carlos@notgmail.com", "bdate":"1970-01-26","joined":"2020-04-01"}
    // Output: {}. // no match found, could be blocked, user doesn't know.
	@GET
	@Path("/seeuser")
	@Produces(MediaType.APPLICATION_JSON)
	public Response seeuser(InputStream InputData) {
        String responseString = "{\"status_code\":0}";
        StringBuilder crunchifyBuilder = new StringBuilder();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(InputData));
            String line = null;
            while ((line = in.readLine()) != null) {
                crunchifyBuilder.append(line);
            }
            String jsonString = crunchifyBuilder.toString();

            Map<String, String> userMap = gson.fromJson(jsonString, mapType);
            String handle = userMap.get("handle");
            String password = userMap.get("password");
            Map<String, String> myMap = Launcher.dbEngine.seeUser(handle, password);
            handle = myMap.get("handle");
            String fullname = myMap.get("fullname");
            String location = myMap.get("location");
            String xmail = myMap.get("xmail");
            String bdate = myMap.get("bdate");
            String joined = myMap.get("joined");
            responseString = "{\"status\":\"1\", \"handle\":\"" + handle + ", \"fullname\":\"" + fullname + ", \"location\":\"" + location + "\", \"email\":\"" + xmail + "\", \"bdate\":\"" + bdate + "\", \"joined\":\"" + joined + "\"}";
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            ex.printStackTrace();
            return Response.status(500).entity(exceptionAsString).build();
        }
        return Response.ok(responseString)
                .header("Access-Control-Allow-Origin", "*").build();
	}

	// Query should be give idnum, handle of at most 4 (Hint: LIMIT 4)
    // idnum and handles of people followed by people that are followed
	// by you BUT not you and not anyone you
	// already follow.
	// Input: curl -d '{"handle":"@cooldude42", "password":"mysecret!"}' -H "Content-Type: application/json" -X 
	//POST http://localhost:9990/api/suggestions (Links to an external site.)
	// Output, status > 0 is the number of suggested people returned
	// Output: {"status":"3", "idnums":"1,2,4", "handles":"@paul,@carlos","@fake"}
	// Output: {"status":"0", "error":"no suggestions"}
	@GET
	@Path("/suggestions")
	@Produces(MediaType.APPLICATION_JSON)
    public Response suggestions(){
        String responseString = "{\"status_code\":0, "+"\"error\": \"no suggestions\"}";
        try{
            Map<String, String> teamMap = Launcher.dbEngine.sugUsers();
            Launcher.gson.toJson(teamMap);
            responseString = "{\"status_code\":\"3\", \"idnums\":\"124\", \"handles\":\"paul carlose fake\"}";
        }
        catch (Exception ex){
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            ex.printStackTrace();
            return Response.status(500).entity(exceptionAsString).build();
        }
        return Response.ok(responseString).header("Access-Control-Allow-Origin", "*").build();
    }//suggestions

	// Input: curl -d '{"handle":"@cooldude42", "password":"mysecret!", "chapter":"I ate at Mario's!", "url":"http://imagesite.dne/marios.jpg"}' -H "Content-Type: application/json" -X POST http://localhost:9990/api/poststory (Links to an external site.)
	// Output: {"status":"1"}
	// Output: {"status":"0", "error":"invalid expires date"}
	@GET
	@Path("/poststory")
	@Produces(MediaType.APPLICATION_JSON)
	public Response poststory(InputStream InputData){
        String responseString = "{\"status_code\":\"0\"}";
        StringBuilder crunchifyBuilder = new StringBuilder();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(InputData));
            String line = null;
            while ((line = in.readLine()) != null) {
                crunchifyBuilder.append(line);
            }
            String jsonString = crunchifyBuilder.toString();

            Map<String, String> userMap = gson.fromJson(jsonString, mapType);
            String handle = userMap.get("handle");
            String password = userMap.get("password");
            String chapter = userMap.get("chapter");
            String url = userMap.get("url");
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            String timestamp = now.toString();
            java.time.LocalDateTime tomorrow = java.time.LocalDateTime.now();
            tomorrow = tomorrow.plusHours(24);
            String expired = tomorrow.toString();
            Map<String, String> myMap = Launcher.dbEngine.poststory(handle, password, chapter, url, expired, timestamp);
            responseString = "{\"status\":\"1\"}";
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            ex.printStackTrace();
            return Response.status(500).entity(exceptionAsString).build();
        }
        return Response.ok(responseString)
                .header("Access-Control-Allow-Origin", "*").build();
	}


	// [EDIT 04/22: this is erroneous, let the user do this, its their fault]
	// NO! Output: {"status":"0", "error":"expire date in past"}
	// Output: {"status":"0", "error":"missing chapter"}
	// etc.
	//????????????????????????? DOnt know what he wants from this (think it might go to poststory)

    // "like" or "retweet" someone's Story
	// Input: curl -d '{"handle":"@cooldude42", "password":"mysecret!", "likeit":true}' -H "Content-Type: application/json" -X 
	//POST http://localhost:9990/api/reprint/45 (Links to an external site.)
	// if "likeit" is omitted, a coercion to boolean results in "false".
	// FYI. Seems like reasonable result. [04/16]
	// 45 = Story.sidnum
	// Output: {"status":"1"}
	// Output: {"status":"0", "error":"blocked"}
	// Output: {"status":"0", "error":"story not found"}
	// etc.
	@GET
	@Path("/reprint")
	@Produces(MediaType.APPLICATION_JSON)
	public Response reprint(InputStream InputData){
	String responseString = "{\"status_code\":0}";
        StringBuilder crunchifyBuilder = new StringBuilder();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(InputData));
            String line = null;
            while ((line = in.readLine()) != null) {
                crunchifyBuilder.append(line);
            }
			String jsonString = crunchifyBuilder.toString();
			Map<String, String> userMap = gson.fromJson(jsonString, mapType);
			String handle = userMap.get("handle");
			String password = userMap.get("password");
			String likeit = userMap.get("likeit");
			boolean like = Boolean.parseBoolean(likeit);
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            String timestamp = now.toString();
        }
        catch (Exception ex) {
	        StringWriter sw = new StringWriter();
	        ex.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            ex.printStackTrace();
            return Response.status(500).entity(exceptionAsString).build();
	    }
	    return Response.ok(responseString)
                .header("Access-Control-Allow-Origin", "*").build();
	}

	 // add someone to your followings list
	// Input: curl -d '{"handle":"@cooldude42", "password":"mysecret!"}' -H "Content-Type: application/json" -X POST http://localhost:9990/api/follow/2 (Links to an external site.)
	// 2 = Identity.idnum
	// Output: {"status":"1"}
	// Output: {"status":"0", "error":"blocked"}
	// DNE
	// etc.
	@GET
	@Path("/follow")
	@Produces(MediaType.APPLICATION_JSON)
	public Response follow(InputStream InputData){
        String responseString = "{\"status_code\":0, , "+"\"error\": \"not currently followed\"}";
        StringBuilder crunchifyBuilder = new StringBuilder();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(InputData));
            String line = null;
            while ((line = in.readLine()) != null) {
                crunchifyBuilder.append(line);
            }
            String jsonString = crunchifyBuilder.toString();
            Map<String, String> userMap = gson.fromJson(jsonString, mapType);
            String handle = userMap.get("handle");
            String password = userMap.get("password");
            Map<String, String> myMap = Launcher.dbEngine.follow(handle, password);
            responseString = "{\"status_code\":1}";
        }
        catch (Exception ex){
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            ex.printStackTrace();
            return Response.status(500).entity(exceptionAsString).build();
        }
        return Response.ok(responseString).header("Access-Control-Allow-Origin", "*").build();
	}

	// remove someone from your followings list
	// Input: curl -d '{"handle":"@cooldude42", "password":"mysecret!"}' -H "Content-Type: application/json" -X 
	//POST http://localhost:9990/api/unfollow/2 (Links to an external site.)
	// 2 = Identity.idnum
	// Output: {"status":"1"}
	// Output: {"status":"0", "error":"not currently followed"}
	// etc.
	@GET
	@Path("/unfollow")
	@Produces(MediaType.APPLICATION_JSON)
	public Response unfollow(InputStream InputData){
	    String responseString = "{\"status_code\":0, , "+"\"error\": \"not currently followed\"}";
        StringBuilder crunchifyBuilder = new StringBuilder();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(InputData));
            String line = null;
            while ((line = in.readLine()) != null) {
                crunchifyBuilder.append(line);
            }
		    String jsonString = crunchifyBuilder.toString();
		    Map<String, String> userMap = gson.fromJson(jsonString, mapType);
		    String handle = userMap.get("handle");
		    String password = userMap.get("password");
		    Map<String, String> myMap = Launcher.dbEngine.unfollow(handle, password);
		    responseString = "{\"status_code\":1}";
		}
		catch (Exception ex){
	        StringWriter sw = new StringWriter();
	        ex.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            ex.printStackTrace();
            return Response.status(500).entity(exceptionAsString).build();
	    }
	    return Response.ok(responseString).header("Access-Control-Allow-Origin", "*").build();
    }

	// Block a user
	// Input: curl -d '{"handle":"@cooldude42", "password":"mysecret!"}' -H "Content-Type: application/json" -X 
	//POST http://localhost:9990/api/block/2 (Links to an external site.)
	// 2 = Identity.idnum
	// Output: {"status":"1"}
	// Output: {"status":"0", "error":"DNE"}
	@GET
	@Path("/block")
	@Produces(MediaType.APPLICATION_JSON)
	public Response block(InputStream InputData){
	String responseString = "{\"status_code\":0, , "+"\"error\": \"DNE\"}";
        StringBuilder crunchifyBuilder = new StringBuilder();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(InputData));
            String line = null;
            while ((line = in.readLine()) != null) {
                crunchifyBuilder.append(line);
            }
		    String jsonString = crunchifyBuilder.toString();
		    Map<String, String> userMap = gson.fromJson(jsonString, mapType);
		    String handle = userMap.get("handle");
		    String password = userMap.get("password");
		    Map<String, String> myMap = Launcher.dbEngine.block(handle, password);
		    responseString = "{\"status_code\":1}";
		}
		catch (Exception ex){
	        StringWriter sw = new StringWriter();
	        ex.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            ex.printStackTrace();
            return Response.status(500).entity(exceptionAsString).build();
	    }
	    return Response.ok(responseString).header("Access-Control-Allow-Origin", "*").build();
	}

	// see all Story/Reprints of people you follow
    //     for a particular time interval
	// Input: curl -d '{"handle":"@cooldude42", "password":"mysecret!", "newest":"2020-04-02 15:33:59", "oldest":"2020-03-29 00:00:01"}' -H "Content-Type: application/json" -X POST http://localhost:9990/api/timeline (Links to an external site.)
	//
	// This is the most complicated API. You'll need a single SQL statement to get
	// all the points for the rubric. This is a challenge to make the DBMS do all the hard
	// work! Think through what is needed to JOIN all the tables to produce this answer set.
	// Your task is to create a single SQL query based on the requester's handle, producing
	// all Story entries for all handles they follow, including any Reprint/retweets
	// (ie, where Reprint.likeit=false). Only list those that have tstamps between the
	// interval (older than "newest" and newer than "oldest" submitted). You can
	// assume newest and oldest are valid values. You will have to deal with all
	// tables, including the Block table. (if someone retweets a Story of someone that
	// has blocked you, it should not show on your timeline).
	// [EDIT 04/16] Here is the output for timeline. Enumerate the key for each story/reprint
	// Then the value/righthand side will be a JSON object itself, curly brace,
	// then (5) key/value pairs and a closing brace }. In the example code you
	// can put this in a Map<String,String>. We're not worried about using this
	// output, we just want to see it.
	// [EDIT 04/17] I left off sidnum, but it is needed.]
	// Output: {"0":"{\"type\":\"story\",\"author\":\"@cooldude44\",\"sidnum\":\"14\",\"chapter\":\"Just some set math, SQL is super fun!\",\"posted\":\"2020-04-16 15:37:48\"}","1":"{\"type\":\"reprint\",\"author\":\"@cooldude44\",\"sidnum\":\"15\",\"chapter\":\"JSON objects are fun and useful!\",\"posted\":\"2020-04-15 10:37:44\"}","status":"2"}
	// Output: {"status":"0"}
	// etc.
@Get
	@Path("/timeline")
	@Procedures(MediaType.APPLICATION_JSON)
	public Response timeline(InputStream InputData){
	String responseString = "{\"status_code\":}";
	StringBuilder crunchifyBuilder = new StringBuilder();
	try{
	BufferedReader in = new BufferedReader(new InputStreamReader(InputData));
            String line = null;
	while ((line = in.readLine()) != null){
	crunchifyBuilder.append(line);
	}
	Map<String, String> userMap = gson.fromJson(jsonString, mapType);
	String handle = userMap.get("handle");
	String password = userMap.get("password");
	String newest = userMap.get("newest");
	String oldest = userMap.get("oldest");
	String tstamps = userMap.get("tstamps");
	String idnum = userMap.get("idnum");
	String chapter = userMap.get("chapter");
	String responseString =  {"0":"{\"type\":\"story\",\"author\":\""+handle+"\",\"sidnum\":\""+idnum+"\",\"chapter\":\""+chapter+"\",\"posted\":\""+tstamps+"\"}"; 

	}
	catch (Exception ex){
	StringWriter sw = new StringWriter();
	ex.printStackTrace(new PrintWriter(sw));
    String exceptionAsString = sw.toString();
    ex.printStackTrace();
    return Response.status(500).entity(exceptionAsString).build();
	}
	return Response.ok(responseString).header("Access-Control-Allow-Origin", "*").build();
    } // API.java

    } // API.java
