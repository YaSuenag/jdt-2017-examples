import java.lang.reflect.*;

import helper.*;


public class FuncLoader{

  // from /usr/include/dlfcn.h
  public static final long RTLD_DEFAULT = 0L;

  public static long loadLibrary(byte[] libnameInBytes, byte[] errMsgInBytes){
    throw new UnsupportedOperationException("from Java code");
  }

  public static long getFuncAddr(long handle, byte[] symbolInBytes){
    throw new UnsupportedOperationException("from Java code");
  }

  public static void setup() throws NoSuchFieldException,
                                    NoSuchMethodException,
                                    IllegalAccessException,
                                    InvocationTargetException{
    AsmInjector injector;
 
    injector = new AsmInjector();
    injector.injectLoadFunc(
         FuncLoader.class.getMethod("loadLibrary", byte[].class, byte[].class));

    injector = new AsmInjector();
    injector.injectLookupFunc(
           FuncLoader.class.getMethod("getFuncAddr", long.class, byte[].class));

  }

}

