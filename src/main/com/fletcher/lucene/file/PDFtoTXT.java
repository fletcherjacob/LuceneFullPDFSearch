package main.com.fletcher.lucene.file;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class PDFtoTXT {

    public static void main(String[] args) {

        //Create The DirectoryPath Way
        File aDirectory = new File("inputFilesPDF");

        // List the files within the array
        String[] filesInDir = aDirectory.list();



        System.out.println(java.time.LocalTime.now());


        // Iterate
        for (int i = 0; i < filesInDir.length; i++) {



            // extract text using PDFBox library
            try {

                // use text Extraction
                PDDocument doc = PDDocument.load(new File("inputFilesPDF\\" + filesInDir[i]));
                String text = new PDFTextStripper().getText(doc);
                doc.close();

                // Remove .PDF Extension
                String[] array = filesInDir[i].split("[.]");

                // Pull extracted text as a variable
                String filename = array[0];

                // write the content to the new text file
                PrintWriter pw = new PrintWriter(new FileWriter("inputFiles\\" + filename + ".txt"));


                pw.write(text);


                pw.close();
               // System.out.println("file: " + filename );


            } catch (IOException e) {
                e.printStackTrace();
            }



                }
        System.out.println("Conversion Completed:    " + java.time.LocalTime.now());
            }

        }











