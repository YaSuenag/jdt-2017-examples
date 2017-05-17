import helper.*;

import jdk.vm.ci.code.*;


public class SyscallTest{

  public static class GetPID{

    /* from /usr/include/asm/unistd_64.h */
    public static final int __NR_getpid = 39;

    public static int getPid(){
      throw new UnsupportedOperationException("from Java code");
    }

  }

  public static void main(String[] args) throws Exception{
    AsmInjector injector = new AsmInjector();
    InstalledCode code = injector.injectSyscall0(GetPID.__NR_getpid,
                                              GetPID.class.getMethod("getPid"));
    System.out.println("Installed address: 0x" +
                                    Long.toHexString(code.getAddress()));
    System.out.println(GetPID.getPid());
    System.out.println("Press any key to exit...");
    System.in.read();
  }

}

