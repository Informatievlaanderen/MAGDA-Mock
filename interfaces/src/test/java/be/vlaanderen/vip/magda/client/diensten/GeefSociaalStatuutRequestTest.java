package be.vlaanderen.vip.magda.client.diensten;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.OffsetDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import be.vlaanderen.vip.magda.client.diensten.GeefSociaalStatuutRequest.Builder;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;

class GeefSociaalStatuutRequestTest {

    @Nested
    class GeefSociaalStatuutRequestBuilder {
        
        @Test
        void throwsException_whenInszNull() {
            assertThrows(IllegalStateException.class, 
                         () -> GeefSociaalStatuutRequest.builder()
                                                        .sociaalStatuut("sociaal-statuut")
                                                        .datum(OffsetDateTime.now())
                                                        .build());
        }

        @Test
        void throwsException_whenSociaalStatuutNull() {
            assertThrows(IllegalStateException.class, 
                         () -> GeefSociaalStatuutRequest.builder()
                                                        .insz("insz")
                                                        .datum(OffsetDateTime.now())
                                                        .build());
        }

        @Test
        void throwsException_whenDatumNull() {
            assertThrows(IllegalStateException.class, 
                         () -> GeefSociaalStatuutRequest.builder()
                                                        .insz("insz")
                                                        .sociaalStatuut("sociaal-statuut")
                                                        .build());
        }
        
        @Test
        void isOk_whenRequiredPropertiesPresent() {
            assertDoesNotThrow(() -> GeefSociaalStatuutRequest.builder()
                                                              .insz("insz")
                                                              .sociaalStatuut("sociaal-statuut")
                                                              .datum(OffsetDateTime.now())
                                                              .build());
        }
        
    }
    
    @Nested
    class ToMagdaDocument {
        private MagdaRegistrationInfo info;
        private Builder builder;
        
        @BeforeEach
        void setup() {
            info = MagdaRegistrationInfo.builder()
                                        .identification("identification")
                                        .hoedanigheidscode("identification")
                                        .build();
            
            builder = GeefSociaalStatuutRequest.builder()
                                               .insz("insz")
                                               .sociaalStatuut("sociaal-statuut")
                                               .datum(OffsetDateTime.now());
        }
        
        @Test
        void setsSociaalStatuus() {
            var request = builder.build()
                                 .toMagdaDocument(info);
            
            assertThat(request.getValue("//SociaalStatuut/Naam"), is(equalTo("sociaal-statuut")));
        }

        @Test
        void setsDatum() {
            var request = builder.datum(OffsetDateTime.parse("2023-01-22T00:00:00.000+00:00"))
                                 .build()
                                 .toMagdaDocument(info);
            
            assertThat(request.getValue("//SociaalStatuut/Datum/Datum"), is(equalTo("2023-01-22")));
        }
    }
    
}
