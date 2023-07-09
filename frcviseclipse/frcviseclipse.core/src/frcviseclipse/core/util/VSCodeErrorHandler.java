package frcviseclipse.core.util;


public class VSCodeErrorHandler  implements Thread.UncaughtExceptionHandler {

    public void uncaughtException(Thread t, Throwable e) {
        Logger.errorOnVSCode("####Error in frcviseclipse: " + e.getMessage());
        Logger.errorOnVSCode("####Stacktrace: " + e.getStackTrace());
    }
}
