import java.util.stream.*;

import jdk.vm.ci.hotspot.*;


public class CompLevel extends TestBase{

  public static void main(String[] args) throws Exception{
    runLoop(1000000);

    HotSpotResolvedJavaMethod resolvedMethod = getResolvedMethod();

    System.out.println(resolvedMethod.getName());
    System.out.println("  hasCompiledCode = " +
                                    resolvedMethod.hasCompiledCode());
    System.out.println("  Compile level = " + IntStream.rangeClosed(0, 4)
                                                       .filter(resolvedMethod::hasCompiledCodeAtLevel)
                                                       .findAny()
                                                       .getAsInt());
  }

}

