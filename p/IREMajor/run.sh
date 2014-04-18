#! /bin/bash

#javac -d ./bin/ -cp "./src:./metamap-api-1.0-SNAPSHOT.jar:./jsoup-1.7.3.jar:./prologbeans.jar" ./src/Indexer/MayoIndex.java
#javac -d ./bin/ -cp "./src:./metamap-api-1.0-SNAPSHOT.jar:./jsoup-1.7.3.jar:./prologbeans.jar" ./src/Parser/MayoParser.java


#java -cp  ".:./bin:./metamap-api-1.0-SNAPSHOT.jar:./jsoup-1.7.3.jar:./prologbeans.jar" Parser.MayoParser "$1"



javac -d ./bin/ -cp "./src:./metamap-api-1.0-SNAPSHOT.jar:./jsoup-1.7.3.jar:./prologbeans.jar" ./src/Indexer/WebMDIndex.java
javac -d ./bin/ -cp "./src:./metamap-api-1.0-SNAPSHOT.jar:./jsoup-1.7.3.jar:./prologbeans.jar" ./src/Parser/WebMDParser.java


#java -cp  ".:./bin:./metamap-api-1.0-SNAPSHOT.jar:./jsoup-1.7.3.jar:./prologbeans.jar" -Xms500m -Xmx1g Parser.WebMDParser "$1"
java -cp  ".:./bin:./metamap-api-1.0-SNAPSHOT.jar:./jsoup-1.7.3.jar:./prologbeans.jar" Parser.WebMDParser "$1"
