package be.vlaanderen.vip.mock.magda.inventory;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ResourceLoaderTest {

    @Nested
    class GetResourceAsStream {

        @Test
        void getsSimpleResourceDirectoryStream() throws IOException {
            try(var loader = ResourceLoader.fromRootUri(composeSimpleDirRootUri())) {
                assertEquals("content2", streamToString(loader.getResourceAsStream("file2.txt")));
                assertEquals("content4", streamToString(loader.getResourceAsStream("baz/file4.txt")));
                assertEquals("content10", streamToString(loader.getResourceAsStream("baz/qux/file with spaces.txt")));
                assertNull(loader.getResourceAsStream("nonexistent.txt"));
            }
        }

        @Test
        void getsSimpleJarDirectoryStream() throws IOException {
            try(var loader = ResourceLoader.fromRootUri(composeSimpleJarRootUri())) {
                assertEquals("content2", streamToString(loader.getResourceAsStream("file2.txt")));
                assertEquals("content4", streamToString(loader.getResourceAsStream("baz/file4.txt")));
                assertEquals("content10", streamToString(loader.getResourceAsStream("baz/qux/file with spaces.txt")));
                assertNull(loader.getResourceAsStream("nonexistent.txt"));
            }
        }

        @Test
        void getsNestedJarDirectoryStream() throws IOException {
            try(var loader = ResourceLoader.fromRootUri(composeNestedJarRootUri())) {
                assertEquals("content2", streamToString(loader.getResourceAsStream("file2.txt")));
                assertEquals("content4", streamToString(loader.getResourceAsStream("baz/file4.txt")));
                assertEquals("content10", streamToString(loader.getResourceAsStream("baz/qux/file with spaces.txt")));
                assertNull(loader.getResourceAsStream("nonexistent.txt"));
            }
        }

        private String streamToString(InputStream is) throws IOException {
            return IOUtils.toString(is, StandardCharsets.UTF_8)
                          .trim();
        }
    }

    @Nested
    class GetResourceAsPath {

        @Test
        void getsSimpleResourceDirectoryPath() throws IOException {
            try(var loader = ResourceLoader.fromRootUri(composeSimpleDirRootUri())) {
                try(var stream = Files.walk(loader.getResourceAsPath("baz"), 1)) {
                    assertEquals(5, stream.toList().size());
                }
            }
        }

        @Test
        void getsSimpleJarDirectoryPath() throws IOException {
            try(var loader = ResourceLoader.fromRootUri(composeSimpleJarRootUri())) {
                try(var stream = Files.walk(loader.getResourceAsPath("baz"), 1)) {
                    assertEquals(5, stream.toList().size());
                }
            }
        }

        @Test
        void getsNestedJarDirectoryPath() throws IOException {
            try(var loader = ResourceLoader.fromRootUri(composeNestedJarRootUri())) {
                try(var stream = Files.walk(loader.getResourceAsPath("baz"), 1)) {
                    assertEquals(5, stream.toList().size());
                }
            }
        }

        @Test
        void getsNestedJarDirectoryPathMoreThanOnce() throws IOException { // makes sure that the FileSystems get closed...
            for(var i = 0; i < 2; i++) {
                try(var loader = ResourceLoader.fromRootUri(composeNestedJarRootUri())) {
                    try(var stream = Files.walk(loader.getResourceAsPath("baz"), 1)) {
                        assertEquals(5, stream.toList().size());
                    }
                }
            }
        }
    }

    private URI composeSimpleDirRootUri() {
        return composeRootUri("%s/bar", "resourceloader/simpledir");
    }

    private URI composeSimpleJarRootUri() {
        return composeRootUri("jar:%s!/bar", "resourceloader/simple.jar");
    }

    private URI composeNestedJarRootUri() {
        return composeRootUri("jar:%s!/foo/inner.jar!/bar", "resourceloader/outer.jar");
    }

    private URI composeRootUri(String formatString, String resource) {
        return URI.create(formatString.formatted(Objects.requireNonNull(getClass().getClassLoader().getResource(resource))));
    }
}