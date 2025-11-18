package be.vlaanderen.vip.magda.restclient.integration;

import be.vlaanderen.vip.magda.client.MagdaServiceIdentification;
import be.vlaanderen.vip.magda.client.diensten.MobilityRegistrationRequest;
import be.vlaanderen.vip.magda.client.domain.mobility.MobilityRegistrationJsonAdapter;
import be.vlaanderen.vip.magda.client.domain.mobility.MobilityRegistrationService;
import be.vlaanderen.vip.magda.client.domain.mobility.Registration;
import be.vlaanderen.vip.magda.client.domain.mobility.RestMobilityRegistrationService;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import be.vlaanderen.vip.magda.client.endpoints.MagdaEndpoint;
import be.vlaanderen.vip.magda.client.endpoints.MagdaEndpoints;
import be.vlaanderen.vip.magda.client.rest.MagdaRestClient;
import be.vlaanderen.vip.magda.restclient.MagdaRestClientBuilder;
import com.github.tomakehurst.wiremock.WireMockServer;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * In this test class we will test the RestMobilityRegistrationService with the MagdaRestClient implementation as defined in this module.
 */
public class MobilityRegistrationRestIntegrationTest {
    @Test
    @SneakyThrows
    public void testMobilityRegistrationsEndpoint_returnsRegistrationsList() {
        String input = """
                {
                    "@context": "https://vlaamseoverheid.atlassian.net/wiki/download/attachments/1693680273/Mobility-context.jsonld",
                    "self": "/registrations?vin=WVWZZZAUZLP551560",
                    "items": [
                        {
                            "license": {
                                "plateNr": "1ABC123",
                                "plateUID": "0282335146",
                                "plateType": {
                                    "code": "NM",
                                    "description": "Normaal"
                                },
                                "languageCode": "NL",
                                "startSituationDate": "2020-02-20T00:00:00Z",
                                "lastRegistrationDate": "2020-02-20T00:00:00Z",
                                "status": {
                                    "code": "REG",
                                    "description": "Ingeschreven"
                                },
                                "statusLegal": {
                                    "code": "NOR",
                                    "description": "Normaal"
                                },
                                "lastUpdateDateTime": "2020-02-20T09:25:57.303Z"
                            },
                            "certificates": [
                                {
                                    "id": "374424351",
                                    "beginDate": "2020-02-19T23:00:00Z",
                                    "statusLegal": {
                                        "code": "NOR",
                                        "description": "Normaal"
                                    }
                                }
                            ],
                            "titular": {
                                "@id": "_:100",
                                "@type": "http://purl.org/dc/terms/Agent",
                                "type": "P",
                                "source": "RRN",
                                "person": {
                                    "@id": "_:100",
                                    "@type": "http://www.w3.org/ns/person#Person",
                                    "nationalNr": {
                                        "identificator": "71640618918"
                                    },
                                    "lastName": [
                                        "Serruys"
                                    ],
                                    "firstName": [
                                        "Anna"
                                    ],
                                    "birthYear": "1971"
                                },
                                "contactInfo": {
                                    "@type": "https://schema.org/ContactPoint",
                                    "registrationAddress": {
                                        "@type": "http://www.w3.org/ns/locn#Address",
                                        "streetName": {
                                            "string": "Vandenschrieckstraat"
                                        },
                                        "houseNumber": "101",
                                        "postalCode": "1500",
                                        "cityNisCode": "23027",
                                        "cityName": {
                                            "string": "Halle"
                                        }
                                    },
                                    "address": {
                                        "@type": "http://www.w3.org/ns/locn#Address",
                                        "streetName": {
                                            "string": "Vandenschrieckstraat"
                                        },
                                        "houseNumber": "101",
                                        "postalCode": "1500",
                                        "cityNisCode": "23027",
                                        "cityName": {
                                            "string": "Halle"
                                        }
                                    }
                                }
                            },
                            "transaction": {
                                "typeCode": "REG",
                                "subTypeCode": {
                                    "code": "REGNM",
                                    "description": "Normale inschrijving"
                                },
                                "dateTime": "2020-02-20T09:25:57.303Z",
                                "info": {
                                    "plateNumberPriorTitular": "",
                                    "importedVehicle": false
                                }
                            },
                            "vehicle": {
                                "vin": "WVWZZZAUZLP551560",
                                "unifier": "01",
                                "category": {
                                    "code": "M1",
                                    "description": "Voertuigen bestemd voor personenvervoer, ten hoogste 8 zitplaatsen (zonder bestuurder)"
                                },
                                "makeName": "VOLKSWAGEN, VW",
                                "commercialName": "GOLF",
                                "firstRegistrationDate": "2020-02-20",
                                "firstKnownUseDate": "2020-02-20",
                                "type": "AUV",
                                "wvta": "e1*2007/46*0627*40",
                                "variant": "VACDKRFX0",
                                "version": "FM6FM6AJ019N4BFON1VL1BVR2A",
                                "dateCOC": "2020-01-29",
                                "status": {
                                    "code": "NOR",
                                    "description": "normaal"
                                },
                                "statusLegal": {
                                    "code": "NOR",
                                    "description": "Normaal"
                                },
                                "statusAdministrative": {
                                    "code": "NOR",
                                    "description": "normaal"
                                },
                                "customsStickerCode": "3",
                                "distanceType": "K",
                                "techControlValidityDate": "2024-02-20",
                                "techControlEntryDatetime": "2020-02-25T16:13:11Z",
                                "codeForBodyWork": [
                                    {
                                        "code": "AC",
                                        "description": "STATIONWAGEN"
                                    }
                                ],
                                "kind": {
                                    "code": "AC",
                                    "description": "STATIONWAGEN"
                                },
                                "colour": [
                                    {
                                        "code": "8",
                                        "description": "Grijs"
                                    }
                                ],
                                "technicPermissibleMaxMass": 1850,
                                "massOfTheVehicleInRunningOrder": 1300,
                                "referenceMass": 1325,
                                "actualMassOfTheVehicle": 1366,
                                "numberOfAxles": 2,
                                "wheelBase": 2620,
                                "maximumSpeed": 200,
                                "limiterIndicator": false,
                                "handicapIndicator": false,
                                "tachoIndicator": false,
                                "assistanceWithoutPedallingIndicator": false,
                                "nrOfSeatingPositions": 5,
                                "suspension": {
                                    "code": "1",
                                    "description": "Niet pneumatische ophanging"
                                },
                                "exhaustDirectiveRefCode": "715/2007*2018/1832DG",
                                "engine": [
                                    {
                                        "engineCapacity": 999.0,
                                        "fuel": [
                                            {
                                                "code": "10",
                                                "description": "Benzine",
                                                "maximumNetPower": 85.0,
                                                "euroNormCode": "6ei",
                                                "combinedFuelConsumption": 4.9,
                                                "combinedCO2": 113,
                                                "WLTPCombinedCO2": 131
                                            }
                                        ]
                                    }
                                ]
                            }
                        }
                    ],
                    "total": 1
                }
                """;
        MagdaServiceIdentification dienst = new MagdaServiceIdentification("mobility-registrations", "00.01");
        WireMockServer wireMockServer = new WireMockServer(0);
        wireMockServer.start();
        wireMockServer.stubFor(get(urlEqualTo("/v1/mobility/registrations?plateNr=1XNN230"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(input)
                ));
        MagdaEndpoints endpoints = MagdaEndpoints.builder()
                .addMapping(dienst.getName(), dienst.getVersion(), MagdaEndpoint.of(wireMockServer.baseUrl() + "/v1/mobility/registrations"))
                .build();
        MagdaRestClient client = new MagdaRestClientBuilder().withEndpoints(endpoints).build();
        MobilityRegistrationService mobilityRegistrationService = new RestMobilityRegistrationService(client, new MobilityRegistrationJsonAdapter());
        List<Registration> registrations = mobilityRegistrationService.getRegistrations(
                MobilityRegistrationRequest.builder()
                        .plateNr("1XNN230")
                        .registrationInfo(MagdaRegistrationInfo.builder().identification("identificatie").hoedanigheidscode("HC").build())
                        .correlationId(UUID.fromString("afbf89cc-b8bc-11f0-a0f2-04cf4b22694c"))
                        .enduserId("00000000097")
                        .build());
        assertEquals(1, registrations.size());
        Registration registration = registrations.get(0);
        Registration.Titular titular = registration.getTitular();
        assertNotNull(titular);
        Registration.Titular.Person person = titular.getPerson();
        assertNotNull(person);
        Registration.Titular.Person.NationalNr nationalNr = person.getNationalNr();
        assertNotNull(nationalNr);
        assertEquals("71640618918", nationalNr.getIdentificator());
        wireMockServer.stop();
    }
}
