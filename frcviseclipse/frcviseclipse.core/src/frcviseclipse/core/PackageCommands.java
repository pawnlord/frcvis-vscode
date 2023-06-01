package frcviseclipse.core;

import java.util.List;
import frcviseclipse.core.util.Logger;

public class PackageCommands {
    public static String CMD_LOG = "frcvis.log";
    public static String CMD_ERROR = "frcvis.error";

    public static void displayTree(List<Object> arguments){
        if(arguments.size() < 3){
            Logger.errorOnVSCode("Not enough arguments for displayTreeJava");
            return;
        }
        String uri = (String)(arguments.get(0));
        Double line = (Double)(arguments.get(1));
        Double colmn = (Double)(arguments.get(2));
        Logger.logOnVSCode("Received: " + uri + ":" + line + ":" + colmn);
        Logger.logOnVSCode("Is float");
    }
}
