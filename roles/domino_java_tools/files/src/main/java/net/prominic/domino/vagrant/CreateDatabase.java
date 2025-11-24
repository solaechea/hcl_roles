package net.prominic.domino.vagrant;

import java.io.File;

import lotus.domino.*;

/**
 * Create a blank database that may be used for DXL imports
 */
public class CreateDatabase {

    private static final String APP_NAME = "CreateDatabase";
    private static final String USAGE = "java -jar CreateDatabase.jar <server> <database-name>";

    public static void main(String[] args) {
        Session session = null;
        try {
            System.out.println("Application '" + APP_NAME + "' started.");

			if (args.length < 2) {
				System.err.println("ERROR: Not enough arguments.");
				System.err.println("USAGE:  " + USAGE);
				System.exit(1);
			}
			String server = args[0];
			String databaseName = args[1];


            NotesThread.sinitThread();

            // If a password is available on the command line, use that when creating the session
            String password = System.getenv("PASSWORD");
            if (null == password || password.trim().isEmpty()) {
                System.out.println("No password found.");
                session = NotesFactory.createSession();
            }
            else {
                System.out.println("Password found.");
                session = NotesFactory.createSession((String)null, (String)null, password);
            }
            System.out.println("Running as user: '" + session.getUserName() + "'.");

            createDatabase(session, server, databaseName);


            System.out.println("names.nsf was successfully created.");


        }
        catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        finally {
            try {
                if (null != session) {
                    session.recycle();
                }
            }
            catch(NotesException ex) {
                ex.printStackTrace();
            }
            NotesThread.stermThread();
            System.out.println("Application '" + APP_NAME + "' completed.");
        }
    }


    public static void createDatabase(Session session, String server, String databaseName) throws NotesException, Exception {
        DbDirectory dbDirectory = null;
        Database database = null;

        try {
        		// NOTE: The database could also be created from a template.  See CreateNamesDatabase for an example.

        		// Create with DBDirectory:  https://help.hcl-software.com/dom_designer/14.0.0/basic/H_CREATE_METHOD_JAVA.html
        		// If "" is used for the server the database will be created in the directory configured in notes.ini
            dbDirectory = session.getDbDirectory(server);
            // The second parameter will open the database so that more options may be run.
            database = dbDirectory.createDatabase(databaseName, true);
            // The database is blank, with no forms or views
            // TODO:  Describe the default ACL
            
            // For the title, use the database name, but strip the directory and extension
            String title = databaseName;
            int index = title.lastIndexOf('.');
            if (index >= 0) {
            		title = title.substring(0, index);
            }
            index = title.lastIndexOf("/");
            if (index < 0) { // no match
            		index = title.lastIndexOf("\\");  // try backslash instead
        		}
        		if (index >= 0) {
        			title = title.substring(index + 1);
        		}
            database.setTitle(title);
        }
        finally {
            if (null != database) {
                database.recycle();
            }
            if (null != dbDirectory) {
                dbDirectory.recycle();
            }
        }

    }
}