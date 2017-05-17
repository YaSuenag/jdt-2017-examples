package helper;

import jdk.vm.ci.code.test.amd64.*;
import jdk.vm.ci.hotspot.*;
import jdk.vm.ci.meta.*;
import jdk.vm.ci.amd64.*;
import jdk.vm.ci.code.*;
import jdk.vm.ci.code.test.*;

import java.lang.reflect.*;


public class ExtendedAMD64TestAssembler extends AMD64TestAssembler{

  private final Field bciField;

  private final Method emitModRMMemoryMethod;

  private final Method emitMoveMethod;

  private final Method emitREXMethod;

  public ExtendedAMD64TestAssembler(CodeCacheProvider codeCache,
                                    TestHotSpotVMConfig config)
                             throws NoSuchFieldException, NoSuchMethodException{
    super(codeCache, config);

    bciField = HotSpotCompiledNmethod.class.getDeclaredField("entryBCI");
    bciField.setAccessible(true);
    emitModRMMemoryMethod = AMD64TestAssembler.class.getDeclaredMethod(
                               "emitModRMMemory", boolean.class,
                                    int.class, int.class, int.class, int.class);
    emitModRMMemoryMethod.setAccessible(true);

    emitMoveMethod = AMD64TestAssembler.class.getDeclaredMethod(
                     "emitMove", boolean.class, Register.class, Register.class);
    emitMoveMethod.setAccessible(true);

    emitREXMethod = AMD64TestAssembler.class.getDeclaredMethod(
                     "emitREX", boolean.class, int.class, int.class, int.class);
    emitREXMethod.setAccessible(true);
  }

  public Register getArgReg(int no, Class<?> klass, boolean isJava){
    HotSpotCallingConventionType type =
                    isJava ? HotSpotCallingConventionType.JavaCall
                           : HotSpotCallingConventionType.NativeCall;

    return codeCache.getRegisterConfig()
                    .getCallingConventionRegisters(
                                    type, JavaKind.fromJavaClass(klass))
                    .get(no);
  }

  public Register getReturnReg(Class<?> klass){
    return codeCache.getRegisterConfig()
                    .getReturnRegister(JavaKind.fromJavaClass(klass));
  }

  @Override
  public HotSpotCompiledCode finish(HotSpotResolvedJavaMethod method){
    HotSpotCompiledNmethod nm = (HotSpotCompiledNmethod)super.finish(method);

    /*
     * Set entry BCI to -1.
     * TestAssembler#finish() will set 0 to entryBCI.
     */
    try{
      Field bciField =
               HotSpotCompiledNmethod.class.getDeclaredField("entryBCI");
      bciField.setAccessible(true);
      bciField.setInt(nm, -1);
    }
    catch(NoSuchFieldException | IllegalAccessException e){
      throw new RuntimeException(e);
    }

    return nm;
  }

  public void emitSyscall(int syscallNo)
                       throws IllegalAccessException, InvocationTargetException{
    emitLoadIntAsLong(AMD64.rax, syscallNo);
    code.emitShort(0x050F); // SYSCALL: 0F 05
  }

  public void emitLoadEffectiveAddress(Register dest, Register src, int ofs)
                       throws IllegalAccessException, InvocationTargetException{
    emitModRMMemoryMethod.invoke(this, true, 0x8D,
                             dest.encoding, src.encoding, ofs); // LEA r64,r/m64
  }

  public void emitLoadPointer(Register dest, Register src, int ofs)
                       throws IllegalAccessException, InvocationTargetException{
    emitModRMMemoryMethod.invoke(this, true, 0x8B,
                             dest.encoding, src.encoding, ofs); // MOV r64,r/m64
  }

  public void emitMove(boolean w, Register dest, Register src)
                       throws IllegalAccessException, InvocationTargetException{
    emitMoveMethod.invoke(this, w, dest, src);
  }

  public void emitLoadIntAsLong(Register ret, int imm32)
                       throws IllegalAccessException, InvocationTargetException{
    byte regMask = (byte)(ret.encoding & 0x7);
    emitREXMethod.invoke(this, true, 0, 0, regMask);
    code.emitByte(0xC7 | regMask); // MOV r/m64, imm32
    code.emitByte((0b11 << 6) | regMask); // Mod: reg-reg (/0)
    code.emitInt(imm32);
  }

  private void emit256bitMove(Register ymmReg, Register reg,
                                                   byte opcode, byte ofs){
    /* Intel Software Developers Manual Vol.2A 2.3.5. The VEX Prefix */
    code.emitByte(0xC5); // 2-byte VEX
    code.emitByte(     (1 << 7) | // REX.R
                  (0b1111 << 3) | // Register specifier
                       (1 << 2) | // 256-bit vector
                           0b10   // opcode extention (F3)
                 );
    code.emitByte(opcode); // VMOVDQU

    byte modRM = (byte)(((ymmReg.encoding & 0x7) << 3) | (reg.encoding & 0x7));
    if(ofs == 0){
      code.emitByte(modRM);
    }
    else{
      code.emitByte((0b01 << 6) /* Mod: reg - mem+disp8 */ | modRM);
      code.emitByte(ofs);
    }

  }

  public void emit256bitMemToReg(Register ymmReg, Register src, byte ofs){
    emit256bitMove(ymmReg, src, (byte)0x6F, ofs);
  }

  public void emit256bitRegToMem(Register ymmReg, Register dest, byte ofs){
    emit256bitMove(ymmReg, dest, (byte)0x7F, ofs);
  }

  public void emit256bitParallelIntAdd(Register ymmDest,
                                   Register ymmSrc1, Register src2, byte ofs){
    /* Intel Software Developers Manual Vol.2A 2.3.5. The VEX Prefix */
    code.emitByte(0xC5); // 2-byte VEX
    code.emitByte(                        (1 << 7) | // REX.R
                  ((~ymmSrc1.encoding & 0xF) << 3) | // Register specifier
                                          (1 << 2) | // 256-bit vector
                                              0b01   // opcode extention (66)
                 );
    code.emitByte(0xFE); // VPADDD
    byte modRM = (byte)(((ymmDest.encoding & 0x7) << 3) |
                         (src2.encoding & 0x7));
    if(ofs == 0){
      code.emitByte(modRM);
    }
    else{
      code.emitByte((0b01 << 6) /* Mod: reg - mem+disp8 */ | modRM);
      code.emitByte(ofs);
    }
  }

}
