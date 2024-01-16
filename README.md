# WuerzburgTransportGuide

A simple Java desktop application that uses the [VVM Netzplan](https://netzplan.vvm-info.de/) to display public transport information for the city of Würzburg.
It is based on the [JavaFX](https://openjfx.io/) framework and uses the [Retrofit](https://square.github.io/retrofit/) HTTP client library to communicate with the VVM Netzplan API.

VVM does not provide an official API for their Netzplan service, so this project uses a reverse engineered API specification. Consequently, the API may change at any time and without notice, treat it as uncompleted and unstable.
This project is not affiliated with or supported by the Verkehrsverbund Mainfranken.

## Building

At (minimum) Java Development Kit 21 is required to build this project.
The project uses the [Gradle](https://gradle.org/) build system.

### Building the Project

The project can be built using the ``jlinkZip`` Gradle task. This will create a zip archive containing the application and all required dependencies with its own JRE making it easy to distribute.
NOTE: This task will only generate a zip archive for the platform it is executed on.

The generated zip archive can be found under ``build/distributions``. It can be extracted anywhere and the application can be started by executing the ``bin/WuerzburgTransportGuide`` script.

### Generating the Client API

This project uses the [Open API](https://www.openapis.org/what-is-openapi) specification that describes the [VVM Netzplan](https://netzplan.vvm-info.de/) REST API.
This helps us generate the required interface for the [Retrofit](https://square.github.io/retrofit/) HTTP client library.
Using the ``build`` or ``openApiGenerate`` Gradle task will generate the required code.

The source specification file can be found under ``specs/netzplan.yml``
