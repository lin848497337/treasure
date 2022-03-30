package sample.util;

import java.io.OutputStream;
import java.io.PrintStream;

public class Console {
    private String line;
    private PrintStream os ;

    public Console(PrintStream os) {
        this.os = os;
    }

    private void clean(){
        if (line != null){
            for (int i=0 ; i<line.length() ; i++){
                os.print('\b');
            }
        }
    }

    public void write(String line){
        clean();
        this.line = line;
        os.print(line);
    }
}
