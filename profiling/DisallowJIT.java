import jdk.vm.ci.hotspot.*;


public class DisallowJIT extends TestBase{

  public static void main(String[] args) throws Exception{
    HotSpotResolvedJavaMethod resolvedMethod = getResolvedMethod();
    resolvedMethod.setNotInlineable();

    runLoop(1000000);

    System.out.println("runLoop() finished. (compiled = "
                                     + resolvedMethod.hasCompiledCode() + ")");
  }

}

