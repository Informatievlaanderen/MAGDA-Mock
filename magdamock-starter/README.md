# Magda mock connector spring starter

This is a starter project that contains an `AutoConfiguration` for the `MagdaConnector` interface. Included in this project are the starters for `org.springframework.vault.core.VaultTemplate` and `brave.Tracing`. So make sure to exclude the auto configurations for this project or disable them in configuration if you don't want these to be included in your `MagdaConnector`:

```yaml application.yaml
spring:
  cloud.vault:
    enabled: false
  autoconfigure.exclude: 
    - org.springframework.cloud.vault.config.VaultAutoConfiguration
    - org.springframework.boot.actuate.autoconfigure.tracing.zipkin.ZipkinAutoConfiguration
    
# or

spring:
  cloud.vault:
    enabled: false

management:
  tracing:
    enabled: false
```

## Configuration

When using the `magdamock-starter`, a `MagdaConnector` will automatically be wired into your project.

### Mock connection

When configured to use the mock connection. The resulting `MagdaConnector` will resolve requests by fetching static resources from the classpath. The configuration for this is the following:

```yaml application.yaml
magda:
  connector:
    connection: Mock
```

### Remote connection

This kind of connection will execute soap request to a service that can handle these kind of `MagdaRequests`. For this we need to configure the `endpoints`, which is in charge of deciding how each request will be routed; and `registrations`, which will decide what `hoedanigheidscode` and `identification` will be sent with request based on the optional registration property of `MagdaRequest`. If no registration is given with request, the value `default` will be used.

```yaml application.yaml
magda:
  connector:
    magda-mock: https://magda-mock-base.url/api/Magda-02.00/soap/WebService
    connection: Remote
    registrations:
      default:
        identification: default-identification
        hoedanigheidscode: default-hoedanigheidscode
      other:
        identification: other-identification
        hoedanigheidscode: other-hoedanigheidscode
    endpoints: # example of possible endpoints
      - name: GeefAanslagbiljetPersonenbelasting
        version: 02.00.0000
        path: ${magda.connector.magda-mock}
      - name: GeefPersoon
        version: 02.02.0000
        path: ${magda.connector.magda-mock}
      - name: RegistreerInschrijving
        version: 02.00.0000
        path: ${magda.connector.magda-mock}
```

Its also possible to configure a keystore that should be used to sign request, this can either be done by including a keystore in your deployment or be making a reference using the `Vault`.

```yaml application.yaml: security with local file
magda:
  connector:
    wss:
      key-store-path: /path/to/keystore
      key-store-password: password-of-key-store
      key-alias: name-of-the-key-alias
      key-password: password-of-the-key
```

```yaml application.yaml: security with vault
magda:
  connector:
    vault:
      path: /path/where/key/store/fields/are/stored
      key-store-key: name-of-the-key-where-key-store-is
      key-store-password-key: name-of-the-key-where-key-store-password-is
      key-alias: name-of-the-key-where-key-alias-is
      key-password-key: name-of-the-key-where-key-password-is
```