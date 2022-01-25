package sample.math.cmd;

import sample.math.ExecutorContext;

public class CLOSECmd extends Cmd{
    @Override
    protected boolean doExecute(ExecutorContext context) {
        return false;
    }
}
