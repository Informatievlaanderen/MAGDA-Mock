package be.vlaanderen.vip.mock.magdaservice.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

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
            
            @Test
            void returnsValueFromMagdaSimulator() throws IOException, URISyntaxException {
                try(var finder = config.resourceFinder()) {
                    try(var result = finder.loadSimulatorResource("Persoon", "GeefBewijs/02.00.0000/00671031647.xml")) {
                        assertThat(new String(result.readAllBytes()), is(equalTo(getResourceContent("magda_simulator/Persoon/GeefBewijs/02.00.0000/00671031647.xml"))));
                    }
                }
            }
        }
        
        @Nested
        class WithMockTestcasePath {
            
            @BeforeEach
            void setup() {
                config.setMockTestcasePath(dir.getAbsolutePath());
            }
        
            @Test
            void returnsValueFromTestcasePath() throws IOException, URISyntaxException {
                // this file is also present in the simulator
                var file = new File(dir, "Persoon/GeefBewijs/02.00.0000/00671031647.xml");
                provideParentDirectories(file.getParentFile());
                Files.writeString(file.toPath(), "content");

                try(var finder = config.resourceFinder()) {
                    try(var result = finder.loadSimulatorResource("Persoon", "GeefBewijs/02.00.0000/00671031647.xml")) {
                        assertThat(new String(result.readAllBytes()), is(equalTo("content")));
                    }
                }
            }
            
            @Test
            void fallsbackToMagdaSimulator() throws IOException, URISyntaxException {
                try(var finder = config.resourceFinder()) {
                    try(var result = finder.loadSimulatorResource("Persoon", "GeefBewijs/02.00.0000/00671031647.xml")) {
                        assertThat(new String(result.readAllBytes()), is(equalTo(getResourceContent("magda_simulator/Persoon/GeefBewijs/02.00.0000/00671031647.xml"))));
                    }
                }
            }

            private void provideParentDirectories(File file) throws IOException {
                if(!file.exists() && !file.mkdirs()) {
                    throw new IOException("Failed to make parent directories for file %s".formatted(file.toPath().toString()));
                }
            }
        }
    }
    
    private String getResourceContent(String path) throws IOException {
        try(var stream = Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(path))) {
            return new String(stream.readAllBytes());
        }
    }
}
