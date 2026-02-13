package be.vlaanderen.vip.mock.magda.config;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.direct.DirectCallHttpServerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.FileCopyUtils;

import java.nio.file.Files;
import java.nio.file.Path;

public class EmbeddedWireMockBuilder {
    
    public static WireMockData wireMockServer() {
        return wireMockServer(0);
    }

    private static String unpackWireMockResources() {
        try {
            Path tempDir = Files.createTempDirectory("magdamock-rest-wiremock");

            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath*:wiremock/**/*");

            for (Resource resource : resources) {
                if (resource.exists() && resource.isReadable() && resource.getURL().getPath().endsWith(".json")) {
                    String fullPath = resource.getURL().getPath();

                    String wiremockPrefix = "wiremock/";
                    String relativePath = fullPath.substring(fullPath.indexOf(wiremockPrefix) + wiremockPrefix.length());

                    if (relativePath.isEmpty()) continue;

                    Path target = tempDir.resolve(relativePath);
                    Files.createDirectories(target.getParent());

                    try (var is = resource.getInputStream()) {
                        FileCopyUtils.copy(is, Files.newOutputStream(target));
                    }
                }
            }
            return tempDir.toAbsolutePath().toString();
        } catch (Exception e) {
            throw new RuntimeException("Unable to provide files for wiremock", e);
        }
    }

    public static WireMockData wireMockServer(Integer wiremockPort) {
        String fileSource = unpackWireMockResources();
        DirectCallHttpServerFactory factory = new DirectCallHttpServerFactory();

        WireMockConfiguration config = WireMockConfiguration.wireMockConfig()
                .port(wiremockPort)
                .httpServerFactory(factory)
                .usingFilesUnderDirectory(fileSource);

        return new WireMockData(new WireMockServer(config), factory);
    }
}