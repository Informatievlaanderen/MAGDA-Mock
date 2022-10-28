# README #

This is a MAGDA mock, which is available both as a service and a library.

This project also includes a MAGDA Soap connector and a mock connector

### Repository structure ###

Your application ==> magdaconnector ==> interfaces 

Your unit tests ==> magdamock ==> interfaces

magdaservice (called via magdaconnector or your own connector) ==> magdamock ==> interfaces


### How do I get set up? ###

The project has no external dependencies, other than the libraries specified in pom.xml

No database or queueing or other infrastructure necessary.

### Who do I talk to? ###

* MAGDA solution architecture team
* SOLID team (the first users of the mock)

### Documentation ###

* [Functional analysis](https://vlaamseoverheid.atlassian.net/wiki/spaces/MARCHT/pages/6083871398/MAGDA+afnemer+Mock+vs+MAGDA+developer+Mock)
* [Architecture](https://vlaamseoverheid.atlassian.net/wiki/spaces/MARCHT/pages/6170676392/MAGDA+Afnemer+Mock+-+Architectuur)
* [Code repository](https://bitbucket.org/vlaamseoverheid/magdamock.service/src/main/)
* [Infra repository](https://bitbucket.org/vlaamseoverheid/magdamock.service-infra/src/master/)


### Acknowledgements ###

Connector and mock extracted from [Mijn Burgerprofiel](https://www.burgerprofiel.be/)