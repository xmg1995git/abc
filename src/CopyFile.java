
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * @author xiaoMaGe
 * @date 2020-05-24 - 18:46
 * <p>
 * CopyFile类用于多线程复制文件
 * 用到了RandomAccessFile实现对文件的随机访问
 */
public class CopyFile implements Runnable {
    /**
     * 用户输入参数
     */
    private File srcFile;                       // 源文件
    private File destFile;                      // 目标文件
    /**
     * 程序内部用到的参数
     */
    private long posStart;                      // 复制的开始位置
    private long posEnd;                        // 复制的结束位置
    private byte[] data = new byte[1024];       // 存储数据的字节数组
    private int threadNum;                      // 线程数
    /**
     * 不覆盖目标文件
     */
    private long readStart;                      // 读文件的开始
    private boolean cover;                       // 是否覆盖

    public CopyFile() {
    }
    public CopyFile(File srcFile, File destFile) throws FileNotFoundException {
        this.srcFile = srcFile;
        this.destFile = destFile;
        checkSrcFile(srcFile);
        checkDestFile(destFile);
    }
    public CopyFile(File srcFile, File destFile, byte[] data) throws FileNotFoundException {
        this.srcFile = srcFile;
        this.destFile = destFile;
        this.data = data;
        checkSrcFile(srcFile);
        checkDestFile(destFile);
    }

    private CopyFile(File srcFile, File destFile, long posStart, long posEnd, long readStart, boolean cover) {
        this.srcFile = srcFile;
        this.destFile = destFile;
        this.posStart = posStart;
        this.posEnd = posEnd;
        this.readStart = readStart;
        this.cover = cover;
    }
    /**
     * 开始执行复制工作
     */
    public void toCopyFile() throws FileNotFoundException {
        int num = this.threadNum;
        System.out.println(Thread.currentThread().getName() + "---------->" + "启动了" + num + "个线程");
        CopyFile[] copyFiles = new CopyFile[num];
        for (int i = 0; i < num; i++) {
            copyFiles[i] = new CopyFile(this.srcFile, this.destFile, i * data.length, srcFile.length(), i * data.length, true);
            Thread thread = new Thread(copyFiles[i]);
            thread.start();
        }
    }
    /**
     * 是否覆盖目标文件 true：覆盖 false:不覆盖
     * @param cover
     */
    public void toCopyFile(Boolean cover) throws FileNotFoundException {
        if (cover == true) {
            toCopyFile();
            return;
        }
        int num = ensureThreadNum(srcFile, this.data);
        System.out.println(Thread.currentThread().getName() + "---------->" + "启动了" + num + "个线程");
        ArrayList<CopyFile> list = new ArrayList<CopyFile>(num);
        // 先确定好参数再去start(),负责有可能出现乱码，防止destFile.length()改变
        for (int i = 0; i < num; i++) {
            list.add(new CopyFile(srcFile,
                                 destFile,
                                (destFile.length() + i * data.length),
                                 srcFile.length(),
                                (i * data.length),
                                false));
        }
        // 执行srart()
        for (CopyFile copyFile : list) {
            new Thread(copyFile).start();
        }
    }

    @Override
    public void run() {
        RandomAccessFile rafWrite = null;
        RandomAccessFile rafRead = null;
        try {
            rafWrite = new RandomAccessFile(destFile, "rw");
            rafRead = new RandomAccessFile(srcFile, "r");
            int length = 0;
            rafWrite.seek(this.posStart);
            rafRead.seek(this.readStart);
            while ((length = rafRead.read(data)) != -1) {
                rafWrite.write(data, 0, length);
                if (rafWrite.getFilePointer() >= posEnd) {
                    System.out.println(Thread.currentThread().getName() + "---------->" + "文件copy完毕");
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("---------->" + e.getMessage());
        }finally {
            if(rafRead != null){
                try {
                    rafRead.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(rafWrite != null){
                try {
                    rafWrite.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 计算所需要的线程数
     *
     * @param srcFile
     * @param data
     * @return
     */
    private int ensureThreadNum(File srcFile, byte[] data) throws FileNotFoundException {
        long length = srcFile.length();
        int datalen = data.length;
        this.threadNum = datalen;
        return numberFormat(length, datalen);
    }

    /**
     * 格式化小数
     *
     * @param a
     * @param b
     * @return
     */
    private int numberFormat(long a, int b) {
        DecimalFormat dF = new DecimalFormat("0.00");
        String format = dF.format((float) a / b);
        return (int) Math.ceil(Double.parseDouble(format));
    }

    /**
     * 检查目标文件是否存在，没有则进行创建
     *
     * @param file
     */
    private void checkDestFile(File file) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * 检查源文件是否存在
     * @param file
     * @throws FileNotFoundException
     */
    private void checkSrcFile(File file) throws FileNotFoundException {
        boolean exists = file.exists();
        if (!exists) {
            throw new FileNotFoundException("源文件不存在，无法复制！！！");
        }
    }


    public static void main(String[] args) throws FileNotFoundException {
        // 源文件
        File file = new File("C:\\Users\\Administrator\\Desktop\\test.txt");
        // 目标文件
        File toFile = new File("C:\\Users\\Administrator\\Desktop\\toTest1.txt");

        // 调用方法，内部已启动线程进行复制工作
        new CopyFile(file, toFile).toCopyFile(false);

       


    }
}
