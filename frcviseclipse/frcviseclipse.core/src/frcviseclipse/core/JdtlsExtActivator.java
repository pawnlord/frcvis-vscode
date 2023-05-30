package frcviseclipse.core;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class JdtlsExtActivator implements BundleActivator {

    private static BundleContext context;
    
    public JdtlsExtActivator () {

    }

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        JdtlsExtActivator.context = bundleContext;
        System.out.println("Activated!1!11!11!!!!");
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        JdtlsExtActivator.context = null;
    }


}