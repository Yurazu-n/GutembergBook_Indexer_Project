package org.example.interfaces;

import java.io.IOException;

public interface FileManager {
    int readLastProcessedBookId(String filePath) throws IOException;

    void updateLastProcessedBookId(String filePath, int lastBookId) throws IOException;
}