
import java.io.*;
import java.util.ArrayList;
import java.util.List;

//import InputOutput;

/**
 * @author knowyourself1010
 *
 */
public class CopyUsefulClasses {
    private String source = null; // 类源目录
    private String dest = null; // 类拷贝目的目录
    String[] jarArr = new String[] {"deploy" };

    /***
     *
     * @param source
     *            类源目录
     * @param dest
     *            类拷贝目的目录
     * @param jarArr
     *            需要的提取的jar文件
     */
    public CopyUsefulClasses(String source, String dest, String[] jarArr) {
        this.source = source;
        this.dest = dest;
        this.jarArr = jarArr;
    }

    public static void main(String[] args) {
        String[] jarArr = new String[] {"rt"};
        CopyUsefulClasses obj = new CopyUsefulClasses(
                "C:\\Users\\Administrator\\Desktop\\seralPort\\jre",
                "C:\\Users\\Administrator\\Desktop\\seralPort\\jre", jarArr);
        obj.cutRT("C:\\Users\\Administrator\\Desktop\\seralPort\\jre\\classes.txt");
    }

    /***
     * 根据classes.txt文件精简rt目录
     * @param logName
     *            提取class明细
     */
    public void cutRT(String logName) {
        List<String> cList = getClassesListFromFile(logName);
        int count = 0; // 用于记录成功拷贝的类数
        try {
            for(String klass : cList) {
                if (copyClass(klass)) {
                    count++;
                } else {
                    System.out.println("ERROR " + count + ": " + klass);
                }
            }
        } catch (IOException e) {
            System.out.println("ERROR: " + e);
        }
        System.out.println("count: " + count);
    }

    /***
     * 从原jar路径提取相应的类到目标路径，如将java/lang/CharSequence类从rt目录提取到rt1目录
     *
     * @param string
     *            提取类的全路径
     * @return
     * @throws IOException
     */
    public boolean copyClass(String string) throws IOException {
        if (string.lastIndexOf("/") == -1) {
            return false;
        }
        String classDir = string.substring(0, string.lastIndexOf("/"));
        String className = string.substring(string.lastIndexOf("/") + 1,
                string.length())
                + ".class";

        boolean result = false;

        for (String jar : jarArr) {
            File srcFile = new File(source + "/" + jar + "/" + classDir + "/"
                    + className);
            if (!srcFile.exists()) {
                continue;
            }

            byte buf[] = new byte[256];
            FileInputStream fin = new FileInputStream(srcFile);

            /* 目标目录不存在,创建 */
            File destDir = new File(dest + "/" + jar + "1/" + classDir);
            if (!destDir.exists()) {
                destDir.mkdirs();
            }
            File destFile = new File(destDir + "/" + className);
            FileOutputStream fout = new FileOutputStream(destFile);
            int len = 0;
            while ((len = fin.read(buf)) != -1) {
                fout.write(buf, 0, len);
            }
            fout.flush();
            result = true;
            fin.close();
            fout.close();
            break;
        }
        return result;
    }

    /**
     * 将原始的classes.txt转换为类名列表
     * @param source 由-verbose:classes选项生成的classes.txt的路径
     */
    private List<String> getClassesListFromFile(String source) {
        BufferedReader reader = null;
        ArrayList<String> classList = new ArrayList<>();
        try {
            reader = new BufferedReader(new FileReader(source));
            String line = null;
            while((line = reader.readLine()) != null) {
                if (line.startsWith("[")) {
                    if (line.startsWith("[Opened")) {
                        continue;
                    }
                    line = line.replace("[Loaded ", "");
                    int index = line.indexOf("from");
                    if (index >= 0) {
                        line = line.substring(0, index);
                    }
                    line = line.trim().replace(".", "/");
                    classList.add(line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (Exception e) {

            }
        }
        return classList;
    }
}