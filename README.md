<h1><img src="docs/img/magda_logo.png" height="80" align="center">  MAGDA Mock</h1>

![GitHub tag (latest by date)](https://img.shields.io/github/v/tag/Informatievlaanderen/MAGDA-Mock?label=version)
![GitHub issues](https://img.shields.io/github/issues/InformatieVlaanderen/MAGDA-Mock)
![GitHub](https://img.shields.io/github/license/InformatieVlaanderen/MAGDA-mock)

MAGDA Mock is a project by Digitaal Vlaanderen's MAGDA Platform to offer a mock environment for customers of its SOAP v3 and v2 services, 
without real personal identifiable information and with stable and consistent test cases across services.

The MAGDA Mock is available as a Docker image, library and the project also offers a SOAP and Mock connector which
can be used to connect to MAGDA Acceptance and Production environments after testing with the mock.

### Repository structure ###
#### `interfaces` module
Defines the interface to which both a "real" signed MAGDA connection in acceptance (TNI) or production will adhere ([`MagdaSoapConnection`](magdaconnector/src/main/java/be/vlaanderen/vip/magda/client/MagdaSignedConnection.java) and [`MagdaSignedConnection`](magdaconnector/src/main/java/be/vlaanderen/vip/magda/client/MagdaSoapConnection.java)) and which is also
followed by the MAGDA Mock library ([`MagdaMockConnection`](magdamock/src/main/java/be/vlaanderen/vip/mock/magda/client/MagdaMockConnection.java)).

Additionally, the module defines templates for the interfaces of the MAGDA SOAP Services which can be addressed through the MAGDA Mock in order to facilitate the use.

#### `magdaconnector` module
Implements a Signed MAGDA SOAP connection to v2 or v3 services, following the `MagdaConnector` interface from the `interfaces` package.
Supports certificate based authentication through mTLS and SOAP message signing following the guidelines on the [MAGDA Gebruikersomgeving](https://vlaamseoverheid.atlassian.net/wiki/spaces/MG/pages/485982282/Certificaten).

_Your application code can use this module to integrate with a real MAGDA environment like acceptance or production for SOAP services._

#### `magdamock` module
Implements a mocked MAGDA connection for a number of services with respect to 
[`Persoon`](magdamock/src/main/resources/magda_simulator/Persoon) (services related to a citizen, 
like [GeefPersoon-02.02](https://vlaamseoverheid.atlassian.net/wiki/spaces/MG/pages/1093927442/Persoon.GeefPersoon-02.02)) 
and [`Onderneming`](magdamock/src/main/resources/magda_simulator/Onderneming) (services related to the registration of enterprises).

This module stores the test cases used to respond to these requests for personas in its resources, see [`magda_simulator`](magdamock/src/main/resources/magda_simulator).

Not all MAGDA SOAP services currently have test data, MAGDA customers can request for additional test cases through their aansluitingsbeheerder or via the MAGDA Service desk.
Pull requests to add additional test data are also appreciated, but will be subject to approval and availability of the MAGDA Mock team.

_The unit testing in your application can use this module to integrate with a MAGDA mock environment for SOAP services._

#### `magdaservice` module
The MAGDA service integrates with the `magdamock` module to offer a Spring Boot application which exposes the endpoint `POST Magda-02.00/soap/WebService` that accepts
a valid SOAP request payload for any MAGDA SOAP service for which test cases have been defined in the `magdamock` module.

For the response a matching will be attempted with the service and its test case for the specified test persona.

The `magdaservice` module also builds a [Docker image](https://github.com/Informatievlaanderen/MAGDA-Mock/pkgs/container/magda-mock), which is provided through Github packages and can be run locally to mimick the working of MAGDA Platform in your environment.

### How do I get set up? ###

The project has no external dependencies, other than the libraries specified in pom.xml

No database or queueing or other infrastructure necessary.

### Who do I talk to? ###

* **For external users:** Raise a technical issue, i.e. bugs or inconsistencies in test cases, on Github or request for additional test cases or services through the MAGDA Service desk
* **For internal users:** Talk to the MAGDA Solutions Architects.

### Acknowledgements ###
Original connector code and some test cases adapted from [Mijn Burgerprofiel](https://www.burgerprofiel.be/).

## Testcases
Testcases in the MAGDA Mock follow the principle of test personas, where we try to provide consistent information (or edge cases)
for the persona across multiple services in the MAGDA Mock.

### TST-numbers
In order to not conflict with actual citizen identifiers in the MAGDA Mock (i.e. INSZ-numbers), we define so-called TST-numbers for the test cases of services pertaining to personal identifiable information of citizens.

These TST-numbers have the same basic format as an INSZ-number (`^{0-9}[11]$`), and succeed under the [modulo 97 checksum](https://vlaamseoverheid.atlassian.net/wiki/spaces/MG/pages/6208324471/Validatiemethodes).
An important difference is that, unlike for a national registry number (rijkregisternummer), the third and fourth number do not form a valid month in the range 01 through 12 instead they lie in the range 61 through 72 in order to
discourage processing of the number for the purpose of obtaining the date of birth of the individual and to avoid collisions with real citizen identifiers.


### Consistency of test cases
We try to ensure that all TST-numbers mentioned in test cases at least are present in the `Persoon.GeefPersoon-02.02` mock service, such
that a minimal set of information on the test persona is available. If this is not the case, please raise an issue and we will look to resolve this.

However, if your use case requires additional test data for a persona outside of the GeefPersoon-service, it is considered a feature
request and will have to be sent through the MAGDA Service Desk.

## Development ##

### Simulators ###

The `MagdaMockConnection` responds to requests using a structure of `ISOAPSimulator`s.
This structure is set up in the `constructBuiltInSimulator` method; when developing the mock, this is your entry point.
`ISOAPSimulator`s are a set of classes which take a request `MagdaDocument` and return a response `MagdaDocument`.
This response can be a document containing data, or a document reporting an error or the result of an action.
If something goes wrong during the handling of the request, a `MagdaSendFailed` is thrown.
The simulators are designed to be composable, and each subtype handles a specific aspect of formulating a response.
Each type of simulator is explained below.

#### SignatureVerifyingSimulator and SigningSimulator ####

These simulators operate on the general SOAP level rather than the specific MAGDA level, and therefore precede the `CombinedSimulator` (see below).
They are optionally added depending on whether WSS is enabled in the configuration. They will validate the signature in the request and sign the response, respectively, based on the configured keystore.
If the `SignatureVerifyingSimulator` fails to verify the request, it yields a SOAP error response.
If the `SigningSimulator` fails to sign the response (for whatever technical reason), `MagdaSendFailed` is thrown.

#### CombinedSimulator ####

This simulator acts as a hub to other simulators. It maps a set of service/version pairs (e.g., service "GeefBewijs", version "02.00.0000") to a set of simulators which
handle the response for each respective service and version. During the setup of the mock connection, simulators for specific services and versions are registered
under it.

The service and version are extracted from the request document with the respective xpath expressions `//Verzoek/Context/Naam` and `//Verzoek/Context/Versie`.
The request document will then be passed down to the simulator registered under that pair. If no service is registered, a `MagdaSendFailed` exception is thrown.

#### StaticResponseSimulator ####

This simulator loads one of the xml files bundled in the resources under the directory `magda_simulator` and performs value substitutions on some fields contained within.
It is instantiated for a type (e.g. "Persoon" or "Onderneming"), and a list of keys.

The keys are xpath expressions that will be used to read values out of certain request fields (e.g., "//INSZ"), and they determine a part of the path that the document is loaded from.
The ordering of the list is significant.

##### Loading the document #####

The respective values that accord to the keys will be read out of the request document from the xpath expression represented by that key (denoted below by the name `val`).
The document will then be loaded from the according resource path, `magda_simulator/<type>/<service>/<version>/<val-1>/<val-2>/.../<val-n>.xml`.
The length of the list of values is arbitrary, and the depth of the directory hierarchy depends therefore on the number of keys that the static resource simulator has.
Note that the last value in the list does not accord to a directory, but to the base name of the xml file.

Once the document is loaded, a number of general substitutions will be performed based on certain values from the request; see the method `SOAPSimulator::PatchResponse`.

##### Fallback behavior #####

There is a fallback mechanism in case a document is not found at its expected path. Firstly, the simulator will try to find it higher up in the directory hierarchy.
For instance, if a document is not found at path `magda_simulator/<type>/<service>/<version>/<val-1>/<val-2>.xml`, it will be looked for at the following paths, in the given order:
`magda_simulator/<type>/<service>/<version>/<val-2>.xml`
`magda_simulator/<type>/<service>/<val-2>.xml`
`magda_simulator/<type>/<val-2>.xml`
`magda_simulator/<val-2>.xml`
This is interesting because it permits an arbitrary level of flexibility per individual case in how granular the different responses can be, so that the bundled documents don't need to be needlessly duplicated.

If the simulator fails to find the target document anywhere, it will start looking for different documents, using the same hierarchical logic. These documents are `notfound.xml` and `success.xml`, respectively.

If none of the fallback options yield any document, a `MagdaSendFailed` exception is thrown.

#### RandomPasfotoSimulator ####

This is a simulator that is somewhat similar to the `StaticResponseSimulator`, but its functionality is modified for returning passport photos specifically, and it has some logic built in to do that.
As will become evident from the explanation below, the functionality of this class is currently very specifically tailored toward its currently existing usages.

This simulator assumes that the list of keys it's instantiated for is not empty, and that the first in the list of keys is an INSZ number.  The rest of the keys in the list is ignored.
It will use an algorithm to extract information on the person's (binary) gender and their date of birth from the INSZ number.

Based on this, a passport photo is pseudo-randomly selected from one of two directories, `mannen` or `vrouwen`.
The `mannen` directory is expected to contain 6 files: `0.xml`, `1.xml`, ..., `5.xml`.
The `vrouwen` directory is expected to contain 4 files: `0.xml`, `1.xml`, ..., `3.xml`.
The number is selected from the date of birth modulo 6 or 4. In this manner, the simulator yields a pseudo-random passport photo document, while remaining idempotent for each INSZ number.
If none of the possible documents is not found, `MagdaSendFailed` is thrown.

The document will be looked for at the resource path: `magda_simulator/<type>/<service>/<version>/<gender>/<n>.xml`.
There is no hierarchical fallback logic, but a `notfound.xml` and a `succes.xml` [sic] file will be looked for on the level of the gender directory.

The passport photo is not an actual image file, but a regular MAGDA document containing a field with base64 encoded data that you need to provide.

The substitution logic in `SOAPSimulator::PatchResponse` will be performed on the found document, as in the `StaticResourceSimulator`.
Beside that, the INSZ number (in field `//Antwoorden/Antwoord/Inhoud/Pasfoto/INSZ`) will also be substituted, by reading it out of the request field corresponding to the xpath expression that the key represents.

### XSD validations ###

When adding a new instance of a resource simulator, xml files will need to be added to the `magda_simulator` directory.
On the JUnit test runtime level, there is a check in place to ensure that any documents in that directory are structured correctly by conforming it to an according XSD file.
Make sure that the document is conformant to the according XSD (otherwise the build will fail), but also make sure that any new subdirectory added to `magda_simulator` has an according XSD to match its content against.
To do this, check if the subdirectory falls under any of the subdirectories listed in `SimulatorXmlValidation::XML_FOLDERS_AND_XSDS`, and if not, add one to that list if possible.

_Source of the XSD files:_ https://vlaamseoverheid.atlassian.net/wiki/spaces/MG/pages/487620609/Overzicht+testdata+endpoints+en+XSD+s#Diensten-alle-versies-samen