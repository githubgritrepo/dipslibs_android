package com.evo.mitzoom.cmd;

import com.evo.mitzoom.IListener;

public interface CmdHandler extends IListener {
    void onCmdReceived(CmdRequest request);
}
