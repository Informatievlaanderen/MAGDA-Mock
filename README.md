# README #

## General information ##

This is a MAGDA mock, which is available both as a service and a library.

This project also includes a MAGDA Soap connector and a mock connector

### Repository structure ###

Your application ==> magdaconnector ==> interfaces

Your unit tests ==> magdamock ==> interfaces

magdaservice (called via magdaconnector or your own connector) ==> magdamock ==> interfaces

See [Architecture](https://vlaamseoverheid.atlassian.net/wiki/spaces/MARCHT/pages/6170676392/MAGDA+Afnemer+Mock+-+Architectuur)

### How do I get set up? ###

The project has no external dependencies, other than the libraries specified in pom.xml

No database or queueing or other infrastructure necessary.

### Who do I talk to? ###

* MAGDA solution architecture team
* SOLID team (the first users of the mock)

### Documentation ###

* [Functional analysis](https://vlaamseoverheid.atlassian.net/wiki/spaces/MARCHT/pages/6083871398/MAGDA+afnemer+Mock+vs+MAGDA+developer+Mock)
* [Architecture + HOWTO nieuwe testcases toevoegen](https://vlaamseoverheid.atlassian.net/wiki/spaces/MARCHT/pages/6170676392/MAGDA+Afnemer+Mock+-+Architectuur)
* [Code repository](https://bitbucket.org/vlaamseoverheid/magdamock.service/src/main/)
* [Infra repository](https://bitbucket.org/vlaamseoverheid/magdamock.service-infra/src/master/)

### Acknowledgements ###

Connector and mock extracted from [Mijn Burgerprofiel](https://www.burgerprofiel.be/)

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

Source of the XSD files: https://vlaamseoverheid.atlassian.net/wiki/spaces/MG/pages/487620609/Overzicht+testdata+endpoints+en+XSD+s#Diensten-alle-versies-samen