#!/bin/bash

javac -cp "./build/classes:config:lib/log4j.jar" src/de/uniba/wiai/lspi/chord/service/impl/CreateNetwork.java -d ./build/classes
javac -cp "./build/classes:config:lib/log4j.jar" src/de/uniba/wiai/lspi/chord/service/impl/CreatePeer.java -d ./build/classes
javac -cp "./build/classes:config:lib/log4j.jar" src/de/uniba/wiai/lspi/chord/service/impl/StringKey.java -d ./build/classes
javac -cp "./build/classes:config:lib/log4j.jar" src/de/uniba/wiai/lspi/chord/service/impl/InsertKey.java -d ./build/classes
javac -cp "./build/classes:config:lib/log4j.jar" src/de/uniba/wiai/lspi/chord/service/impl/LookupTest.java -d ./build/classes


read -p "Start? ^C to exit"
#if [[ $confirm != [y] ]]; then
#    echo "Byeee."
#    exit 1
#fi

#sleep 1

java -cp "./build/classes:config:lib/log4j.jar" de.uniba.wiai.lspi.chord.service.impl.CreateNetwork &



# peer nodes ip addresses
for port in {8081..8088}
do
    java -cp "./build/classes:config:lib/log4j.jar" de.uniba.wiai.lspi.chord.service.impl.CreatePeer "localhost:$port" "localhost:8080" &
    #sleep 1
done

echo "Inserting key"

sleep 1
java -cp "./build/classes:config:lib/log4j.jar" de.uniba.wiai.lspi.chord.service.impl.InsertKey "localhost:8086" "12345" "file1.txt"

#sleep 1

echo "Looking up key"

java -cp "./build/classes:config:lib/log4j.jar" de.uniba.wiai.lspi.chord.service.impl.LookupTest "localhost:8080" "12345"


sleep 10

echo "Killing all, ^C to stop killing all"

sleep 10

pkill java