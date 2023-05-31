package frcviseclipse.core;

import java.util.List;

import org.eclipse.jdt.ls.core.internal.IDelegateCommandHandler;
import org.eclipse.core.runtime.IProgressMonitor;
import java.io.*;
import org.eclipse.jdt.ls.core.internal.JavaLanguageServerPlugin;

public class CommandHandler implements IDelegateCommandHandler {
    @Override
    public Object executeCommand(String commandId, List<Object> arguments, IProgressMonitor monitor) throws Exception {
        switch (commandId) {
            case "frcvis.helloJava":
                JavaLanguageServerPlugin.getInstance().getClientConnection().executeClientCommand("frcvis.log", "Hello Java");
                return null;
            default:
                break;
        }
        throw new UnsupportedOperationException(String.format("Not supported commandId: '%s'.", commandId));
    }    
}
