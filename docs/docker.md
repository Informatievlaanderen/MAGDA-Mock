# How to use the image

The `magdamock` images come in the following format: `vlaamseoverheid-magda-docker-public.jfrog.io/magda/magdamock.service:${tag}`. Where tag matches the desired release version of the `magdamock`.

Lets kick it off with an example:
*docker-compose:*
```yaml
version: '3'
services:
  magda-mock:
    image: "vlaamseoverheid-magda-docker-public.jfrog.io/magda/magdamock.service:latest"
    ports:
      - "8080:8080"
    command: java -jar /app.jar
```

This docker compose will run the latest `magdamock` without any additional configuration.

## Configuration

### General

`magdamock` is powered by spring boot and includes a couple of spring boot (compliant) dependencies:
 - spring-boot-starter-web
 - spring-boot-starter-actuator
 - micrometer-core
 - micrometer-registry-prometheus
 - micrometer-tracing-bridge-brave
 - zipkin-reporter-brave

This means an `application.yml` can be added in a volume which allows for additional configuration.
*docker-compose:*
```yaml
version: '3'
services:
  magda-mock:
    image: "vlaamseoverheid-magda-docker-public.jfrog.io/magda/magdamock.service:latest"
    ports:
      - "8080:8081"
    volumes:
      - /volume/config:/config
    command: java -jar /app.jar --spring.config.location=/config/
```
*application.yml:*
```yaml
server:
  port: 8081
```

### Test cases

It's possible to extend the test-cases supplied by the application through configuration:
*docker-compose:*
```yaml
version: '3'
services:
  magda-mock:
    image: "vlaamseoverheid-magda-docker-public.jfrog.io/magda/magdamock.service:latest"
    ports:
      - "8080:8080"
    volumes:
      - /volume/test-cases:/test-cases
      - /volume/config:/config
    command: java -jar /app.jar --spring.config.location=/config/
```
*application.yml:*
```yaml
magdamock:
  mockTestcasePath: /test-cases
```

When run like this, in addition to it's default test cases, the `magdamock` will also search inside the */test-cases* folder for responses. The folder structure inside the configured folder is expected to be `{type}/{service}/{version}/{search-key}.xml`. Search key depends on the service being called, but usually is the INSZ number of the person the data is being request for. As for type/service/version, the following options are available:
 - Persoon/RegistreerInschrijving/02.00.0000 - filenames {INSZ}.xml
 - Persoon/RegistreerInschrijving/02.01.0000 - filenames {INSZ}.xml
 - Persoon/RegistreerUitschrijving/02.00.0000 - filenames {INSZ}.xml
 - Persoon/GeefBewijs/02.00.0000 - filenames {INSZ}.xml
 - Persoon/GeefHistoriekInschrijving/02.01.0000 - filenames {INSZ}.xml
 - Persoon/RaadpleegLeerkredietsaldo/01.00.0000 - filenames {INSZ}.xml
 - Persoon/GeefLoopbaanOnderbrekingen/02.00.0000 - filenames {INSZ}.xml
 - Persoon/GeefStatusRechtOndersteuningen/02.00.0000 - filenames {INSZ}.xml
 - Persoon/GeefFuncties/02.00.0000 - filenames {INSZ}.xml
 - Persoon/GeefDossiers/02.00.0000 - filenames {INSZ}.xml
 - Persoon/GeefKindVoordelen/02.00.0000 - filenames {INSZ}.xml
 - Persoon/GeefVolledigDossierHandicap/03.00.0000 - filenames {RRNR}.xml
 - Persoon/GeefPersoon/02.02.0000 - filenames {INSZ}.xml
 - Persoon/GeefHistoriekPersoon/02.00.0000 - filenames {INSZ}.xml
 - Persoon/GeefHistoriekPersoon/02.02.0000 - filenames {INSZ}.xml
 - Persoon/GeefGezinssamenstelling/02.00.0000 - filenames {INSZ}.xml
 - Persoon/GeefGezinssamenstelling/02.02.0000 - filenames {INSZ}.xml
 - Persoon/GeefDossierKBI/01.00.0000 - filenames {INSZ}.xml
 - Persoon/GeefAanslagbiljetPersonenbelasting/02.00.0000 - filenames {INSZ}.xml
 - Persoon/ZoekEigendomstoestanden/02.00.0000 - filenames {INSZ}.xml
 - Persoon/ZoekPersoonOpAdres/02.02.0000/{source}/{PostalCode}/{StreatCode}/{HouseNumber} - filenames {ReferencePerson}.xml
 - Persoon/GeefAttest/02.00.0000 - filenames {INSZ}.xml
 - Persoon/GeefPasfoto/02.00.0000 - filenames {INSZ}.xml
 - Onderneming/GeefOnderneming/02.00.0000 - filenames {VentureNumber}.xml
 - Onderneming/GeefOndernemingVKBO/02.00.0000 - filenames {VentureNumber}.xml
 - Vastgoed/GeefEpc/02.00.0000/{Certificate} - filenames {BuildingUnitId}.xml
 - Vastgoed/GeefEpc/02.01.0000/{Certificate}/{BuildingUnitId}/{PostalCode}/{Street} - filenames {HouseNumber}.xml

If the following folder structure is mounted for the magdamock:
```
test-cases
  Persoon
    GeefBewijs
      0123456789.xml
      4567980123.xml
```
and a `GeefBewijs` request has been made with INSZ number `012345679.xml`, the contents of */test-cases/Persoon/GeefBewijs/0123456789.xml* will be returned.

### Signing

It's possible to have request signature validation and response signature signing enabled by the magdamock:
*docker-compose:*
```yaml
version: '3'
services:
  magda-mock:
    image: "vlaamseoverheid-magda-docker-public.jfrog.io/magda/magdamock.service:latest"
    ports:
      - "8080:8080"
    volumes:
      - /volume/certificates:/certificates
      - /volume/config:/config
    command: java -jar /app.jar --spring.config.location=/config/
```
*application.yml:*
```yaml
registratie:
  certPath: /certificates/keystore
  keyAlias: alias
  keyPassword: key-password
  keystorePassword: keystore-password
```

This in turn will look for a keystore in */certifcates/keystore* and use it so verifies requests and signs responses.