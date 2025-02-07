#!/bin/bash
# Compile the Java files
javac -cp "./build/classes:config:lib/log4j.jar" src/de/uniba/wiai/lspi/chord/service/impl/CreateNetwork.java -d ./build/classes
javac -cp "./build/classes:config:lib/log4j.jar" src/de/uniba/wiai/lspi/chord/service/impl/CreatePeer.java -d ./build/classes
javac -cp "./build/classes:config:lib/log4j.jar" src/de/uniba/wiai/lspi/chord/service/impl/StringKey.java -d ./build/classes
javac -cp "./build/classes:config:lib/log4j.jar" src/de/uniba/wiai/lspi/chord/service/impl/InsertKey.java -d ./build/classes
javac -cp "./build/classes:config:lib/log4j.jar" src/de/uniba/wiai/lspi/chord/service/impl/LookupTest.java -d ./build/classes

read -p "Start??? (^C to cancel)..."


screen -dmS network bash -c 'java -cp "./build/classes:config:lib/log4j.jar" de.uniba.wiai.lspi.chord.service.impl.CreateNetwork'

sleep 2

for port in {8081..8088}; do
    peer_num=$((port - 8080))
    screen -dmS peer_$peer_num bash -c 'java -cp "./build/classes:config:lib/log4j.jar" de.uniba.wiai.lspi.chord.service.impl.CreatePeer "localhost:'$port'" "localhost:8080"'
done

echo "Bootstrap and peer nodes started in screen sessions."
echo "To attach to a session, use: screen -r <session_name>"
echo "For example, to attach to the bootstrap node: screen -r network"