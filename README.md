# WuerzburgTransportGuide

## Building

At (minimum) Java Development Kit 21 is required to build this project.

### Generating the Client API

This project uses the [Open API](https://www.openapis.org/what-is-openapi) specification that describes the REST API.
This helps us generate the required interface for the [Retrofit](https://square.github.io/retrofit/) HTTP client library.

The source specification file can be found under ``specs/netzplan.yml``

