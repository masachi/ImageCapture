package Conversaion;


import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.*;

public class MainClass {
    public enum DOC_TYPE {
        DOC,
        DOCX,
        PPT,
        PPTX,
        TXT,
        XLS,
        XLSX
    }

    public static void main(String[] args){
        Converter converter;

        try{
            converter = processArguments(args);
        } catch (Exception e){
            System.out.println("\n\nInput\\Output file not specified properly.");
            return;
        }

        try {
            com.aspose.words.License wordLicense = new com.aspose.words.License();
            wordLicense.setLicense(ClassLoader.getSystemResourceAsStream("license.xml"));
            com.aspose.cells.License excelLicense = new com.aspose.cells.License();
            excelLicense.setLicense(ClassLoader.getSystemResourceAsStream("license.xml"));
            com.aspose.slides.License pptLicense = new com.aspose.slides.License();
            pptLicense.setLicense(ClassLoader.getSystemResourceAsStream("license.xml"));
        } catch (Exception e) {
            converter = null;
            e.printStackTrace();
        }


        if(converter == null){
            System.out.println("Unable to determine type of input file.");
        } else {
            try {
                converter.convert();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static Converter processArguments(String[] args) throws Exception{
        CommandLineValues values = new CommandLineValues();
        CmdLineParser parser = new CmdLineParser(values);

        Converter converter = null;
        try {
            parser.parseArgument(args);


            String inPath = values.inFilePath;
            String outPath = values.outFilePath;
            boolean shouldShowMessages = values.verbose;


            if(inPath == null){
                parser.printUsage(System.err);
                throw new IllegalArgumentException();
            }

            if(outPath == null){
                outPath = changeExtensionToPDF(inPath);
            }


            String lowerCaseInPath = inPath.toLowerCase();

            InputStream inStream = getInFileStream(inPath);
            OutputStream outStream = getOutFileStream(outPath);

            if(values.type == null){
                if(lowerCaseInPath.endsWith("doc")){
                    converter = new DocToPDFConverter(inPath, outPath, inStream, outStream, shouldShowMessages, true);
                } else if (lowerCaseInPath.endsWith("docx")){
                    converter = new DocxToPDFConverter(inPath, outPath, inStream, outStream, shouldShowMessages, true);
                } else if(lowerCaseInPath.endsWith("ppt")){
                    converter = new PptToPDFConverter(inPath, outPath, inStream, outStream, shouldShowMessages, true);
                } else if(lowerCaseInPath.endsWith("pptx")){
                    converter = new PptxToPDFConverter(inPath, outPath, inStream, outStream, shouldShowMessages, true);
                } else if(lowerCaseInPath.endsWith("txt")){
                    converter = new TextToPDFConverter(inPath, outPath, inStream, outStream, shouldShowMessages, true);
                } else if(lowerCaseInPath.endsWith("xls")){
                    converter = new XlsToPDFConverter(inPath, outPath, inStream, outStream, shouldShowMessages, true);
                } else if(lowerCaseInPath.endsWith("xlsx")){
                    converter = new XlsxToPDFConverter(inPath, outPath, inStream, outStream, shouldShowMessages, true);
                } else {
                    converter = null;
                }


            } else {

                switch(values.type){
                    case DOC: converter = new DocToPDFConverter(inPath, outPath, inStream, outStream, shouldShowMessages, true);
                        break;
                    case DOCX: converter = new DocxToPDFConverter(inPath, outPath, inStream, outStream, shouldShowMessages, true);
                        break;
                    case PPT:  converter = new PptToPDFConverter(inPath, outPath, inStream, outStream, shouldShowMessages, true);
                        break;
                    case PPTX: converter = new PptxToPDFConverter(inPath, outPath, inStream, outStream, shouldShowMessages, true);
                        break;
                    case TXT: converter = new TextToPDFConverter(inPath, outPath, inStream, outStream, shouldShowMessages, true);
                        break;
                    case XLS: converter = new XlsToPDFConverter(inPath, outPath, inStream, outStream, shouldShowMessages, true);
                        break;
                    case XLSX: converter = new XlsxToPDFConverter(inPath, outPath, inStream, outStream, shouldShowMessages, true);
                        break;
                    default: converter = null;
                        break;

                }


            }

        } catch (CmdLineException e) {
            // handling of wrong arguments
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
        }

        return converter;

    }


    public static class CommandLineValues {

        @Option(name = "-type", aliases = "-t", required = false, usage = "Specifies doc converter. Leave blank to let program decide by input extension.")
        public DOC_TYPE type = null;


        @Option(name = "-inputPath", aliases = {"-i", "-in", "-input"}, required = false,  metaVar = "<path>",
                usage = "Specifies a path for the input file.")
        public String inFilePath = null;


        @Option(name = "-outputPath", aliases = {"-o", "-out", "-output"}, required = false, metaVar = "<path>",
                usage = "Specifies a path for the output PDF.")
        public String outFilePath = null;

        @Option(name = "-verbose", aliases = {"-v"}, required = false, usage = "To see intermediate processing messages.")
        public boolean verbose = true;

    }

    //From http://stackoverflow.com/questions/941272/how-do-i-trim-a-file-extension-from-a-string-in-java
    public static String changeExtensionToPDF(String originalPath) {

//		String separator = System.getProperty("file.separator");
        String filename = originalPath;

//		// Remove the path upto the filename.
//		int lastSeparatorIndex = originalPath.lastIndexOf(separator);
//		if (lastSeparatorIndex == -1) {
//			filename = originalPath;
//		} else {
//			filename = originalPath.substring(lastSeparatorIndex + 1);
//		}

        // Remove the extension.
        int extensionIndex = filename.lastIndexOf(".");

        String removedExtension;
        if (extensionIndex == -1){
            removedExtension =  filename;
        } else {
            removedExtension =  filename.substring(0, extensionIndex);
        }
        String addPDFExtension = removedExtension + ".pdf";

        return addPDFExtension;
    }


    protected static InputStream getInFileStream(String inputFilePath) throws FileNotFoundException {
        File inFile = new File(inputFilePath);
        FileInputStream iStream = new FileInputStream(inFile);
        return iStream;
    }

    protected static OutputStream getOutFileStream(String outputFilePath) throws IOException {
        File outFile = new File(outputFilePath);

        try{
            //Make all directories up to specified
            outFile.getParentFile().mkdirs();
        } catch (NullPointerException e){
            //Ignore error since it means not parent directories
        }

        outFile.createNewFile();
        FileOutputStream oStream = new FileOutputStream(outFile);
        return oStream;
    }
}
