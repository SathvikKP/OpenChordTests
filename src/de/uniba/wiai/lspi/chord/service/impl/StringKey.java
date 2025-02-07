package de.uniba.wiai.lspi.chord.service.impl;
import de.uniba.wiai.lspi.chord.service.Key;

public class StringKey implements Key {
    private String theString;

    public StringKey(String theString) {
        this.theString = theString;
    }

    public byte[] getBytes() {
        return this.theString.getBytes();
    }

    @Override
    public int hashCode() {
        return theString.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof StringKey) {
            return ((StringKey) o).theString.equals(this.theString);
        }
        return false;
    }
}
