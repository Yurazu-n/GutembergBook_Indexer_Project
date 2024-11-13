package org.example.control;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Please provide the path to the lastBookId file as an argument.");
            System.exit(1);
        }

        String lastBookPath = args[0];

        IndexerControl indexer = new IndexerControl();

        try {
            indexer.executeIndexing(lastBookPath);
            System.out.println("Indexing completed successfully.");
        } catch (IOException e) {
            System.err.println("An error occurred during the indexing process:");
            e.printStackTrace();
        }
    }
}
