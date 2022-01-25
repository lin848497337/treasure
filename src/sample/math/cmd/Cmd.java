package sample.math.cmd;

import sample.math.ExecutorContext;

public abstract class Cmd {

    private Cmd nextCmd;

    public Cmd getNextCmd() {
        return nextCmd;
    }

    public void setNextCmd(Cmd nextCmd) {
        this.nextCmd = nextCmd;
    }

    public final void execute(ExecutorContext context){
        if(!doExecute(context)){
            return;
        }
        if (nextCmd != null){
            nextCmd.execute(context);
        }
    }

    protected abstract boolean doExecute(ExecutorContext context);
}
