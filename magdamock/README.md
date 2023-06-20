# Magda connector

The magda connector is an abstraction that can be used to connect to magda services; that services can be a magda mock library, an instance of the magdamock service or an actual magda service.

## MagdaConnectorImpl

The implementation of the abstraction requires 3 components: `MagdaConnection`, `AfnemerLogService` & `MagdaHoedanigheidService`.

### MagdaConnection

The `MagdaConnection` takes care of sending request towards the magda backend. Depending on the backend you will need to choose the correct implementation:

#### MagdaMockConnection

This component embeds a 'fake' backend, which we call the magda simulator. When sending request to the mock connection, responses will be retrieved from resources inside the classpath. This is intended to be used for testing, the responses will return data with realistic formats and contents, but is NOT intended for production use.

```java
var connection = MagdaMockConnection.create(); // will create a mock connection with the default simulator

SOAPSimulator simulator = ...; // more on creating your own simulator later
var customMockConnection = MagdaMockConnection.create(simulator); // initialize with a custom simulator
```

#### MagdaSignedConnection

This connection will optionally sign request and verify responses depending on how the component is instantiated. There are 2 ways to construct this class:

```java
MagdaSignedConnection(MagdaConnection magdaConnection, MagdaConfigDto config) throws TwoWaySslException

MagdaSignedConnection(MagdaConnection magdaConnection, TwoWaySslProperties requestSignerConfig, TwoWaySslProperties responseVerifierConfig) throws TwoWaySslException
```

In both casses the target `MagdaConnection` will be used to execute the request.

Using the `MagdaConfigDto`, request signing will happen with the configure keystore; and if `verificationEnabled` is enabled the response will be used to verify the response.

When using `requestSignerConfig` and `responseVerifierConfig`, the request and response will be signed/verified if the related config is present.

```java
MagdaConnection targetConnection = ...

var properties = new TwoWaySslProperties();
keystore.setKeyStoreLocation("location");
keystore.setKeyAlias("alias");
keystore.setKeyPassword("key-password");
keystore.setKeyStorePassword("keystore-password");

var config = MagdaConfigDto.builder()
                           .verificationEnabled(true)
                           .keystore(properties)
                           .build();
var connection = new MagdaSignedConnection(targetConnection, config); // using config
var connection = new MagdaSignedConnection(targetConnection, /* request */ properties, /* response */ properties);
```

#### MagdaSoapConnection

This connection will send http request towards the configured backend service. This component requires 2 parameters: `MagdaEndpoints` and `MagdaConfigDto`. The `MagdaConfigDto` is used to create a SSLConnection using the keystore if present. The `MagdaEndpoints` is used to decide what request will be sent to what endpoint depending on the service called.

```java
MagdaConfigDto config = ...
var endpoints = MagdaEndpoints.builder()
                              .addMapping("service", // e.g: GeefBewijs
                                          "version", // e.g: 02.00.0000
                                          "url") // e.g: https://magda.be/Magda-02.00/soap/WebService
                              .addMapping(...) // more mappings as is required
                              .build();
var connection = new MagdaSoapConnection(endpoints, config);
```

### AfnemerLogService

This interface contains callbacks for certain events:

```java
// triggered before a request is sent
void logAanvraag(MagdaAanvraag magdaRequest);

// triggered after a response has been received that does not contain errors
void logGeslaagdeAanvraag(GeslaagdeAanvraag magdaRequest);

// triggered after a response has been received that contains errors
void logGefaaldeAanvraag(GefaaldeAanvraag failedLoggedRequest);

// triggered when the connection.send throws MagdaSendFailed
void logOnbeantwoordeAanvraag(OnbeantwoordeAanvraag unansweredLoggedRequest);
```

The `magdamock` library contains a implementation of this class: `AfnemerLogServiceMock`. This class retains all the data from the callbacks and logs it to the info log. This class should NOT be used in an production environment because it logs sensitive information to info logs and as data is contained in lists, at some point this will cause out of memory exceptions.

It is strongly recommend to create your own implementation for this class.

### MagdaHoedanigheidService

This class is responsible for supply requester information for request. Every `Aanvraag` sent through the `MagdaConnector` contains a `registratie` field (with default value `default`). The `MagdaHoedanigheidService` will be queried with this `registratie` and should return a correlating `identification` and `hoedanigheidcode`.

#### MagdaHoedanigheidServiceImpl

This implementation also takes a `MagdaConfigDto` and will use registration information of it:

```java
var config = MagdaConfigDto.builder()
                           .registration(Map.of("registratie", MagdaRegistrationConfigDto.builder()
	                                                                                     .identification("identification")
	                                                                                     .hoedanigheidscode("hoedanigheidscode")
	                                                                                     .build()))
                           .build();
var service = new MagdaHoedanigheidServiceImpl(config);
```

For every expected value of `registratie` in the `Aanvraag` objects, a registration entry should be present.

#### MagdaHoedanigheidServiceMock

When using a mock environment, the `identification` and `hoedanigheidcode` are not relevant to the service receiving the request, so then `MagdaHoedanigheidServiceMock` can be used:

```java
var service =  new MagdaHoedanigheidServiceMock(MagdaRegistrationInfo.builder()
                                                                     .identification("identification")
                                                                     .hoedanigheidscode("hoedanigheidscode"))
                                                                     .build();
```

### Complete examples

#### MagdaMockConnection

```java
MagdaRegistrationInfo registrationInfo = ...

var logService = new AfnemerLogServiceMock();
var hoedanigheidSerivce = new MagdaHoedanigheidServiceMock(registrationInfo);
var connection = MagdaMockConnection.create();

var connector = new MagdaConnectorImpl(connection, logService, hoedanigheidService);
```

#### Signed Soap Connection

```java
MagdaConfigDto magdaConfig = ...
MagdaEndpoints endpoints = ...

var logService = new AfnemerLogServiceMock(); // suggested a custom implementation here
var hoedanigheidSerivce = new MagdaHoedanigheidServiceImpl(magdaConfig);
var soapConnection = new MagdaSoapConnection(endpoints, magdaConfig);
var signedConnection = new MagdaSignedConnection(soapConnection, magdaConfig);

var connector = new MagdaConnectorImpl(signedConnection, logService, hoedanigheidService);
```

#### Sending a request

```java
MagdaConnector connector = ...

var magdaAntwoord = connector.send(new GeefBewijsAanvraag("insz"));

var document = magdaAntwoord.getDocument();
var xml = document.getXml(); // org.w3c.dom.Document
```

## SOAPSimulator

It is possible to setup a custom `MagdaMockConnection` that uses as `SOAPSimulator` to your liking. This can be done using `SOAPSimulatorBuilder`.

```java
ResourceFinder resourceFinder = ...
TwoWaySslProperties properties = ...

var simulator = SOAPSimulatorBuilder.builder(resourceFinder)
                                    .requestVerifierProperties(properties) // optional; will verify the request signature is set
                                    .responseSignerProperties(properties) // optional; will sign the response if set
                                    .build();

// create connection with the simulator:
var connection = MagdaMockConnection.create(simulator);
```

### ResourceFinder

The resource finder is responsible for the xml response for the request. The default resource finder contains response that are present in the classpath of the `magdamock` library: `ResourceFinders.magdaSimulator()`. But it is possible to register your own resources as source for a resource finder: `ResourceFinders.directory(String/File directory)`. The directory is expected to have a certain structure for it to be able to return responses for request.

E.g:
Consider the following request : `new GeefBewijsAanvraag("01234567891")`. This request wil will attempt to return data for the `GeefBewijs` service for version `02.00.0000` and the citizen with a insz number `01234567891`. The type of this request is `Persoon`. So a following directory structure is expected:

```
Persoon
  \- GeefAttest
     \- 02.00.0000
        \- ... xml files
     GeefBewijs
     \- 02.00.0000
        \- 00071031644.xml
           01234567891.xml  <= this document will be returned
           90122500159.xml
           91090200269.xml
Onderneming
  \- GeefOnderneming
     \- 02.00.0000
        \- ... xml files
  \- GeefAttest
     \- 02.00.0000
        \- ... xml files
```