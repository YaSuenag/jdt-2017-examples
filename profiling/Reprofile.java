import jdk.vm.ci.hotspot.*;


public class Reprofile extends TestBase{

  public static void main(String[] args) throws Exception{
    runLoop(1000000);

    HotSpotResolvedJavaMethod resolvedMethod = getResolvedMethod();
    System.out.println("runLoop() finished.");
    System.in.read();

    resolvedMethod.reprofile();
    System.out.println("reprofile.");
    System.in.read();

    runLoop(1000000);
    System.out.println("runLoop() finished.");
    System.out.println();
    System.out.println("Press any key to exit...");
    System.in.read();
  }

}

