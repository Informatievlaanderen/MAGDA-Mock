package be.vlaanderen.vip.mock.magda.inventory;

import java.io.File;
import java.io.IOException;

public abstract class AbstractResourceFinder implements ResourceFinder {

    protected boolean containsPathTraversal(String relativePath)
    {
        var file = new File(relativePath);

        String canonicalPath;
        String absolutePath;
        try {
            canonicalPath = file.getCanonicalPath();
            absolutePath = file.getAbsolutePath();
        }
        catch(IOException e) {
            return true;
        }

        return !canonicalPath.equals(absolutePath);
    }
}