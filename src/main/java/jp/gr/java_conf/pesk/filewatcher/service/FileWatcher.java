package jp.gr.java_conf.pesk.filewatcher.service;

import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import org.apache.commons.lang.StringUtils;

public class FileWatcher implements Runnable {

    private String watchDirectory;
    private String watchFileName;

    public FileWatcher(String dir, String fileName) {
        this.setWatchDirectory(dir);
        this.setWatchFileName(fileName);
    }

    /**
     * @return watchDirectory
     */
    public String getWatchDirectory() {
        return watchDirectory;
    }

    /**
     * @param watchDirectory セットする watchDirectory
     */
    public void setWatchDirectory(String watchDirectory) {
        this.watchDirectory = watchDirectory;
    }

    /**
     * @return watchFileName
     */
    public String getWatchFileName() {
        return watchFileName;
    }

    /**
     * @param watchFileName セットする watchFileName
     */
    public void setWatchFileName(String watchFileName) {
        this.watchFileName = watchFileName;
    }

    @Override
    public void run() {
        try {
            Path dir = Paths.get(getWatchDirectory());

            try (WatchService watcher = dir.getFileSystem().newWatchService()) {
                // ファイル作成のみ監視
                WatchKey watchKey = dir.register(watcher, new Kind[]{StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY});

                // 監視対象ファイルが作成されるまで待ち続ける
                while (true) {

                    if (Thread.currentThread().isInterrupted()) {
                        throw new InterruptedException();
                    }

                    Path name = null;

                    for (WatchEvent<?> event : watchKey.pollEvents()) {
                        Kind<?> kind = event.kind();

                        if (kind == StandardWatchEventKinds.ENTRY_CREATE || kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                            name = (Path)event.context();
                            if (StringUtils.equals(getWatchFileName(), name.toString())) {
                                // 監視ファイル名と同一の場合はbreak
                                break;
                            }

                        }

                    }

                    // TODO 冗長をなくす
                    if (name != null && StringUtils.equals(getWatchFileName(), name.toString())) {

                        System.out.println("Detect file create [" +getWatchDirectory() +"\\" +getWatchFileName()+ "]");

                        break;
                    }

                }

            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }

        } catch (RuntimeException | IOException e) {
            e.printStackTrace();
        }

    }

}
