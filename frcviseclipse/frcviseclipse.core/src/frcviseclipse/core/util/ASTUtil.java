package frcviseclipse.core.util;

import org.eclipse.jdt.core.dom.ITypeBinding;

public class ASTUtil {
    public static boolean isSubclass(ITypeBinding binding, String superclassName){
        // TODO: Split into separate function
        if(binding.getQualifiedName().equals(superclassName)){
            return true;
        }
        if(binding.getQualifiedName().equals(Object.class.getName()) || binding.getSuperclass() == null){
            return false;
        }
        if(binding.getSuperclass().getQualifiedName().equals(superclassName)){
            return true;
        }
        for(ITypeBinding _interface: binding.getInterfaces()){

            if(_interface.getQualifiedName().equals(superclassName)){
                return true;
            }
        }
        
        return isSubclass(binding.getSuperclass(), superclassName);
    }
    public static boolean implementsInterface(ITypeBinding binding, String superclassName){
        if(binding.getQualifiedName().equals(superclassName)){
            return true;
        }
        for(ITypeBinding _interface: binding.getInterfaces()){
            if(_interface.getQualifiedName().equals(superclassName)){
                return true;
            }
        }
        return false;
    }

}
