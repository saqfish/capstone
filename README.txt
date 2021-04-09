DB Scheduling System
Abdirsiaq Sheikh - asheik6@wgu.edu
Date: 04/08/2021
Purpose: Develop a GUI-based scheduling desktop application that connects to an existing MySQL database.

Versions used:

IntelliJ: Community Edition 2020.03
JDK version: 11.0.9
JavaFX: JavaFX-SDK-15
MySQL Connector 8.0.22.

How to Launch:

1. Add JavaFX to the project
2. Add MySql connector to the project
1. Add the run configuration with VM options: `--module-path <JavaFx path> --add-modules javafx.controls,javafx.fxml`.

Launching from provided JAR:

java --module-path JavaFX --add-modules javafx.fxml,javafx.controls -jar scheduler.jar

