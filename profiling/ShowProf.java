import java.util.*;
import java.util.stream.*;

import jdk.vm.ci.hotspot.*;
import jdk.vm.ci.meta.*;


public class ShowProf extends TestBase{

  public static void main(String[] args) throws Exception{
    runLoop(1000000);

    HotSpotResolvedJavaMethod resolvedMethod = getResolvedMethod();
    ProfilingInfo profiling = resolvedMethod.getProfilingInfo();

    System.out.println(profiling);
    System.out.println();
    System.out.println("  Compile level = " + IntStream.rangeClosed(0, 4)
                                                       .filter(resolvedMethod::hasCompiledCodeAtLevel)
                                                       .findAny()
                                                       .getAsInt());
    System.out.println("isMature = " + profiling.isMature());

    for(int bci = 0; bci <= 30; bci++){
      StringJoiner joiner = new StringJoiner(", ");

      if(profiling.getExecutionCount(bci) != -1){
        joiner.add("execution count = " + profiling.getExecutionCount(bci));
      }
      if(profiling.getExceptionSeen(bci).isTrue()){
        joiner.add("exception");
      }
      if(profiling.getBranchTakenProbability(bci) != -1){
        joiner.add("branch = " + profiling.getBranchTakenProbability(bci));
      }

      if(joiner.length() != 0){
        System.out.println("BCI " + bci + ": " + joiner.toString());
      }
    }

    System.out.println("Press any key to exit...");
    System.in.read();
  }

}

