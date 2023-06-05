package be.vlaanderen.vip.mock.magdaservice.config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import be.vlaanderen.vip.mock.magda.inventory.ResourceFinder;

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
            void setup() {
                finder = config.resourceFinder();
            }
            
            @Test
            void returnsValueFromMagdaSimulator() throws IOException, URISyntaxException {
                try(var result = finder.loadSimulatorResource("Persoon", "GeefBewijs/02.00.0000/00671031647.xml")) {
                    assertThat(new String(result.readAllBytes()), is(equalTo(getResourceContent("magda_simulator/Persoon/GeefBewijs/02.00.0000/00671031647.xml"))));
                }
            }
            
        }
        
        @Nested
        class WithMockTestcasePath {
            private ResourceFinder finder;
            
            @BeforeEach
            void setup() {
                config.setMockTestcasePath(dir.getAbsolutePath());
                finder = config.resourceFinder();
            }
        
            @Test
            void returnsValueFromTestcasePath() throws IOException {
                // this file is also present in the simulator
                var file = new File(dir, "Persoon/GeefBewijs/02.00.0000/00671031647.xml");
                file.getParentFile().mkdirs();
                Files.writeString(file.toPath(), "content");
                
                try(var result = finder.loadSimulatorResource("Persoon", "GeefBewijs/02.00.0000/00671031647.xml")) {
                    assertThat(new String(result.readAllBytes()), is(equalTo("content")));
                }
            }
            
            @Test
            void fallsbackToMagdaSimulator() throws IOException, URISyntaxException {
                try(var result = finder.loadSimulatorResource("Persoon", "GeefBewijs/02.00.0000/00671031647.xml")) {
                    assertThat(new String(result.readAllBytes()), is(equalTo(getResourceContent("magda_simulator/Persoon/GeefBewijs/02.00.0000/00671031647.xml"))));
                }
            }
            
        }
        
    }
    
    private String getResourceContent(String path) throws IOException, URISyntaxException {
        try(var stream = getClass().getClassLoader().getResourceAsStream(path)) {
            return new String(stream.readAllBytes());
        }
    }
}
