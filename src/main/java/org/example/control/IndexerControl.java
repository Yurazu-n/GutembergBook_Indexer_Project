package org.example.control;

import org.example.interfaces.*;
import org.example.model.BookAllocation;
import org.example.model.Position;
import org.example.model.Word;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class IndexerControl implements BookIndexer {

    private final LastBookManager lastBookManager;
    private final JsonFileManager jsonFileManager;
    private final WordCleaner wordCleaner;
    private final WordLemmatizer wordLemmatizer;

    public IndexerControl() {
        this.lastBookManager = new BookManagerControl();
        this.jsonFileManager = new JsonFileManagerControl();
        this.wordCleaner = new WordCleanerControl();
        this.wordLemmatizer = new WordLemmatizerControl();
    }

    @Override
    public void indexBook(int bookId) throws IOException {
        String bookFilePath = "datalake/books/" + bookId + ".txt";

        List<String> lines = Files.readAllLines(Paths.get(bookFilePath));
        Map<String, Word> wordMap = new HashMap<>();

        // Process each line in the book file
        for (int lineNumber = 0; lineNumber < lines.size(); lineNumber++) {
            String line = lines.get(lineNumber);
            String[] words = line.split("\\s+");

            for (int position = 0; position < words.length; position++) {
                String cleanedWord = wordCleaner.cleanWord(words[position]);
                String lemma = wordLemmatizer.lemmatize(cleanedWord);

                if (!lemma.isEmpty()) {
                    Position pos = new Position(lineNumber + 1, position + 1);
                    String bookKey = "BookID_" + bookId;

                    wordMap.computeIfAbsent(lemma, k -> new Word(lemma, new HashMap<>(), 0))
                            .getAllocations()
                            .computeIfAbsent(bookKey, k -> new BookAllocation(0, new ArrayList<>()))
                            .getPositions().add(pos);

                    // Update times and total count
                    wordMap.get(lemma).getAllocations().get(bookKey).setTimes(
                            wordMap.get(lemma).getAllocations().get(bookKey).getTimes() + 1);
                    wordMap.get(lemma).setTotal(wordMap.get(lemma).getTotal() + 1);
                }
            }
        }

        // Save each word in the appropriate JSON file
        for (String lemma : wordMap.keySet()) {
            saveOrUpdateWord(wordMap.get(lemma));
        }
    }

    private void saveOrUpdateWord(Word word) throws IOException {
        String firstLetter = word.getWord().substring(0, 1).toLowerCase();
        String directoryPath = "datamart/reverse_indexes/" + firstLetter;
        Files.createDirectories(Paths.get(directoryPath));

        String jsonFilePath = directoryPath + "/" + word.getWord() + ".json";
        Word existingWord = jsonFileManager.readJson(jsonFilePath);

        if (existingWord != null) {
            mergeWordData(existingWord, word);
            jsonFileManager.writeJson(jsonFilePath, existingWord);
        } else {
            jsonFileManager.writeJson(jsonFilePath, word);
        }
    }

    private void mergeWordData(Word existingWord, Word newWord) {
        for (Map.Entry<String, BookAllocation> entry : newWord.getAllocations().entrySet()) {
            String bookKey = entry.getKey();
            BookAllocation newBookAllocation = entry.getValue();

            existingWord.getAllocations().merge(bookKey, newBookAllocation, (existingAlloc, newAlloc) -> {
                Set<Position> positionSet = new HashSet<>(existingAlloc.getPositions());
                positionSet.addAll(newAlloc.getPositions());
                existingAlloc.setPositions(new ArrayList<>(positionSet));
                existingAlloc.setTimes(positionSet.size());
                return existingAlloc;
            });

            existingWord.setTotal(existingWord.getTotal() + newBookAllocation.getTimes());
        }
    }

    public void executeIndexing(String lastBookPath) throws IOException {
        int lastBookId = lastBookManager.readLastProcessedBookId(lastBookPath);
        for (int bookId = lastBookId + 1; ; bookId++) {
            indexBook(bookId);
            lastBookManager.updateLastProcessedBookId(lastBookPath, bookId);
        }
    }
}
