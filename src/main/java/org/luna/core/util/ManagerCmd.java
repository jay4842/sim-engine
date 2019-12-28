package org.luna.core.util;

public class ManagerCmd {

    private String cmd;
    private Object obj;

    public ManagerCmd(String cmd, Object obj){
        this.cmd = cmd;
        this.obj = obj;
    }

    public String getCmd() {
        return cmd;
    }

    public Object getObj() {
        return obj;
    }
}
