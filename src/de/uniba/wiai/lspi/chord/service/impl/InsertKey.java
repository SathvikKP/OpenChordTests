package de.uniba.wiai.lspi.chord.service.impl;

import de.uniba.wiai.lspi.chord.service.*;
import java.io.Serializable;
import de.uniba.wiai.lspi.chord.data.URL;
import de.uniba.wiai.lspi.util.logging.Logger;
import java.net.MalformedURLException;
import de.uniba.wiai.lspi.chord.service.impl.StringKey;


public class InsertKey {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: java InsertKey <localURL> <key> <value>");
            return;
        }

        PropertiesLoader.loadPropertyFile();
        String protocol = URL.KNOWN_PROTOCOLS.get(URL.SOCKET_PROTOCOL);
        URL localURL;

        try {
            localURL = new URL(protocol + "://" + args[0] + "/");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        Chord chord = new ChordImpl();

        try {
            chord.join(localURL, new URL(protocol + "://localhost:8080/"));
            System.out.println("Joined network at " + localURL);
            // Measure memory usage after joining the network
            Runtime runtime = Runtime.getRuntime();
            long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024; // Convert to MB
            System.out.println("Memory usage at " + localURL + " = " + usedMemory + " MB");

            // Use StringKey for key insertion
            StringKey key = new StringKey(args[1]);
            Serializable value = args[2];

            chord.insert(key, value);
            System.out.println("Inserted key: " + key + " with value: " + value);

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } 
        catch (ServiceException e) {
            throw new RuntimeException("Could not join network!", e);
        }
    }
}
