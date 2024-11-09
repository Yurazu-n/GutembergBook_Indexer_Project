package org.example.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.interfaces.JsonFileManager;
import org.example.model.Word;

import java.io.File;
import java.io.IOException;

public class JsonFileManagerControl implements JsonFileManager {

    private final ObjectMapper objectMapper;

    public JsonFileManagerControl() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Word readJson(String filePath) throws IOException {
        File file = new File(filePath);

        // Check if the file exists; if not, return null
        if (file.exists()) {
            return objectMapper.readValue(file, Word.class);
        }
        return null;
    }

    @Override
    public void writeJson(String filePath, Word word) throws IOException {
        File file = new File(filePath);

        // Write the Word object to the JSON file with pretty-printing
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, word);
    }
}
