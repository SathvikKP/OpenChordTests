#!/bin/bash

javac -cp "./build/classes:config:lib/log4j.jar" src/de/uniba/wiai/lspi/chord/service/impl/CreateNetwork.java -d ./build/classes
sleep 1
java -cp "./build/classes:config:lib/log4j.jar" de.uniba.wiai.lspi.chord.service.impl.CreateNetwork &