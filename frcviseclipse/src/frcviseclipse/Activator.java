package frcviseclipse;

import java.io.FileWriter;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
    	FileWriter out = null;
    	try {
    		out = new FileWriter("output.txt");
    		out.append("Hello, World!\n");
    	} finally {
    		if(out != null) {
    			out.close();
    		}
    	}
	}

	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

}
