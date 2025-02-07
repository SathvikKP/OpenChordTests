#!/bin/bash

#javac -cp "./../build/classes:config:lib/log4j.jar" de/uniba/wiai/lspi/chord/service/impl/StartPeer.java -d ./../build/classes
javac -cp "./build/classes:config:lib/log4j.jar" src/de/uniba/wiai/lspi/chord/service/impl/CreatePeer.java -d ./build/classes
sleep 1

# peer nodes ip addresses
for port in {8081..8088}
do
    java -cp "./build/classes:config:lib/log4j.jar" de.uniba.wiai.lspi.chord.service.impl.CreatePeer "localhost:$port" "localhost:8080" &
    sleep 1
done
