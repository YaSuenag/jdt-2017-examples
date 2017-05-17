import java.util.*;


public class PAddTestPureJava{

  public static class PAddInt{

    public static void padd(int[] dest, int[] src){
      for(int i = 0; i < 8; i++){
        dest[i] += src[i];
      }
    }

  }

  public static void main(String[] args) throws Exception{
    Random rand = new Random();
    int[] dest = new int[8];
    int[] src  = new int[8];

    for(int i = 0; i < 1000000; i++){

      for(int j = 0; j < 8; j++){
        dest[j] = rand.nextInt();
        src[j] = rand.nextInt();
      }

      PAddInt.padd(dest, src);
    }

    System.out.println("Check compiled code...");
    System.in.read();
  }

}
