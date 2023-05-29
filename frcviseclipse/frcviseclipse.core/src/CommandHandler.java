package frcviseclipse.core;

import java.util.List;

import org.eclipse.jdt.ls.core.internal.IDelegateCommandHandler;
import org.eclipse.core.runtime.IProgressMonitor;
import java.io.*;

public class CommandHandler implements IDelegateCommandHandler {
    @Override
    public Object executeCommand(String commandId, List<Object> arguments, IProgressMonitor monitor) throws Exception {
        if (commandId.equals("")) {
            switch (commandId) {
                case "frcvis.helloJava":
                    FileWriter out = null;
            
                    try {
                        out = new FileWriter("output.txt");
                        
                        out.append("Hello Java");
                    } finally {
                        if (out != null) {
                            out.close();
                        }
                    }
                    break;
                default:
                    break;
            }
        }
        throw new UnsupportedOperationException(String.format("Not supported commandId: '%s'.", commandId));
    }    
}
