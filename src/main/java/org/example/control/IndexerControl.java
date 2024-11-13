package org.example.control;


import org.example.interfaces.WordIndexer;

public class IndexerControl implements WordIndexer {

    public IndexerControl() {
        FileBookManagerControl fileManager = new FileBookManagerControl();
        JsonFileManagerControl jsonFileManager = new JsonFileManagerControl();
        FileBookManagerControl fileBookManager = new FileBookManagerControl();
    }

    @Override
    public void indexWord(String word){

    }

}
