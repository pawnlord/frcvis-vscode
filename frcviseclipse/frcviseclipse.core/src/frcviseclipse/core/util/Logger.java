package frcviseclipse.core.util;
import frcviseclipse.core.PackageCommands;

import org.eclipse.jdt.ls.core.internal.JavaLanguageServerPlugin;

public class Logger {
    public static void logOnVSCode(String message){
        JavaLanguageServerPlugin.getInstance().getClientConnection().executeClientCommand(PackageCommands.CMD_LOG, message);
    }    
    public static void errorOnVSCode(String message){
        JavaLanguageServerPlugin.getInstance().getClientConnection().executeClientCommand(PackageCommands.CMD_ERROR, message);
    }
}