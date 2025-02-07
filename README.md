# Open Chord README

This repository contains an scripts, configuration files, and example classes for testing distributed hash table (DHT) functionality of OpenChord Software. I have written a brief overview focusing on three key elements: **OpenChordTest.java**, **runme.sh**, and the **config** directory.

### Requirements
- Ubuntu / Linux Operating System that supports the screen terminal software
- openjdk or any jdk version


---

## 1. OpenChordTest.java

**Location**: `src/de/uniba/wiai/lspi/chord/service/impl/OpenChordTest.java`  
**Purpose**: MAIN CODEBASE!! Demonstrates how to create and join a Chord ring, insert key-value pairs, look them up, and run performance tests. It offers two main modes:
- **Bootstrap Mode**: Creates a new DHT network using `Chord.create(...)`.
- **Peer Mode**: Joins an existing network with `Chord.join(...)`.

**Note**:  
- Uses `PropertiesLoader.loadPropertyFile()` to configure various Chord settings (e.g., successors, logging).  
- Each peer will have interactive terminal (slightly buggy) that accept console commands like “insert,” “lookup,” and “runtests.”  

Usage Example (command-line):

Bootstrap a new network

`bash
java -cp "build/classes:config:lib/log4j.jar" de.uniba.wiai.lspi.chord.service.impl.OpenChordTest bootstrap
`

Join an existing network

`bash
java -cp "build/classes:config:lib/log4j.jar" de.uniba.wiai.lspi.chord.service.impl.OpenChordTest peer localhost:8081 localhost:8080
`

---

## 2. runme.sh

**Location**: `./runme.sh`  
**Purpose**: This automates compiling and launching instances of Open Chord nodes. It performs:
1. Removes old log files.
2. Compiles necessary classes (e.g., `OpenChordTest.java`).
3. Spawns a “bootstrap” node on port 8080.
4. Optionally spawns multiple peer nodes on subsequent ports (e.g., 8081, 8082, etc.).
5. Uses screen functionality -> requires OS supporting screen.

**Note**:
- This sets environment variables and classpaths to include the `config` directory and dependencies in `lib/`. I placed it here for convinence. If you change the location, then lot of changes are required
- It creates a ring of nodes automatically without manually issuing multiple `java` commands.


Usage Example:

`bash runme.sh
`

---

## 3. config Directory

**Location**: `config/`  
**Purpose**: Contains configuration files for the Open Chord project, including:
- **log4j.properties**: I have disabled debug logs and have used WARN level logs for convinence and testing. Feel free to change it to however you want. Currently, it stores logs for each peer in the ./logs/peer* format for easy viewing.  
- **chord.properties**: We can change default Chord parameters such as `successors=2`.

**Highlights**:  
- Adjust `chord.properties` to tune Chord’s maintenance tasks for your environment.  
- Edit `log4j.properties` or a similar file to control the logging level, whether writing to console/file, log format, etc.  
- Place additional libraries or JAR files in `lib/` if you add new features requiring extra dependencies.

---

## How to Get Started

1. **clone the repository**

(Need ubuntu with openjdk)

2. **Run**  

Examine, and change the runme.sh script according to your needs, then run it. It creates the following
- **Bootstrap Node**: Creates a new Chord network on `localhost:8080`.  
- **Peer Node**: Joins the existing network with a specific local port and the bootstrap port.

3. **Configure**  
- Edit `config/chord.properties` for DHT parameters.  
- Edit `config/log4j.properties` to control logging details.
- Edit `runme.sh` for number of peers, sleep time, log file location etc

4. **Check Logs**  
- I have defaulted Logs to be written to `logs/` directory (or a location set in `config/log4j.properties`).  
- If something goes wrong, review log contents (you may encounter FATAL errors but it stil runs fine somehow)
---

## Additional Notes

- **License**: Open Chord is licensed under the GNU General Public License (GPL).   
- **Manual Testing**: The `console.sh` script can run a console tool for direct node manipulation (from documentation)

For further details, see the comments in the code or the `docs` folder for official API reference.
