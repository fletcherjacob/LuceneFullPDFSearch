package main.com.fletcher.lucene.file;


import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

public class Searcher {
    //directory contains the lucene indexes
    private static final String INDEX_DIR = "indexedFiles";

    public static void main(String[] args) throws Exception {
        //Create lucene searcher. It search over a single IndexReader.
        IndexSearcher searcher = createSearcher();

        //Search indexed contents using search term
        System.out.println("SOFWERX CHATBOT SEARCH ENGINE WIN DEPLOYMENT");
        System.out.println("Enter Search Phrase:");
        Scanner in = new Scanner(System.in);

        String searchWord = in.nextLine();



        //String searchWord = "Jungle Aid" ;

        TopDocs foundDocs = searchInContent(searchWord, searcher);

        //Total found documents
        System.out.println("Total Results :: " + foundDocs.totalHits);

        //Let's print out the path of files which have searched term
        for (ScoreDoc sd : foundDocs.scoreDocs) {
            Document d = searcher.doc(sd.doc);
            System.out.println("Path : " + d.get("path") + "\\n" + ", Score : " + sd.score);
            // Loading an existing document


            //File pathName= new File(d.get("path"));
            // Remove .PDF Extension
            //string preName = d.get("path");

            String preName = d.get("path");
            String[] nameArray1 = preName.split("[.]");
            String preName1 = nameArray1[0];
            System.out.println("0 " + preName1);
            String[] nameArray2 = preName1.split("[\\\\]");

            // Use for future faster pagination solution
            String folderName = nameArray2[0];
            String root= nameArray2[0];
            String filename = nameArray2[1];


          // System.out.println("1 " + root);
            //System.out.println("2 " + folderName);
              // System.out.println("3 " + filename);
            //Creates path for file to be used by daniels code
            System.out.println(root +"\\\\" + folderName + "\\\\" + filename + ".pdf");
              File pdfFile = new File(/*root +"\\\\" + folderName*/ "inputFilesPDF" + "\\\\" + filename + ".pdf");

                PDDocument document = PDDocument.load(pdfFile);

                int foundPageNumberArray[] = searchPDFReturnPages(document, searchWord);
                //  printIntArray(foundPageNumberArray);


                int mostLikelyPagesArray[] = mostLikelyPages(foundPageNumberArray);
                //Assigns Highest page hit for future use
                int MPH = mostLikelyPagesArray[0];
                // printIntArray(mostLikelyPagesArray);
                System.out.println("Most Likely on page " + MPH);
                PDFopen();


                //Closing the document
                document.close();
                //System.out.println(filesInDir[i]+":  " + java.time.LocalTime.now());*/

        }


    }


    private static TopDocs searchInContent(String textToFind, IndexSearcher searcher) throws Exception {
        //Create search query
        QueryParser qp = new QueryParser("contents", new StandardAnalyzer());
        Query query = qp.parse(textToFind);

        //search the index
        TopDocs hits = searcher.search(query, 500);
        return hits;
    }

    private static IndexSearcher createSearcher() throws IOException {
        Directory dir = FSDirectory.open(Paths.get(INDEX_DIR));

        //It is an interface for accessing a point-in-time view of a lucene index
        IndexReader reader = DirectoryReader.open(dir);

        //Index searcher
        IndexSearcher searcher = new IndexSearcher(reader);
        return searcher;
    }


    public static String redundantRemoverArray(String searchString) {
        //TODO


        return "test for now lol";
    }

    private static String[] wordsSeparatedArray(String searchString) {//add other permutations of original string here for added bonus
        String[] wordArray = searchString.split("\\s+");

        for (int i = 0; i < wordArray.length; i++) {//cleanup any non characters
            wordArray[i] = wordArray[i].replaceAll("[^\\w]", "");
        }

        return wordArray;
    }


    private static int[] searchPDFReturnPages(PDDocument document, String testString) throws IOException {
        //Instantiate PDFTextStripper class
        PDFTextStripper pdfStripper = new PDFTextStripper();
        int hitCount = 0;
        int hitPagesArray[] = new int[10000];//lol java garbage collector
        String searchStringInWordsStringArray[];//fix later
        searchStringInWordsStringArray = wordsSeparatedArray(testString);

        for (int i = 1; i < document.getNumberOfPages(); i++) {//page itteration
            pdfStripper.setStartPage(i);
            pdfStripper.setEndPage(i);
            String textFromPDFPage = pdfStripper.getText(document);

            for (int j = -1; j < searchStringInWordsStringArray.length; j++) {//note this prob wont itterate more then 8 times max
                String stringOrWordToBeSearched = testString;

                if (j > -1) {
                    stringOrWordToBeSearched = searchStringInWordsStringArray[j];
                }

                boolean isThere = textFromPDFPage.toLowerCase().contains(stringOrWordToBeSearched.toLowerCase());//lower case and everything is same standard
                if (isThere) {
                    //System.out.println("=== " + stringOrWordToBeSearched + " found on page number " + i + " ===");
                    //System.out.println(textFromPDFPage);
                    //System.out.println("\n\n");
                    hitCount++;
                    hitPagesArray[hitCount] = i;
                }
                if (isThere && j == -1) {//whole string found on page
                    //break;
                }
            }
        }

        System.out.println("[" + testString + "]" + " content could be on " + hitCount + " pages:");


        return hitPagesArray;
    }

    private static int[] mostLikelyPages(int hitPages[]) {
        int likelyPages[] = new int[5];
        int max_count = 1, res = hitPages[0];
        int curr_count = 1;
        int n = hitPages.length;

        for (int k = 0; k < 5; k++) {//Max 5 itterations dw about runtime complexity here
            for (int i = 1; i < n; i++) {//worry about runtime complexity with this loop and all the 0s
                if (hitPages[i] == 0) {
                    break;
                }


                if (hitPages[i] == hitPages[i - 1])
                    curr_count++;
                else {
                    if (curr_count > max_count) {
                        max_count = curr_count;

                        res = hitPages[i - 1];
                    }
                    curr_count = 1;
                }
            }

            // If last element is most frequent
            if (curr_count > max_count) {
                max_count = curr_count;
                res = hitPages[n - 1];
            }


            likelyPages[k] = res;


        }
        return likelyPages;
    }

    private static void printIntArray(int array[]) {
        for (int i = 0; i < array.length; i++) {//yeah there's a better way to do this lol
            if (array[i] != 0) {
                System.out.print(array[i] + " ");//I can return these as an array, string, or whatever needed
            }
            //could break after printing zeros but its nbd atm
        }
        System.out.println("");
    }


    public static void PDFopen() throws IOException {
        try {

            if ((new File("inputFilesPDF\\Correct.pdf")).exists()) {

                Process p = Runtime
                        .getRuntime()
                        .exec("rundll32 url.dll,FileProtocolHandler inputFilesPDF\\Correct.pdf#page=77");
                System.out.println("Working");
                p.waitFor();

            } else {

                System.out.println("File is not exists");

            }

            System.out.println("Done");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

} 