package de.uniba.wiai.lspi.chord.service.impl;

import de.uniba.wiai.lspi.chord.service.*;
import de.uniba.wiai.lspi.chord.data.URL;
import java.net.MalformedURLException;
import java.util.Scanner;
import java.util.Set;
import java.util.Properties;
import java.io.Serializable;
import de.uniba.wiai.lspi.util.logging.Logger;

public class OpenChordTest {

    private static final Logger logger = Logger.getLogger(OpenChordTest.class);

    /** 
     * Runs the interactive loop that accepts commands and calls the appropriate operation method.
     */
    private static void infiniteLoop(Chord chord, URL localURL) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n[Node " + localURL + "] Enter command: 'insert' or 'lookup' or 'runtests' (or 'exit' to quit):");
            String command = scanner.nextLine();
            if ("exit".equalsIgnoreCase(command)) {
                System.out.println("Exiting");
                logger.warn("Exiting.");
                break;
            } else if ("insert".equalsIgnoreCase(command)) {
                insertOperation(chord, localURL, scanner, null, null);
            } else if ("lookup".equalsIgnoreCase(command)) {
                lookupOperation(chord, localURL, scanner, null);
            } else if ("runtests".equalsIgnoreCase(command)) {
                runtests(chord, localURL, scanner);
            } else {
                System.out.println("Unknown.");
                logger.warn("Unknown.");
            }
        }
        scanner.close();
    }
    
    /**
     * Performs the insert operation.
     */
    private static void insertOperation(Chord chord, URL localURL, Scanner scanner, String keyInput, String valueInput) {
        String key = keyInput;
        String value = valueInput;
        if (key == null || key.trim().isEmpty()) {
            System.out.println("Enter key to insert: ");
            key = scanner.nextLine();
        }
        if (value == null || value.trim().isEmpty()) {
            System.out.println("Enter value for key " + key + ": ");
            value = scanner.nextLine();
        }
        try {
            long startTime = System.nanoTime();
            chord.insert(new StringKey(key), value);
            long endTime = System.nanoTime();
            long elapsedTime = (endTime - startTime) / 1_000_000; // convert ns to ms
            System.out.println("Insert time for key " + key + " = " + elapsedTime + " ms");
            logger.warn("Insert time for key " + key + " = " + elapsedTime + " ms");
            System.out.println("Inserted (" + key + ", " + value + ")");
            logger.warn("Inserted (" + key + ", " + value + ")");
            Runtime runtime = Runtime.getRuntime();
            long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024; // in MB
            System.out.println("Memory usage at " + localURL + " = " + usedMemory + " MB");
            logger.warn("Memory usage at " + localURL + " = " + usedMemory + " MB");
        } catch (Exception e) {
            System.out.println("Error inserting key: " + e.getMessage());
            logger.warn("Error inserting key: " + e.getMessage());
        }
    }
    
    /**
     * Performs the lookup operation.
     */
    private static long lookupOperation(Chord chord, URL localURL, Scanner scanner, String keyInput) {
        String key = keyInput;
        if (key == null || key.trim().isEmpty()) {
            System.out.println("Enter key to lookup: ");
            key = scanner.nextLine();
        }
        try {
            StringKey lookupKey = new StringKey(key);
            long startTime = System.nanoTime();
            Set<Serializable> results = chord.retrieve(lookupKey);
            long endTime = System.nanoTime();
            long elapsedTime = (endTime - startTime) / 1_000_000; // convert ns to ms
            System.out.println("Lookup time for key " + key + " = " + elapsedTime + " ms");
            logger.warn("Lookup time for key " + key + " = " + elapsedTime + " ms");
            boolean found = false;
            for (Serializable result : results) {
                found = true;
                System.out.println("Retrieved value: " + result.toString());
                logger.warn("Retrieved value: " + result.toString());
            }
            if (!found) {
                System.out.println("Key not found: " + lookupKey);
                logger.warn("Key not found: " + lookupKey);
            }
            Runtime runtime = Runtime.getRuntime();
            long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024; // in MB
            System.out.println("Memory usage at " + localURL + " = " + usedMemory + " MB");
            logger.warn("Memory usage at " + localURL + " = " + usedMemory + " MB");
            return elapsedTime;
        } catch (Exception e) {
            System.out.println("Error during lookup: " + e.getMessage());
            logger.warn("Error during lookup: " + e.getMessage());
            return -1;
        }
    }

    /**
     * Performs the runtests operation.
     */
    public static void runtests(Chord chord, URL localURL, Scanner scanner) {
        System.out.println("Enter number of lookups to perform: ");
        int numTests = Integer.parseInt(scanner.nextLine());
        long totalTime = 0; int lookups = 0;
        for (int i = 1; i <= numTests; i++) {
            String key = Integer.toString(i);
            long timeTaken = lookupOperation(chord, localURL, scanner, key);
            if (timeTaken > 0) {
                totalTime += timeTaken; lookups++;
            }
        }
        if (lookups > 0) {
            long averageTime = totalTime / lookups;
            System.out.println("Average lookup time for " + lookups + " lookups = " + averageTime + " ms");
            logger.warn("Average lookup time for " + lookups + " lookups = " + averageTime + " ms");
        }
        
    }

    /**
     * Runs in bootstrap (network) mode.
     * Initializes a new Chord network (using create) and then enters the interactive loop.
     */
    public static void runBootstrap() {
        PropertiesLoader.loadPropertyFile();
        String protocol = URL.KNOWN_PROTOCOLS.get(URL.SOCKET_PROTOCOL);
        URL localURL = null;
        try {
            localURL = new URL(protocol + "://localhost:8080/");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        Chord chord = new ChordImpl();
        try {
            chord.create(localURL);
            System.out.println("Chord network created at " + localURL);
            logger.warn("Chord network created at " + localURL);
            //Scanner scanner = new Scanner(System.in);
            chord.insert(new StringKey("8080"), "default");
            System.out.println("Inserted (" + 8080 + ", " + "default" + ")");
            logger.warn("Inserted (" + 8080 + ", " + "default" + ")");
        } catch (ServiceException e) {
            throw new RuntimeException("Could not create DHT!", e);
        }
        // Enter interactive mode for lookup/insert
        infiniteLoop(chord, localURL);
    }

    /**
     * Runs in peer mode.
     * Joins an existing Chord network (using join) and then enters the interactive loop.
     * Expects two parameters: local URL (e.g. localhost:8081) and bootstrap URL (e.g. localhost:8080).
     */
    public static void runPeer(String local, String bootstrap) {
        PropertiesLoader.loadPropertyFile();
        String protocol = URL.KNOWN_PROTOCOLS.get(URL.SOCKET_PROTOCOL);
        URL localURL, bootstrapURL;
        try {
            localURL = new URL(protocol + "://" + local + "/");
            bootstrapURL = new URL(protocol + "://" + bootstrap + "/");
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid URL format", e);
        }

        Chord chord = new ChordImpl();
        try {
            chord.join(localURL, bootstrapURL);
            System.out.println("Peer joined at " + localURL + " via " + bootstrapURL);
            logger.warn("Peer joined at " + localURL + " via " + bootstrapURL);
            String key = Integer.toString(Integer.parseInt(local.substring(local.length() - 4)) - 8080);
            Scanner scanner = new Scanner(System.in);
            insertOperation(chord, localURL, scanner, key, "default");
            /*
            long startTime = System.nanoTime();
            chord.insert(new StringKey(key), "default");
            long endTime = System.nanoTime();
            long elapsedTime = (endTime - startTime) / 1_000_000; // convert ns to ms
            System.out.println("Insert time for key " + key + " = " + elapsedTime + " ms");
            logger.warn("Insert time for key " + key + " = " + elapsedTime + " ms");
            System.out.println("Inserted (" + key + ", " + "default" + ")");
            logger.warn("Inserted (" + key + ", " + "default" + ")");
            Runtime runtime = Runtime.getRuntime();
            long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
            System.out.println("Memory usage at " + localURL + " = " + usedMemory + " MB");
            logger.warn("Memory usage at " + localURL + " = " + usedMemory + " MB");
            */
        } catch (ServiceException e) {
            throw new RuntimeException("Could not join network!", e);
        }
        // Enter interactive mode
        infiniteLoop(chord, localURL);
    }

    /**
     * Main method that selects the mode to run: "bootstrap" or "peer".
     * Command-line usage:
     *   Bootstrap: java -cp ... OpenChordTest bootstrap
     *   Peer:      java -cp ... OpenChordTest peer <localURL> <bootstrapURL>
     */
    public static void main(String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("bootstrap")) {
                runBootstrap();
                return;
            } else if (args[0].equalsIgnoreCase("peer")) {
                if (args.length < 3) {
                    System.out.println("Usage: java OpenChordTest peer <localURL> <bootstrapURL>");
                    return;
                }
                runPeer(args[1], args[2]);
                return;
            }
        }
        // If no command-line arguments provided, prompt the user interactively.
        System.out.println("Invalid args"); System.exit(1);
    }
}
