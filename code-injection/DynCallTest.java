import helper.*;


public class DynCallTest{

  public static void callNative(){
    throw new UnsupportedOperationException("from Java code");
  }

  public static void main(String[] args) throws Exception{
    FuncLoader.setup();
    byte[] libname = Util.generateNullTerminatedByteArray(args[0]);
    byte[] errMsg = new byte[1024];

    long handle = FuncLoader.loadLibrary(libname, errMsg);
    if(handle == 0L){
      System.err.println(
                   Util.generateStringFromNullTerminatedByteArray(errMsg));
      System.exit(-1);
    }

    byte[] funcname = Util.generateNullTerminatedByteArray(args[1]);
    long funcptr = FuncLoader.getFuncAddr(handle, funcname);
    if(funcptr == 0L){
      System.err.println("Could not find: " + args[1]);
      System.exit(-2);
    }

    AsmInjector injector = new AsmInjector();
    injector.injectVoidFunc(funcptr, DynCallTest.class.getMethod("callNative"));

    callNative();

    System.out.println("Press any key to exit...");
    System.in.read();
  }

}
