start: First Update Get CommonFunctions

First: FirstServlet.class CommonFunctions

FirstServlet.class: FirstServlet.java
	javac -classpath  "$(CATALINA_HOME)/lib/servlet-api.jar;$(CATALINA_HOME)/lib/json-simple-1.1.1.jar;$(CATALINA_HOME)/lib/postgresql-9.4-1201.jdbc4.jar;" FirstServlet.java -Xlint
    
Update: UpdateServlet.class CommonFunctions
    
UpdateServlet.class: UpdateServlet.java
	javac -classpath  "$(CATALINA_HOME)/lib/servlet-api.jar;$(CATALINA_HOME)/lib/json-simple-1.1.1.jar;$(CATALINA_HOME)/lib/postgresql-9.4-1201.jdbc4.jar;" UpdateServlet.java -Xlint
    
Get: GetServlet.class CommonFunctions
	
GetServlet.class: GetServlet.java
	javac -classpath  "$(CATALINA_HOME)/lib/servlet-api.jar;$(CATALINA_HOME)/lib/json-simple-1.1.1.jar;$(CATALINA_HOME)/lib/postgresql-9.4-1201.jdbc4.jar;" GetServlet.java -Xlint


CommonFunctions: CommonFunctions.class

CommonFunctions.class: CommonFunctions.java
	javac -classpath  "$(CATALINA_HOME)/lib/servlet-api.jar;$(CATALINA_HOME)/lib/json-simple-1.1.1.jar;$(CATALINA_HOME)/lib/postgresql-9.4-1201.jdbc4.jar;" CommonFunctions.java -Xlint