package jp.gr.java_conf.pesk.filewatcher;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jp.gr.java_conf.pesk.filewatcher.service.FileWatcher;

/**
 * システムプロパティ(watch.directory)で指定したディレクトリに対して、
 * 引数で与えたファイルが作成/変更されるまで監視を続けます。
 * 監視対象のファイルが全て作成/変更された場合は、システムプロパティ(control.file)で指定したファイルを
 * watch.directory配下に生成して処理を終了します。
 *
 * @param 監視ファイル(複数指定可)
 */
public class App {
    public static void main(String[] args) {

        // 監視するディレクトリ名(単一)
        String watchDirecotry = System.getProperty("watch.directory");

        // 制御ファイル(ディレクトリ込で指定)
        String controlFile = System.getProperty("control.file");

        ExecutorService executorService = Executors.newCachedThreadPool();

        for (String arg : args) {

            System.out.println("Now watcherService is waiting for creating files... [" + watchDirecotry + "\\" + arg
                    + "]");

            // 監視するファイル名(複数)
            executorService.execute(new FileWatcher(watchDirecotry, arg));

        }

        executorService.shutdown();

        try {
            // スレッドが全て戻ってきたら、制御ファイルを出力する。
            File file = new File(controlFile);
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
