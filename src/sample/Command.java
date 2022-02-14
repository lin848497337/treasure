package sample;

import com.google.common.collect.Maps;
import sample.util.web.WgetCmd;

import java.io.IOException;
import java.util.Map;

public class Command {
    public static void main(String[] args) throws IOException, InterruptedException {
        Map<String, String> paramMap = Maps.newHashMap();
        for (int i=0 ; i<args.length ; i+=2){
            paramMap.put(args[i], args[i+1]);
        }
        WgetCmd wgetCmd = new WgetCmd(paramMap.get("link"), paramMap.get("file"));
        wgetCmd.addParam(paramMap.get("param"));
        wgetCmd.execute();
    }
}
