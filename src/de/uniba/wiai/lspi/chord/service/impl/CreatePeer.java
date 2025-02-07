package de.uniba.wiai.lspi.chord.service.impl;

import de.uniba.wiai.lspi.chord.service.*;
import de.uniba.wiai.lspi.chord.data.URL;
import de.uniba.wiai.lspi.util.logging.Logger;
import java.util.Scanner;
import java.util.Set;
import java.io.Serializable;

public class CreatePeer {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java StartPeer <localURL> <bootstrapURL>");
            return;
        }

        PropertiesLoader.loadPropertyFile();
        String protocol = URL.KNOWN_PROTOCOLS.get(URL.SOCKET_PROTOCOL);
        URL localURL, bootstrapURL;

        try {
            localURL = new URL(protocol + "://" + args[0] + "/");
            bootstrapURL = new URL(protocol + "://" + args[1] + "/");
        } catch (Exception e) {
            throw new RuntimeException("Invalid URL format", e);
        }

        Chord chord = new ChordImpl();

        try {
            chord.join(localURL, bootstrapURL);
            System.out.println("Peer joined at " + localURL + " via " + bootstrapURL);
            Runtime runtime = Runtime.getRuntime();
            long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024; 
            System.out.println("Memory usage at " + localURL + " = " + usedMemory + " MB");
        } catch (ServiceException e) {
            throw new RuntimeException("Could not join network!", e);
        }

        Scanner scanner = new Scanner(System.in);
        while(true) {
            System.out.println("\n[Peer " + args[0] + "] Enter command: 'insert' or 'lookup' (or 'exit' to quit):");
            String command = scanner.nextLine();
            if ("exit".equalsIgnoreCase(command)) {
                System.out.println("Exiting interactive mode.");
                break;
            } else if ("insert".equalsIgnoreCase(command)) {
                System.out.println("Enter key to insert: ");
                String key = scanner.nextLine();
                System.out.println("Enter value for key " + key + ": ");
                String value = scanner.nextLine();
                try {
                    chord.insert(new StringKey(key), value);
                    System.out.println("Inserted (" + key + ", " + value + ")");
                    Runtime runtime = Runtime.getRuntime();
                    long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
                    System.out.println("Memory usage at " + localURL + " = " + usedMemory + " MB");
                } catch (Exception e) {
                    System.out.println("Error inserting key: " + e.getMessage());
                }
            } else if ("lookup".equalsIgnoreCase(command)) {
                System.out.println("Enter key to lookup: ");
                String key = scanner.nextLine();
                try {
                    StringKey lookupKey = new StringKey(key);

                    
                    long startTime = System.nanoTime();
                    Set<Serializable> results = chord.retrieve(lookupKey);
                    long endTime = System.nanoTime();
                    long elapsedTime = (endTime - startTime) / 1_000_000; 

                    System.out.println("Lookup time for key " + lookupKey + " = " + elapsedTime + " ms");

                    
                    for (Serializable result : results) {
                        System.out.println("Retrieved value: " + result);
                    }
                    Runtime runtime = Runtime.getRuntime();
                    long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024; 
                    System.out.println("Memory usage at " + localURL + " = " + usedMemory + " MB");
                } catch (Exception e) {
                    System.out.println("Error during lookup: " + e.getMessage());
                }
            } else {
                System.out.println("Unknown command. Please use 'insert' or 'lookup'.");
            }
        }
        scanner.close();


    }
}
