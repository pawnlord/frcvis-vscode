package frcviseclipse.core;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.WorkingCopyOwner;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.jdt.core.dom.WildcardType;
import org.eclipse.osgi.internal.loader.EquinoxClassLoader;

import frcviseclipse.core.util.Logger;
import org.eclipse.jdt.ls.core.internal.JDTUtils;
import org.eclipse.jdt.ls.core.internal.handlers.CodeActionHandler;

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
        
        CompilationUnit root = CodeActionHandler.getASTRoot(compilationUnit, monitor);

        NodeFinder finder = new NodeFinder(root, offset, 1);
        ASTNode node = finder.getCoveringNode();
        ASTNode rootNode = finder.getCoveringNode().getRoot();
        while(node != rootNode){
            int nodeType = node.getNodeType();
            Logger.logOnVSCode(node.toString() + ": " + Integer.toString(nodeType));
            if(nodeType == 14){
                var type = ((ClassInstanceCreation)node).getType();
                Logger.logOnVSCode("Type of Class Creation: " + type.toString());
                Logger.logOnVSCode(" ---- Binding: " + type.resolveBinding().toString());
            }
            node = node.getParent();
        }

    }
}
