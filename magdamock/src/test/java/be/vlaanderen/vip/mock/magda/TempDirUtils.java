package be.vlaanderen.vip.mock.magda;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

public class TempDirUtils {
    
    public static File createDir(File dir, String name) throws IOException {
        var newDir = new File(dir, name);
        provideParentDirectories(newDir);
        return newDir;
    }
    
    public static File createFile(File dir, String path, String content) throws IOException {
        var file = new File(dir, path);
        provideParentDirectories(file.getParentFile());
        file.deleteOnExit();
        try {
            Files.writeString(file.toPath(), content);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write to file", e);
        }
        return file;
    }
    
    public static void createFileStructure(File dir, Map<?, ?> structure) throws IOException {
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

    private static void provideParentDirectories(File file) throws IOException {
        if(!file.exists() && !file.mkdirs()) {
            throw new IOException("Failed to make parent directories for file %s".formatted(file.toPath().toString()));
        }
    }
    
    public record FileDescriptor(String content) {
        
        public static FileDescriptor file() {
            return new FileDescriptor("");
        }
        
        public static FileDescriptor file(String content) {
            return new FileDescriptor(content);
        }
        
    }
}
