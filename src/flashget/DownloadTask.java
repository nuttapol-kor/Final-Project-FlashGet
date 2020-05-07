package flashget;

import javafx.concurrent.Task;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Downloader class to access to URl and write file in start-end size position
 *
 * @author Nuttapol Korcharoenrat 6210546404.
 */
public class DownloadTask extends Task<Long> {
    private URL url;
    private File outfile;
    private long start;
    private long size;

    /**
     * DownloadTask Constructor
     *
     * @param url   url to download
     * @param file  file to write
     * @param start start position to access and write
     * @param size  end of position to access and write
     */
    public DownloadTask(URL url, File file, long start, long size) {
        this.url = url;
        this.outfile = file;
        this.start = start;
        this.size = size;
    }

    /**
     * Download and write to file
     * Override from Task class
     *
     * @return byteRead
     */
    @Override
    public Long call() {
        final int BUFFERSIZE = 16 * 1024;
        InputStream in = null;
        RandomAccessFile writer = null;
        long byteRead = 0;
        try {
            URLConnection connection = url.openConnection();

            // TODO try
            String range = null;
            if (size > 0) {
                range = String.format("bytes=%d-%d", start, start + size - 1);
            } else {
                range = String.format("bytes=%d-", start);
            }
            connection.setRequestProperty("Range", range);
            // get the input stream for reading the part of the URL content
            in = connection.getInputStream();
            // create a random access file for synchronous output
            writer = new RandomAccessFile(this.outfile, "rwd");
            // seek to location (in bytes) to start writing to
            writer.seek(start);
            byte[] buffer = new byte[BUFFERSIZE];
            while (byteRead < size) {
                int n = in.read(buffer);
                if (n < 0) break;
                writer.write(buffer, 0, n);
                byteRead += n;
                // update progress
                updateProgress(byteRead, size);
                // update value
                updateValue(byteRead);
                // if task cancel stop the loop
                if (isCancelled()) {
                    break;
                }
            }
        } catch (MalformedURLException ex) {
            System.err.println(ex.getMessage());
        } catch (IOException ioe) {
            System.out.println("Have some error in getContentLengthLong");
        } finally {
            // close InputStream and RandomAccessFile
            try {
                assert in != null;
                in.close();
                assert writer != null;
                writer.close();
            } catch (IOException ex) {
                System.out.println("error");
            }
        }
        return byteRead;
    }
}
