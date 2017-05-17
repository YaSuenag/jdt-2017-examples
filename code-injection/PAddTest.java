import java.util.*;
import java.util.stream.*;

import helper.*;


public class PAddTest{

  public static class PAddInt{
    public static void padd(int[] dest, int[] src){
      throw new UnsupportedOperationException("from Java code");
    }
  }

  public static void main(String[] args) throws Exception{
    AsmInjector injector = new AsmInjector();
    injector.injectParallelAdd(PAddInt.class.getMethod(
                                             "padd", int[].class, int[].class));

    int[] dest = {1, 2, 3, 4, 5, 6, 7, 8};
    int[] src =  {8, 7, 6, 5, 4, 3, 2, 1};
    PAddInt.padd(dest, src);
    System.out.println(IntStream.of(dest)
                                .mapToObj(Integer::toString)
                                .collect(Collectors.joining(" ")));

    System.out.println("Press any key to exit...");
    System.in.read();
  }

}
