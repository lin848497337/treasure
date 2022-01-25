package sample.math;


import sample.math.cmd.CmdType;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;
/**
 * 定义
 * MA5(CD)
 * MA10(CD)
 * MA20(CD)
 * CLOSE(CD)
 * OPEN(CD)
 * HIGHT(CD)
 * LOW(CD)
 * VOL(CD)
 * CURRENT_DATE:d
 *
 * MAX(BEGIN, END, EXP)
 * MIN(BEGIN, END, EXP)
 */
public class CommandQueue implements Iterable<String> {
    private String orgRule;

    public CommandQueue(String orgRule) {
        this.orgRule = orgRule;
    }

    class Context{
        LinkedList<String> stack = new LinkedList<>();
        StringBuilder tmp = new StringBuilder();
        CmdType type;

        void append(char c){
            tmp.append(c);
        }

        void clear(){
            tmp.delete(0, tmp.length());
        }

        void checkCmd(){
            if (type != null){
                return;
            }
            if(stack.isEmpty()){
                return;
            }
           String cmd = stack.poll();
           type = CmdType.valueOf(cmd);
        }

        void hitWhitespace(){
            if (tmp.length() > 0){
                stack.add(tmp.toString());
                clear();
            }
            checkCmd();
        }

        void hitDot(){
            if (tmp.length() > 0){
                stack.add(tmp.toString());
                clear();
            }
        }

        void hitParamStart(){
            if (tmp.length() > 0){
                stack.add(tmp.toString());
                clear();
            }
        }

        void hitParamEnd(){
            if (tmp.length() > 0){
                stack.add(tmp.toString());
                clear();
            }
        }

        public CmdType getType() {
            return type;
        }

        public String[] getParams() {
            return stack.toArray(new String[stack.size()]);
        }

        boolean isEnd(){
            if (type == null){
                return false;
            }
            return stack.size() == type.getParamSize();
        }
    }

    public void parse(){
        Context context = new Context();
        for (int i=0 ; i<orgRule.length() ; i++){
            char c = orgRule.charAt(i);
            switch (c){
            case ' ':
                context.hitWhitespace();
                break;
            case '(':
                context.hitParamStart();
                break;
            case ')':
                context.hitParamEnd();
                break;
            case ',':
                context.hitDot();
                break;
            default:
                context.append(c);

            }
            context.isEnd();
        }
    }

    @Override
    public Iterator<String> iterator() {
        Scanner scanner = new Scanner(orgRule);

        return new Iterator<String>() {

            private LinkedList<String> cmdQueue = new LinkedList<>();

            @Override
            public boolean hasNext() {
                if (scanner.hasNext()){
                    String line = scanner.next();
                    String[] cmds = line.split(" ");
                    for(String cmd : cmds){
                        cmdQueue.add(cmd);
                    }
                }
                return !cmdQueue.isEmpty();
            }

            @Override
            public String next() {
                return cmdQueue.pollLast();
            }
        };
    }
}

