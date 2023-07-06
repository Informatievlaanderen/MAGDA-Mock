package be.vlaanderen.vip.mock.magda;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

public class InszStuff {
    
    @Test
    void validate() throws IOException {
        findRrrns().forEach(this::updateChecksum);
//        var result = findRrrns().stream()
//                   .map(this::getFileName)
//                   .peek(System.out::println)
//                   .allMatch(this::isRijksRegisterNumberChecksumOk);
////                   .forEach(this::updateChecksum);
//        System.out.println(result);
    }
    
    private void updateChecksum(Path path) {
        var ext = getExtension(path);
        var file = getFileName(path);
        var expected = correctInsz(file);
        
        try(var fis = new FileInputStream(path.toFile())) {
            var content = new String(fis.readAllBytes());
            var result = content.replace(file, expected);
            
            var targetFile = path.resolveSibling(fileName(expected, ext));
            
            System.out.println("Source file '%s".formatted(path));
            System.out.println("Replacing '%s' with '%s'".formatted(file, expected));
            System.out.println("Result file '%s'".formatted(targetFile));

            System.out.println();
            System.out.println(content);
            System.out.println();
            System.out.println(result);
            
            try(var fos = new FileOutputStream(targetFile.toFile())) {
                IOUtils.write(result.getBytes(), fos);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        path.toFile().delete();
        
    }
    
    private List<Path> findRrrns() throws IOException {
        return Stream.concat(Files.walk(simulatorResources()),
                             Files.walk(testResources()))
                     .filter(this::isRrnFile)
                     .filter(this::isY2Kbaby)
                     .toList();
    }
    
    private boolean isRrnFile(Path path) {
        var file = path.getFileName().toString().split("\\.")[0];
        if(file.matches("\\d{11}")) {
            return true;
        }
        return false;
    }
    
    
    private boolean isY2Kbaby(Path path) {
        var name = getFileName(path);
        var year = name.substring(0, 2);
        return Long.parseLong(year) <= 23;
    }
    
    private String fileName(String file, String ext) {
        return Stream.of(file, ext)
                     .filter(StringUtils::isNotBlank)
                     .collect(Collectors.joining("."));
    }
    
    private String getFileName(Path path) {
        return FilenameUtils.removeExtension(path.getFileName().toString()); 
    }
    
    private String getExtension(Path path) {
        return FilenameUtils.getExtension(path.getFileName().toString()); 
    }
    
    private Path simulatorResources() {
        return Path.of("src/main/resources/magda_simulator");
    }

    private Path testResources() {
        return Path.of("src/test/resources/simulator_test");
    }
    
    private String correctInsz(String rrn) {
        var sum = expectedChecksum(rrn);
        return String.valueOf(rrn.substring(0, 9) + ((sum < 10) ? "0" : "") + sum);
    }
    
    public boolean isRijksRegisterNumberChecksumOk(String rrn) {
        var rrnChecksum = Long.parseLong(rrn.substring(9, 11));
     
        var partToCalculate = rrn.substring(0, 9);
        partToCalculate = "2" + partToCalculate;
        var rrnInt = Long.parseLong(partToCalculate);
     
        var checksum = 97 - (rrnInt % 97);
     
        if (rrnChecksum == checksum) {
            return true;
        }
        else {
            return false;
        }
    }
    
    private Long expectedChecksum(String rrn) {
        var partToCalculate = rrn.substring(0, 9);
        partToCalculate = "2" + partToCalculate;
        var rrnInt = Long.parseLong(partToCalculate);
        return 97 - (rrnInt % 97);
    }
}
