package frcviseclipse.core;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.WorkingCopyOwner;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.NodeFinder;

import frcviseclipse.core.util.Logger;
import org.eclipse.jdt.ls.core.internal.JDTUtils;

public class PackageCommands {
    public static String CMD_LOG = "frcvis.log";
    public static String CMD_ERROR = "frcvis.error";
    @SuppressWarnings("restriction")
    public static void displayTree(List<Object> arguments, IProgressMonitor monitor){
        if(arguments.size() < 2){
            Logger.errorOnVSCode("Not enough arguments for displayTreeJava");
            return;
        }
        String uri = (String)(arguments.get(0));
        Double offsetReceived = (Double)(arguments.get(1));
        int offset = offsetReceived.intValue();
        Logger.logOnVSCode("Received: " + uri + ":" + offset);
        ICompilationUnit compilationUnit = JDTUtils.resolveCompilationUnit(uri);
        WorkingCopyOwner workingCopy = compilationUnit.getOwner();
        CompilationUnit root;
        try{
            root = compilationUnit.reconcile(AST.JLS_Latest, true, false,  workingCopy, monitor);
        } catch(JavaModelException e){
            Logger.errorOnVSCode("displayTree: JavaModelException: " + e.getMessage() + "\n Trace: " + e.getStackTrace());
            return;
        }

        NodeFinder finder = new NodeFinder(root, offset, 1);
        Logger.logOnVSCode(Integer.toString(finder.getCoveringNode().getNodeType()));


    }
}
