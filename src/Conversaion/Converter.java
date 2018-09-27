package com.gtedx.file_converter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class Converter {
    private final String LOADING_FORMAT = "\nLoading stream\n\n";
    private final String PROCESSING_FORMAT = "Load completed in %1$dms, now converting...\n\n";
    private final String SAVING_FORMAT = "Conversion took %1$dms.\n\nTotal: %2$dms\n";

    private long startTime;
    private long startOfProcessTime;

    protected InputStream inStream;
    protected OutputStream outStream;
    protected String inPath;
    protected String outPath;

    protected boolean showOutputMessages = true;
    protected boolean closeStreamsWhenComplete = true;

    public Converter(String inPath, String outPath, InputStream inStream, OutputStream outStream, boolean showMessages, boolean closeStreamsWhenComplete){
        this.inStream = inStream;
        this.outStream = outStream;
        this.showOutputMessages = showMessages;
        this.closeStreamsWhenComplete = closeStreamsWhenComplete;
        this.inPath = inPath;
        this.outPath = outPath;
    }

    public abstract void convert() throws Exception;

    private void startTime(){
        startTime = System.currentTimeMillis();
        startOfProcessTime = startTime;
    }

    protected void loading(){
        sendToOutputOrNot(String.format(LOADING_FORMAT));
        startTime();
    }

    protected void processing(){
        long currentTime = System.currentTimeMillis();
        long prevProcessTook = currentTime - startOfProcessTime;

        sendToOutputOrNot(String.format(PROCESSING_FORMAT, prevProcessTook));

        startOfProcessTime = System.currentTimeMillis();

    }

    protected void finished(){
        long currentTime = System.currentTimeMillis();
        long timeTaken = currentTime - startTime;
        long prevProcessTook = currentTime - startOfProcessTime;

        startOfProcessTime = System.currentTimeMillis();

        if(closeStreamsWhenComplete){
            try {
                inStream.close();
                outStream.close();
            } catch (IOException e) {
                //Nothing done
            }
        }

        sendToOutputOrNot(String.format(SAVING_FORMAT, prevProcessTook, timeTaken));
    }


    private void sendToOutputOrNot(String toBePrinted){
        if(showOutputMessages){
            actuallySendToOutput(toBePrinted);
        }
    }


    protected void actuallySendToOutput(String toBePrinted){
        System.out.println(toBePrinted);
    }

}
