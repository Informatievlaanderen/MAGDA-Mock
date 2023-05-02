package be.vlaanderen.vip.mock.magda;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import lombok.Getter;

/**
 * Instead of using @TempDir create a util class that has commonly required file operations
 */
public class TempDirExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {
    private static final String TEMP_DIR = "temp-dir";
    
    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        context.getStore(Namespace.GLOBAL).put(TEMP_DIR, Files.createTempDirectory(UUID.randomUUID().toString()).toFile());
    }
    
    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        FileUtils.deleteDirectory(tempDir(context));
    }

    @Override
    public boolean supportsParameter(
            ParameterContext parameterContext, 
            ExtensionContext extensionContext) throws ParameterResolutionException {
        return isTempDirectory(parameterContext);
    }

    @Override
    public Object resolveParameter(
            ParameterContext parameterContext, 
            ExtensionContext extensionContext) throws ParameterResolutionException {
        if(isTempDirectory(parameterContext)) {
            return new TempDirectory(tempDir(extensionContext));
        }
        return null;
    }
    
    private File tempDir(ExtensionContext extensionContext) {
        return (File) extensionContext.getStore(Namespace.GLOBAL).get(TEMP_DIR);
    }
    
    private boolean isTempDirectory(ParameterContext parameterContext) {
        return getType(parameterContext).equals(TempDirectory.class);
    }
    
    private Class<?> getType(ParameterContext ctx) {
        return ctx.getParameter().getType();
    }
    
    public static class TempDirectory {
        @Getter
        private File dir;
        
        public TempDirectory(File dir) {
            this.dir = dir;
        }
        
        public File createDir(String name) {
            return createDir(dir, name);
        }
        
        public File createFile(String path, String content) {
            return createFile(dir, path, content);
        }
        
        public void createFileStructure(Map<Object, Object> structure) {
            createFileStructure(dir, structure);
        }
        
        private File createDir(File dir, String name) {
            var newDir = new File(dir, name);
            newDir.mkdirs();
            return newDir;
        }
        
        private File createFile(File dir, String path, String content) {
            var file = new File(dir, path);
            file.getParentFile().mkdirs();
            try {
                Files.writeString(file.toPath(), content);
            } catch (IOException e) {
                throw new RuntimeException("Failed to write to file", e);
            }
            return file;
        }
        
        private void createFileStructure(File dir, Map<?, ?> structure) {
            for(var entry : structure.entrySet()) {
                if(entry.getValue() instanceof Map<?, ?> sub) {
                    var subDir = createDir(dir, entry.getKey().toString());
                    createFileStructure(subDir, sub);
                }
                else if(entry.getValue() instanceof FileDescriptor fd) {
                    createFile(dir, entry.getKey().toString(), fd.content());
                }
            }
        }
        
        public static record FileDescriptor(String content) {
            
            public static FileDescriptor file() {
                return new FileDescriptor("");
            }
            
            public static FileDescriptor file(String content) {
                return new FileDescriptor(content);
            }
            
        }
        
    }

}
