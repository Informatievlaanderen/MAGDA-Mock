package be.vlaanderen.vip.mock.magdaservice.config;

import be.vlaanderen.vip.magda.client.diensten.GeefAanslagbiljetPersonenbelastingRequest;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import be.vlaanderen.vip.mock.magda.client.simulators.SOAPSimulator;
import be.vlaanderen.vip.mock.magda.inventory.ResourceFinder;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.time.Year;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MagdaMockConnectionConfigTest {
    @TempDir
    private File dir;
    
    private MagdaMockConnectionConfig config;
    
    @BeforeEach
    void setup() {
        config = new MagdaMockConnectionConfig();
    }

    @Nested
    class ResourceFinderBean {
        
        @Nested
        class WithoutMockTestcasePath {
            private ResourceFinder finder;
            
            @BeforeEach
            void setup() throws URISyntaxException, IOException {
                finder = config.resourceFinder();
            }
            
            @Test
            void returnsValueFromMagdaSimulator() throws IOException {
                try(var result = finder.loadSimulatorResource("Persoon", "GeefBewijs/02.00.0000/00671031676.xml")) {
                    assertThat(new String(result.readAllBytes()), is(equalTo(getResourceContent("magda_simulator/Persoon/GeefBewijs/02.00.0000/00671031676.xml"))));
                }
            }
            
        }
        
        @Nested
        class WithMockTestcasePath {
            private ResourceFinder finder;
            
            @BeforeEach
            void setup() throws URISyntaxException, IOException {
                config.setMockTestcasePath(dir.getAbsolutePath());
                finder = config.resourceFinder();
            }
        
            @Test
            void returnsValueFromTestcasePath() throws IOException {
                // this file is also present in the simulator
                var file = new File(dir, "Persoon/GeefBewijs/02.00.0000/00671031647.xml");
                provideParentDirectories(file.getParentFile());
                Files.writeString(file.toPath(), "content");
                
                try(var result = finder.loadSimulatorResource("Persoon", "GeefBewijs/02.00.0000/00671031647.xml")) {
                    assertThat(new String(result.readAllBytes()), is(equalTo("content")));
                }
            }
            
            @Test
            void fallsbackToMagdaSimulator() throws IOException {
                try(var result = finder.loadSimulatorResource("Persoon", "GeefBewijs/02.00.0000/00671031676.xml")) {
                    assertThat(new String(result.readAllBytes()), is(equalTo(getResourceContent("magda_simulator/Persoon/GeefBewijs/02.00.0000/00671031676.xml"))));
                }
            }

            private void provideParentDirectories(File file) throws IOException {
                if(!file.exists() && !file.mkdirs()) {
                    throw new IOException("Failed to make parent directories for file %s".formatted(file.toPath().toString()));
                }
            }
        }
    }

    @Nested
    class SimulatorBean {
        @Test
        @SneakyThrows
        void simulatorBeanWithCopyPropertiesHasProperBehaviour() {
            config.setCopyPropertiesFromRequest(true);
            var simulator = config.simulator(config.resourceFinder(), new RegistratieConfig());

            var request = GeefAanslagbiljetPersonenbelastingRequest.builder2()
                    .insz("00610122309")
                    .incomeYear(Year.of(2021))
                    .build()
                    .toMagdaDocument(
                            MagdaRegistrationInfo.builder()
                            .identification("identification")
                            .build()
                    );

            var response = simulator.send(request);

            assertEquals(response.getValue("//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/Inkomensjaar"), "2021");
        }

        @Test
        @SneakyThrows
        void simulatorBeanWithoutCopyPropertiesHasProperBehaviour() {
            config.setCopyPropertiesFromRequest(false);
            var simulator = config.simulator(config.resourceFinder(), new RegistratieConfig());

            var request = GeefAanslagbiljetPersonenbelastingRequest.builder2()
                    .insz("00610122309")
                    .incomeYear(Year.of(2021))
                    .build()
                    .toMagdaDocument(
                            MagdaRegistrationInfo.builder()
                                    .identification("identification")
                                    .build()
                    );

            var response = simulator.send(request);
            assertEquals(response.getValue("//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/Inkomensjaar"), "2011");
        }
    }
    
    private String getResourceContent(String path) throws IOException {
        try(var stream = Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(path))) {
            return new String(stream.readAllBytes());
        }
    }
}
