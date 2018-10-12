package main.com.Chatbot.fletcher.lucene.file;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class Indexer
{
    public static void main(String[] args)
    {
        //Input folder
        String docsPath = "inputFiles";

        //Output folder
        String indexPath = "indexedFiles";

        //Input Path
        final Path docDir = Paths.get(docsPath);

        try
        {
            //org.apache.lucene.store.Directory instance
            Directory dir = FSDirectory.open( Paths.get(indexPath) );

            //Lucenes Standard analyzer
            Analyzer analyzer = new StandardAnalyzer();

            //IndexWriter Configuration
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);

            //IndexWriter writes new index files to the out put directory
            IndexWriter writer = new IndexWriter(dir, iwc);

            //Runs as a recursive function to write all files to the index
            indexDocs(writer, docDir);

            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    static void indexDocs(final IndexWriter writer, Path path) throws IOException
    {
        //Directory
        if (Files.isDirectory(path))
        {
            //Iterate directory
            Files.walkFileTree(path, new SimpleFileVisitor<Path>()
            {

                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
                {
                    try
                    {
                        //Index this file
                        indexDoc(writer, file, attrs.lastModifiedTime().toMillis());
                    }
                    catch (IOException ioe)
                    {
                        ioe.printStackTrace();
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        else
        {
            //Index this file
            indexDoc(writer, path, Files.getLastModifiedTime(path).toMillis());
        }
    }

    static void indexDoc(IndexWriter writer, Path file, long lastModified) throws IOException
    {
        try (InputStream stream = Files.newInputStream(file))
        {
            //Creates and adds info fields
            Document doc = new Document();

            doc.add(new StringField("path", file.toString(), Field.Store.YES));
            doc.add(new LongPoint("modified", lastModified));
            doc.add(new TextField("contents", new String(Files.readAllBytes(file)), Store.YES));
            writer.updateDocument(new Term("path", file.toString()), doc);
        }
        System.out.println(java.time.LocalTime.now());//speed testing
    }



}