package frcviseclipse;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.ls.core.internal.IDelegateCommandHandler;
import java.io.*;

public class CommandHandler implements IDelegateCommandHandler {

    @Override
    public Object executeCommand(String commandId, List<Object> arguments, IProgressMonitor monitor) throws Exception {
        if (!commandId.equals("")) {
            switch (commandId) {
            case "frcvis.hello":
            	FileWriter out = null;
            	try {
            		out = new FileWriter("output.txt");
            		out.append("Hello, World!\n");
            	} finally {
            		if(out != null) {
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
