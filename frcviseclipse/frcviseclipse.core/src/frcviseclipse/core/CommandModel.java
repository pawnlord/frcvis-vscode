package frcviseclipse.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;

import com.google.gson.Gson;
import com.google.gson.JsonSerializer;

import frcviseclipse.core.util.ASTUtil;
import frcviseclipse.core.util.Logger;

public class CommandModel {
    // IN CASE I'M TESTING WITH OTHER SUPERCLASSES
    // public static final String COMMAND_SUPERCLASS = "edu.wpi.first.wpilibj2.command.CommandBase";
    public static final String COMMAND_SUPERCLASS = "edu.wpi.first.wpilibj2.command.CommandBase";
    public static final String COMMAND_INTERFACE = "edu.wpi.first.wpilibj2.command.Command";

    public static class CommandNode{
        public List<String> argumentCommands;
        public List<String> requiredSubsystems;
        public ASTNode node;
        public String parentCommand = null; // Assume root
        public CommandNode(ASTNode node){
            argumentCommands = new ArrayList<>();
            requiredSubsystems = new ArrayList<>();
            this.node = node;
        }
        public String toString(){
            return node.toString();
        }
    }

    Map<String, CommandNode> nodes;

    public static String getID(ASTNode node){
        return node.toString() + ":" + node.getStartPosition();
    }

    // Take in unknown node and see if it should be added to the model
    public CommandNode tryAddCommand(ASTNode node){
        // All AST nodes should be uniquely identified by there tokens and starting position
        // TODO: Figure out if above statement is true
        String id = getID(node);
        Logger.logOnVSCode("~~~~~~~~~~~~~~~~" + id);

        int nodeType = node.getNodeType();
        if(nodeType == ASTNode.CLASS_INSTANCE_CREATION){
            var instanceCreation = (ClassInstanceCreation)node;
            var type = instanceCreation.getType();
            ITypeBinding binding = type.resolveBinding();


            Logger.logOnVSCode("[" + id + "] Subclass of " + COMMAND_SUPERCLASS + ": " + ASTUtil.isSubclass(binding, COMMAND_SUPERCLASS));
            Logger.logOnVSCode("[" + id + "] Implements " + COMMAND_INTERFACE + ": " + ASTUtil.isSubclass(binding, COMMAND_INTERFACE));
            
            if(ASTUtil.implementsInterface(binding, COMMAND_INTERFACE)){
                CommandNode commandNode = new CommandNode(node);
                
                List<ASTNode> arguments = (List<ASTNode>)instanceCreation.arguments();
                for(ASTNode subnode : arguments){
                    Logger.logOnVSCode(subnode.toString());
                    CommandNode child = tryAddCommand(subnode);
                    if(child != null) {
                        commandNode.argumentCommands.add(getID(child.node));
                        child.parentCommand = id;                
                    }
                }                
                return commandNode;
            }

        } else if(nodeType == ASTNode.METHOD_INVOCATION){
            var method = ((MethodInvocation)node);

            var returnType = method.resolveTypeBinding();
            if(returnType == null){
                return null;
            }
            Logger.logOnVSCode("Return type: " + returnType.getQualifiedName());

            Logger.logOnVSCode("[" + id + "] Subclass of " + COMMAND_SUPERCLASS + ": " + ASTUtil.isSubclass(returnType, COMMAND_SUPERCLASS));
            Logger.logOnVSCode("[" + id + "] Implements " + COMMAND_INTERFACE + ": " + ASTUtil.implementsInterface(returnType, COMMAND_INTERFACE));
            
            if(ASTUtil.implementsInterface(returnType, COMMAND_INTERFACE)){
                CommandNode commandNode = new CommandNode(node);
                
                List<ASTNode> arguments = (List<ASTNode>)method.arguments();
                for(ASTNode subnode : arguments){
                    Logger.logOnVSCode(subnode.toString());
                    CommandNode child = tryAddCommand(subnode);
                    if(child != null) {
                        commandNode.argumentCommands.add(getID(child.node));
                        child.parentCommand = id;                
                    }
                }                
                return commandNode;
            }
        }
        return null;
    }

    public String serialize(){
        Gson gson = new Gson();
        Logger.logOnVSCode("Command Model Json: " + gson.toJson(this));
        return gson.toJson(this);
    }

}
