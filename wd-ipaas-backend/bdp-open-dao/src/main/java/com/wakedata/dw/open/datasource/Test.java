package com.wakedata.dw.open.datasource;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.MasterNotRunningException;

import java.io.*;

/**
 * @author tanzhi
 * @title Test
 * @date 2019/11/20 16:04
 * @projectName dw-open
 * @descriptor
 */
public class Test {


    private static String directory = "C:\\Users\\admin\\Downloads\\dss-open";
    private static String dist = "C:\\Users\\admin\\workplace\\js.txt";

    public static void main(String[] args) throws MasterNotRunningException, IOException, NoSuchFieldException, IllegalAccessException {

        File file = new File(directory);
        processFile(file);


    }

    private static void processFile(File file) throws IOException {
        System.out.println(file.getAbsoluteFile());
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File file1 : files) {
                processFile(file1);
            }
        } else {
            String name = file.getName();
            if (name.endsWith(".vue")||name.endsWith(".js")||name.endsWith(".less")) {
                String s = readFile(file);
                writeFile(s, dist);
            }
        }


    }

    public static String readFile(File file) {
        StringBuilder result = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            String s = null;
            while ((s = br.readLine()) != null) {
                s = s.trim();
                if (StringUtils.isBlank(s)) {
                    continue;
                }
                if (s.startsWith("import") || s.startsWith("/*") || s.startsWith("*") || s.startsWith("*/") || s.startsWith("//")) {
                    continue;
                }
                result.append(s).append("\n");
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    public static void writeFile(String text, String filename) throws IOException {
        FileWriter fileWriter = new FileWriter(filename, true);
        fileWriter.write(text);
        fileWriter.flush();
        fileWriter.close();
    }


}
