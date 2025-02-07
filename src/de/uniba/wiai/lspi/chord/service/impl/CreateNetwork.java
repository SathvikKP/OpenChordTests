package de.uniba.wiai.lspi.chord.service.impl;


import de.uniba.wiai.lspi.chord.service.*;
import de.uniba.wiai.lspi.chord.service.impl.*;
import java.net.MalformedURLException;
//import java.net.URL;
//import java.util.Hashtable;
//import de.uniba.wiai.lspi.chord.com.local.ThreadEndpoint;
//import de.uniba.wiai.lspi.chord.com.rmi.RMIEndpoint;
//import de.uniba.wiai.lspi.chord.com.socket.SocketEndpoint;
import de.uniba.wiai.lspi.chord.data.URL;
import de.uniba.wiai.lspi.util.logging.Logger;
import java.util.Set;
import java.util.Scanner;
import java.io.Serializable;


public class CreateNetwork {
    public static void main(String[] args) {
        PropertiesLoader.loadPropertyFile();
        //de.uniba.wiai.lspi.chord.service.PropertiesLoader.loadPropertyFile();
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
        } catch (ServiceException e) {
            throw new RuntimeException("Could not create DHT!", e);
        }

        // Interactive loop: allow lookups continuously
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n[Bootstrap Node] Enter key to lookup (or type 'exit' to quit):");
            String key = scanner.nextLine();
            if ("exit".equalsIgnoreCase(key)) {
                System.out.println("Exiting lookup loop.");
                break;
            }
            try {
                StringKey lookupKey = new StringKey(key);

                // Measure lookup time
                long startTime = System.nanoTime();
                Set<Serializable> results = chord.retrieve(lookupKey);
                long endTime = System.nanoTime();
                long elapsedTime = (endTime - startTime) / 1_000_000; // Convert to milliseconds

                System.out.println("Lookup time for key " + lookupKey + " = " + elapsedTime + " ms");

                // Display results
                for (Serializable result : results) {
                    System.out.println("Retrieved value: " + result);
                }
            } catch (Exception ex) {
                System.out.println("Error during lookup: " + ex.getMessage());
            }
        }
        scanner.close();
    }
}
