# Sending Magda requests

## MagdaConnector

The `MagdaConnector` simplifies sending request to magda soap endpoints. The connector can send `MagdaRequest`-s and returns a `MagdaResponse`.

```java
MagdaConnector connector = ... // more on instantiation later

var request = GeefPersoonRequest.builder()
                                .insz("0123456789")
                                .build(); // MagdaRequest
                                
var response = connector.send(request); // more on MagdaResponse later
```

### Creating a MagdaConnector

A connector takes 3 parameters:
 - `MagdaConnection`: defines a connection type, this can be a mock connection or a soap connnection
 - `ClientLogService`: interface with callback methods for the lifecycle of magda requests
 - `MagdaHoedanigHeidService`: allows for configuration of what *hoedanigheids code* is used for requests and what endpoints should be used for what services.
 
#### MagdaConnection

##### MagdaMockConnection

This type of connection won't make any soap calls and will handle requests internally. It will look inside the resources of the `magdamock` jar for mock responses and return those.

Setup is straightforward:
```java
var connection = MagdaMockConnection.create();
```

##### MagdaSoapConnection

With this connection actual soap calls will be made so extra configuration will be required:
```java
var config = MagdaConfigDto.builder()
                           .registration(...) // let's skip the function of registration for now
                           .build();

var endpoints = MagdaEndpoints.builder()
                              .addMapping("GeefPersoon", 
                                          "02.02.0000", 
                                          MagdaEndpoint.of("http://localhost:8080/api/Magda-02.00/soap/WebService"))
                              .build();

var connector = new MagdaSoapConnection(endpoints, config);
```

**MagdaEndpoints**
For every service/version that will be called, the endpoint needs to be registered.

###### Signed Connection

Additional properties can be added to `MagdaConfigDto` to ensure that soap requests are signed. When these values are not set, request won't be signed.

```java
var keystore = new TwoWaySslProperties();
keystore.setKeyStoreLocation("location");
keystore.setKeyStorePassword("keystore-password");
keystore.setKeyAlias("alias");
keystore.setKeyPassword("key-password");

var config = MagdaConfigDto.builder()
                           .keystore(keystore)
                           .registration(...)
                           .build();
```

##### ClientLogService

The client log service is an interface that can be implemented to handle certains request lifecycle callbacks. The library supplies an implementation: `DebugLogService`. This logs callback parameters to the debug log.

##### MagdaHoedanigHeidService

###### MagdaHoedanigheidServiceImpl

```java
var config = MagdaConfigDto.builder()
                           .registration(Map.of(MagdaRequest.DEFAULT_REGISTRATION, 
                                                MagdaRegistrationConfigDto.builder()
                                                                          .identification("identification")
                                                                          .hoedanigheidscode("hoedanigheids-code")
                                                                          .build()))
                           .build();

var hoedanigheidService = new MagdaHoedanigheidServiceImpl(config);
```

The purpose of the registration configuration is to allow configuration of multiple pairs of *identification/hoedanigheidscode* codes. When creating magda request it's possible to define what registration you want to use when sending a request:

```java
GeefPersoonRequest.builder()
                  .registration("other-registration")
                  .insz("0123456789")
                  .build();
```

When sending a request, the connection will look for an entry matching the registration value inside the registration map. Then it will use that configurations *identification/hoedanigheidscode* to setup up the soap request body. If no `registration` is specified in a `MagdaRequest`, `MagdaRequest.DEFAULT_REGISTRATION` will be used.

###### MagdaHoedanigheidServiceMock

When sending request through a mock connector, real identification is not required. So we can just register a defualt identification of which the values are not important:

```java
var mockRegistrationInfo = MagdaRegistrationInfo.builder()
                                                .identification("identification")
                                                .hoedanigheidscode("hoedanigheids-code")
                                                .build();
var magdaHoedanigheidService = new MagdaHoedanigheidServiceMock(mockRegistrationInfo);
```

## MagdaRequest

Available magda request include:
 - GeefAanslagbiljetPersonenbelastingRequest
 - GeefBewijsRequest
 - GeefPasfotoRequest
 - GeefPersoonRequest
 - RegistreerInschrijvingRequest
 - RegistreerUitschrijvingRequest
 - RegistreerInschrijving0201Request

## MagdaResponse

The magda response contains a property `MagdaDocument`. From there you can use the helper method `getValue` with a xpath expression to the result in a string value or `xpath` with an xpath expression to get a `NodeList` as response.
The `getXml` method returns the `org.w3c.dom.Document` value of the response.

```java
MagdaDocument document = ...

var value = document.getValue("//Geboorte/Datum");
var nodeList = document.xpath("//Voornamen/Voornaam");

var xml = document.getXml();
```