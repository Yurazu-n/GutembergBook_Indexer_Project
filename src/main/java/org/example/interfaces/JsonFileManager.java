package org.example.interfaces;

import org.example.model.Word;
import java.io.IOException;

public interface JsonFileManager {
    Word readJson(String filePath) throws IOException;

    void writeJson(String filePath, Word word) throws IOException;
}
