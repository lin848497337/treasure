package sample.util.web;

import com.google.common.collect.Lists;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WgetCmd {
    private String link;
    private String fileName;

    private List<String> paramList = Lists.newArrayList();


    public WgetCmd(String link, String fileName) {
        this.link = link;
        this.fileName = fileName;
        this.init();
    }

    private void init(){
        this.paramList.add("--no-check-certificate");
    }

    public void addParam(String param){
        this.paramList.add(param);
    }

    public File execute() throws IOException, InterruptedException {
        List<String> cmdList = new ArrayList<>();
        cmdList.add("wget");
        cmdList.addAll(this.paramList);
        cmdList.add(link);
        cmdList.add("-O");
        cmdList.add(fileName+"__tmp_wget");
        ProcessBuilder pb = new ProcessBuilder(cmdList);
        pb.redirectErrorStream(true);
        Process process = pb.start();
        final InputStream inputStream = process.getInputStream();
        Thread t = new Thread() {
            @Override
            public void run() {

                byte[] cache = new byte[512];
                int idx = 0;
                int data = 0;
                long st = 0;
                try {
                    while ((data = inputStream.read()) != -1) {
                        long now = System.currentTimeMillis();
                        if (idx >= cache.length) {
                            System.out.write(cache, 0, idx);
                            idx = 0;
                            continue;
                        }
                        cache[idx++] = (byte) data;
                        if (data == '\n') {
                            if (now - st > 30000) {
                                st = now;
                                System.out.write(cache, 0, idx);
                            }
                            idx = 0;
                            continue;
                        }
                    }
                } catch (Exception e) {

                }
            }
        };
        t.setDaemon(true);
        t.start();

        process.waitFor();

        if (process.exitValue() != 0) {
            throw new RuntimeException("exec wget error : link " + link);
        }

        File file = new File(fileName + "_tmp_wget");
        File finalFile = new File(fileName);
        file.renameTo(finalFile);
        return finalFile;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Map<String, String> paramMap = new HashMap<>();
        for (int i=0 ; i<args.length ; i+=2){
            paramMap.put(args[i], args[i+1]);
        }
        WgetCmd wgetCmd = new WgetCmd(paramMap.get("link"), paramMap.get("file"));
        wgetCmd.addParam(paramMap.get("param"));
        wgetCmd.execute();
    }
}
