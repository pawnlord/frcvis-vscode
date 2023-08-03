package frcviseclipse.core;

import java.lang.reflect.Type;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.Expose;

import frcviseclipse.core.CommandModel.CommandType;
import frcviseclipse.core.util.ASTUtil;
import frcviseclipse.core.util.Logger;

public class CommandModel {
    // IN CASE I'M TESTING WITH OTHER SUPERCLASSES
    // public static final String COMMAND_SUPERCLASS =
    // "edu.wpi.first.wpilibj2.command.CommandBase";
    public static final String COMMAND_SUPERCLASS = "edu.wpi.first.wpilibj2.command.CommandBase";
    public static final String COMMAND_INTERFACE = "edu.wpi.first.wpilibj2.command.Command";

    public static final String SEQUENTIAL_SUPERCLASS = "edu.wpi.first.wpilibj2.command.SequentialCommandGroup";
    public static final String PARALLEL_SUPERCLASS = "edu.wpi.first.wpilibj2.command.ParallelCommand";

    public static final String SEQUENTIAL_FUNCTION = "sequence";
    public static final String PARALLEL_FUNCTION = "parallel";

    public static final String NEXT_NODE_ID = "~~NEXT_NODE_ID";

    public enum CommandType {
        Parallel(PARALLEL_SUPERCLASS, PARALLEL_FUNCTION),
        // TODO: Add parallel race and the like
        Sequential(SEQUENTIAL_SUPERCLASS, SEQUENTIAL_FUNCTION),
        Other("", ""); // Default

        CommandType(String _class, String functionName) {
            this._class = _class;
            // TODO: Genericize
            this.functionName = functionName;
        }

        String _class;
        String functionName;
    }

    // I don't know _why_ this is necessary, but if we don't do this, it tries
    // to cerialize the security context 
    public class CommandInfoAdapter implements JsonSerializer<CommandInfo> {
        @Override
        public JsonElement serialize(CommandInfo src, Type typeOfSrc, JsonSerializationContext context) {

            JsonObject obj = new JsonObject();

            obj.addProperty("type", src.type.toString());
            obj.addProperty("commandId", src.commandId);
            
            JsonArray arr = new JsonArray();
            for(var conn : src.connections){
                arr.add(conn);;
            }
            obj.add("connections", arr);;
            return obj;
        }
    }

    public static class CommandInfo {
        CommandType type;
        // How we organize where the DAG will point next
        // Should be serialized as commandIds
        List<String> connections = new ArrayList<>();
        String commandId;

        @Expose(serialize = false, deserialize = false)
        CommandNode node;

        public CommandInfo(CommandType type, String commandId) {
            this.type = type;
            this.commandId = commandId;
        }

        public void addConnection(String id) {
            if (connections.size() == 0) {
                connections.add(id);
            }

            switch (type) {
                case Parallel:
                    for (var cmd : node.argumentCommands) {
                        cmd.info.connections.add(id);
                    }
                    break;
                case Sequential:
                    CommandInfo last = this;
                    for (var cmd : node.argumentCommands) {
                        last = cmd.info;
                    }
                    last.addConnection(id);
                    break;
                case Other:
                    break;
            }
        }

        public void populateConnectionsFromNode(CommandNode node) {
            switch (type) {
                case Parallel:
                    for (var cmd : node.argumentCommands) {
                        connections.add(cmd.info.commandId);
                    }
                    break;
                case Sequential:
                    CommandInfo last = this;
                    for (var cmd : node.argumentCommands) {
                        last.connections.add(cmd.info.commandId);
                        last = cmd.info;
                    }

                    break;
                case Other:
                default:
                    break;
            }
        }
    }

    public CommandInfo infoFromConstructor(ClassInstanceCreation node, ITypeBinding binding) {
        CommandType type = CommandType.Other;
        for (var possibleType : CommandType.values()) {
            if (ASTUtil.isSubclass(binding, possibleType._class)) {
                type = possibleType;
            }
        }

        return new CommandInfo(type, getID(node));
    }

    public CommandInfo infoFromMethod(MethodInvocation node, ITypeBinding binding) {
        CommandType type = CommandType.Other;

        IMethodBinding methodBinding = node.resolveMethodBinding();

        Logger.logOnVSCode("~~~ NODE NAME " + methodBinding.getName());
        for (var possibleType : CommandType.values()) {
            if (ASTUtil.isSubclass(binding, possibleType._class)) {
                type = possibleType;
            }
            if (methodBinding.getName().equals(possibleType.functionName)) {
                type = possibleType;
            }
        }

        return new CommandInfo(type, getID(node));
    }

    public static class CommandNode {
        public List<CommandNode> argumentCommands;
        public List<String> requiredSubsystems;
        public ASTNode node;
        public CommandInfo info;

        public CommandNode(ASTNode node) {
            argumentCommands = new ArrayList<>();
            requiredSubsystems = new ArrayList<>();
            this.node = node;
        }

        public String toString() {
            return node.toString();
        }
    }

    Map<String, CommandNode> nodes;

    CommandNode rootNode;

    public void setRootNode(CommandNode rootNode) {
        this.rootNode = rootNode;
    }

    public static String getID(ASTNode node) {
        return node.toString() + ":" + node.getStartPosition();
    }

    // Take in unknown node and see if it should be added to the model
    public CommandNode tryAddCommand(ASTNode node) {
        // All AST nodes should be uniquely identified by there tokens and starting
        // position
        // TODO: Figure out if above statement is true
        String id = getID(node);

        int nodeType = node.getNodeType();
        if (nodeType == ASTNode.CLASS_INSTANCE_CREATION) {
            var instanceCreation = (ClassInstanceCreation) node;
            var type = instanceCreation.getType();
            ITypeBinding binding = type.resolveBinding();

            if (ASTUtil.implementsInterface(binding, COMMAND_INTERFACE)) {
                CommandNode commandNode = new CommandNode(node);

                List<ASTNode> arguments = (List<ASTNode>) instanceCreation.arguments();
                for (ASTNode subnode : arguments) {
                    CommandNode child = tryAddCommand(subnode);
                    if (child != null) {
                        commandNode.argumentCommands.add(child);
                    }
                }
                commandNode.info = infoFromConstructor(instanceCreation, binding);
                commandNode.info.populateConnectionsFromNode(commandNode);
                return commandNode;
            }

        } else if (nodeType == ASTNode.METHOD_INVOCATION) {
            var method = ((MethodInvocation) node);

            var binding = method.resolveTypeBinding();
            if (binding == null) {
                return null;
            }

            if (ASTUtil.implementsInterface(binding, COMMAND_INTERFACE)) {
                CommandNode commandNode = new CommandNode(node);

                List<ASTNode> arguments = (List<ASTNode>) method.arguments();
                for (ASTNode subnode : arguments) {
                    CommandNode child = tryAddCommand(subnode);
                    if (child != null) {
                        commandNode.argumentCommands.add(child);
                    }
                }
                commandNode.info = infoFromMethod(method, binding);
                commandNode.info.populateConnectionsFromNode(commandNode);
                return commandNode;
            }
        }
        return null;
    }

    public String serialize() {
        if (rootNode == null) {
            throw new InvalidParameterException("Root node not set!");
        }
        Gson gson = new GsonBuilder()
            .registerTypeAdapter(CommandInfo.class, new CommandInfoAdapter())
            .create();
        // Logger.logOnVSCode("Command Model Json: " + gson.toJson(this));
        return gson.toJson(rootNode.info);
    }

}
