#!/bin/bash

rm logs/*

javac -cp "./build/classes:config:lib/log4j.jar" src/de/uniba/wiai/lspi/chord/service/impl/StringKey.java -d ./build/classes
javac -cp "./build/classes:config:lib/log4j.jar" src/de/uniba/wiai/lspi/chord/service/impl/OpenChordTest.java -d ./build/classes

read -p "Start ??? (^C to cancel)..."

# Start the bootstrap 
screen -dmS network bash -c 'java -Dlogfile=logs/bootstrap.log -Dlog4j.configuration=file:config/log4j.properties -cp "./build/classes:config:lib/log4j.jar" de.uniba.wiai.lspi.chord.service.impl.OpenChordTest bootstrap'

# Wait
sleep 2

# Start peers....
#for port in {8081..8088}; do
for port in {8081..8096}; do
#for port in {8081..8112}; do
    sleep 2
    peer_num=$((port - 8080))
    log_file="logs/peer$peer_num.log"
    #screen -dmS peer_$peer_num bash -c 'java -Dlog4j.appender.CHORD_FILE.File=$log_file -cp "./build/classes:config:lib/log4j.jar" de.uniba.wiai.lspi.chord.service.impl.OpenChordTest peer "localhost:'$port'" "localhost:8080"'
    screen -dmS peer$peer_num bash -c "java -Dlogfile=$log_file -Dlog4j.configuration=file:config/log4j.properties -cp './build/classes:config:lib/log4j.jar' de.uniba.wiai.lspi.chord.service.impl.OpenChordTest peer 'localhost:$port' 'localhost:8080'"
done

echo "Bootstrap and peer nodes have been started in screen sessions. Check logs to see if any FATAL errors occurred."
echo "screen -ls to list the screen sessions, screen -r <session_name> to attach to a session."

