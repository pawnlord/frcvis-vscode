package frcviseclipse.core;

import java.util.List;

import org.eclipse.jdt.ls.core.internal.IDelegateCommandHandler;
import org.eclipse.core.runtime.IProgressMonitor;
import java.io.*;
import org.eclipse.jdt.ls.core.internal.JavaLanguageServerPlugin;
import frcviseclipse.core.util.Logger;

public class CommandHandler implements IDelegateCommandHandler {
    @Override
    public Object executeCommand(String commandId, List<Object> arguments, IProgressMonitor monitor) throws Exception {
        switch (commandId) {
            case "frcvis.helloJava":
                Logger.logOnVSCode("This is a message on VSCode");
                Logger.errorOnVSCode("This is an error on VSCode");
            return null;
            case "frcvis.displayTreeJava":
                PackageCommands.displayTree(arguments);
            return null;
            default:
                break;
        }
        throw new UnsupportedOperationException(String.format("Not supported commandId: '%s'.", commandId));
    }    
}
