import jdk.vm.ci.runtime.*;
import jdk.vm.ci.hotspot.*;


public class TestBase{

  public static int runLoop(int max){
    int result = 0;

    for(int i = 1; i <= max; i++){
      result += 1;
    }

    return result;
  }

  public static HotSpotResolvedJavaMethod getResolvedMethod()
                                                  throws NoSuchMethodException{
    JVMCIBackend backend = JVMCI.getRuntime().getHostJVMCIBackend();
    HotSpotMetaAccessProvider metaAccess =
                             (HotSpotMetaAccessProvider)backend.getMetaAccess();
    return (HotSpotResolvedJavaMethod)metaAccess.lookupJavaMethod(
                                TestBase.class.getMethod("runLoop", int.class));
  }

}
