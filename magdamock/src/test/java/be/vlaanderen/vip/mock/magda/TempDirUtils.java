package be.vlaanderen.vip.mock.magda;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

public class TempDirUtils {
    
    public static File createDir(File dir, String name) {
        var newDir = new File(dir, name);
        newDir.mkdirs();
        return newDir;
    }
    
    public static File createFile(File dir, String path, String content) {
        var file = new File(dir, path);
        file.getParentFile().mkdirs();
        file.deleteOnExit();
        try {
            Files.writeString(file.toPath(), content);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write to file", e);
        }
        return file;
    }
    
    public static void createFileStructure(File dir, Map<?, ?> structure) {
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
