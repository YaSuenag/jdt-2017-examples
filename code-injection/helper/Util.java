package helper;


public class Util{

  public static byte[] generateNullTerminatedByteArray(String str){
    byte[] inBytes = str.getBytes();
    byte[] inBytes_arg = new byte[inBytes.length + 1]; // add null char
    System.arraycopy(inBytes, 0, inBytes_arg, 0, inBytes.length);
    inBytes_arg[inBytes.length] = (byte)0;

    return inBytes_arg;
  }

  public static String generateStringFromNullTerminatedByteArray(byte[] str){
    return new String(str, 0, str.length - 1);
  }

}
