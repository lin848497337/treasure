package sample.math.cmd;

import sample.math.ExecutorContext;

import java.util.List;

public class OrCmd extends Cmd{

    private List<Cmd> cmdList;

    public OrCmd(List<Cmd> cmdList) {
        this.cmdList = cmdList;
    }

    @Override
    protected boolean doExecute(ExecutorContext context) {
        for (Cmd cmd : cmdList){
            if (cmd.doExecute(context)){
                return true;
            }
        }
        return false;
    }
}
