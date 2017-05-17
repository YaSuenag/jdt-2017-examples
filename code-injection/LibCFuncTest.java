import helper.*;


public class LibCFuncTest{

  public static class GetUID{

    /* uid_t is defined as int:
     *   /usr/include/bits/typesizes.h (__UID_T_TYPE)
     */
    public static int getUid(){
      throw new UnsupportedOperationException("from Java code");
    }

  }

  public static void main(String[] args) throws Exception{
    FuncLoader.setup();

    byte[] funcname = Util.generateNullTerminatedByteArray("getuid");
    long funcptr = FuncLoader.getFuncAddr(FuncLoader.RTLD_DEFAULT, funcname);
    if(funcptr == 0L){
      System.err.println("Could not find: getuid");
      System.exit(-1);
    }

    AsmInjector injector = new AsmInjector();
    injector.injectIntVoidFunc(funcptr, GetUID.class.getMethod("getUid"));

    System.out.println(GetUID.getUid());

    System.out.println("Press any key to exit...");
    System.in.read();
  }

}
