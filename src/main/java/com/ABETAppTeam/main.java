package com.ABETAppTeam;

/**
 * Main class for the ABET Assessment Application
 * 
 * This application is a web application that uses Jetty as the web server.
 * To run the application, use the following Maven command:
 * 
 * mvn clean compile jetty:run
 * 
 * This will start the Jetty server on port 8081 and deploy the application.
 * You can access the application at http://localhost:8081/
 * 
 * Available endpoints:
 * - /index
 * - /admin
 * - /professor
 * - /test
 * 
 * Note: This application uses JSP files for rendering the views. The servlets
 * forward requests to the appropriate JSP files in the WEB-INF directory.
 */
public class main {
    public static void main(String[] args) {
        System.out.println("ABET Assessment Application");
        System.out.println("To run the application, use the following Maven command:");
        System.out.println("mvn clean compile jetty:run");
        System.out.println();
        System.out.println("This will start the Jetty server on port 8081 and deploy the application.");
        System.out.println("You can access the application at http://localhost:8081/");
        System.out.println();
        System.out.println("Available endpoints:");
        System.out.println("- /index");
        System.out.println("- /admin");
        System.out.println("- /professor");
        System.out.println("- /test");
        System.out.println();
        System.out.println("Note: This application uses JSP files for rendering the views.");
        System.out.println("The servlets forward requests to the appropriate JSP files in the WEB-INF directory.");
    }
}
