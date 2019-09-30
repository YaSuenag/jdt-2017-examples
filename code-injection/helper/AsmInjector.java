package helper;

import jdk.vm.ci.meta.*;
import jdk.vm.ci.code.*;
import jdk.vm.ci.runtime.*;
import jdk.vm.ci.hotspot.*;
import jdk.vm.ci.amd64.*;

import org.graalvm.compiler.api.runtime.*;
import org.graalvm.compiler.hotspot.*;

import jdk.vm.ci.code.test.*;

import java.lang.reflect.*;
import sun.misc.*;


public class AsmInjector{

  private MetaAccessProvider metaAccess;

  private CodeCacheProvider codeCache;

  private TestHotSpotVMConfig config;

  private GraalHotSpotVMConfig graalConfig;

  private ExtendedAMD64TestAssembler asm;

  public AsmInjector() throws NoSuchFieldException, NoSuchMethodException{
    JVMCIBackend backend = JVMCI.getRuntime().getHostJVMCIBackend();
    metaAccess = backend.getMetaAccess();
    codeCache = backend.getCodeCache();
    config = new TestHotSpotVMConfig(HotSpotJVMCIRuntime.runtime()
                                                        .getConfigStore());
    GraalJVMCICompiler compiler = (GraalJVMCICompiler)JVMCI.getRuntime()
                                                           .getCompiler();
    HotSpotGraalRuntimeProvider graalRuntime =
                        (HotSpotGraalRuntimeProvider)compiler.getGraalRuntime();
    graalConfig = graalRuntime.getVMConfig();
    asm = new ExtendedAMD64TestAssembler(codeCache, config);
  }

  public InstalledCode injectSyscall0(int syscallNo, Method method)
                       throws IllegalAccessException, InvocationTargetException{
    HotSpotResolvedJavaMethod resolvedMethod =
                 (HotSpotResolvedJavaMethod)metaAccess.lookupJavaMethod(method);

    asm.emitPrologue();
    asm.emitSyscall(syscallNo);
    asm.emitIntRet(AMD64.rax);
    asm.emitEpilogue();

    HotSpotCompiledCode code = asm.finish(resolvedMethod);
    resolvedMethod.setNotInlinableOrCompilable();
    return codeCache.setDefaultCode(resolvedMethod, code);
  }

  public InstalledCode injectLoadFunc(Method method)
                       throws IllegalAccessException, InvocationTargetException{
    HotSpotResolvedJavaMethod resolvedMethod =
                 (HotSpotResolvedJavaMethod)metaAccess.lookupJavaMethod(method);

    asm.emitPrologue();
    Register arg0 = asm.getArgReg(0, byte[].class, true);
    Register arg1 = asm.getArgReg(1, byte[].class, true);
    asm.emitLoadEffectiveAddress(asm.getArgReg(0, byte[].class, false),
                                 arg0,
                                 Unsafe.ARRAY_BYTE_BASE_OFFSET); // 1st argument
    asm.emitLoadEffectiveAddress(asm.getArgReg(1, byte[].class, false),
                                 arg1,
                                 Unsafe.ARRAY_BYTE_BASE_OFFSET); // 2nd argument
    asm.emitLoadPointer(asm.getArgReg(2, byte[].class, false),
                        arg1,
                        graalConfig.arrayOopDescLengthOffset()); // 3rd argument

    asm.emitCall(graalConfig.dllLoad);
    asm.emitPointerRet(asm.getReturnReg(long.class));
    asm.emitEpilogue();

    HotSpotCompiledCode code = asm.finish(resolvedMethod);
    resolvedMethod.setNotInlinableOrCompilable();
    return codeCache.setDefaultCode(resolvedMethod, code);
  }

  public InstalledCode injectLookupFunc(Method method)
                       throws IllegalAccessException, InvocationTargetException{
    HotSpotResolvedJavaMethod resolvedMethod =
                 (HotSpotResolvedJavaMethod)metaAccess.lookupJavaMethod(method);

    asm.emitPrologue();
    Register arg0 = asm.getArgReg(0, long.class, true);
    Register arg1 = asm.getArgReg(1, byte[].class, true);
    asm.emitMove(true,
                 asm.getArgReg(0, long.class, false), arg0); // 1st argument
    asm.emitLoadEffectiveAddress(asm.getArgReg(1, byte[].class, false),
                                 arg1,
                                 Unsafe.ARRAY_BYTE_BASE_OFFSET); // 2nd argument
    asm.emitCall(graalConfig.dllLookup);
    asm.emitPointerRet(asm.getReturnReg(long.class));
    asm.emitEpilogue();

    HotSpotCompiledCode code = asm.finish(resolvedMethod);
    resolvedMethod.setNotInlinableOrCompilable();
    return codeCache.setDefaultCode(resolvedMethod, code);
  }

  public InstalledCode injectIntVoidFunc(long addr, Method method){
    HotSpotResolvedJavaMethod resolvedMethod =
                 (HotSpotResolvedJavaMethod)metaAccess.lookupJavaMethod(method);

    asm.emitPrologue();
    asm.emitCall(addr);
    asm.emitIntRet(asm.getReturnReg(int.class));
    asm.emitEpilogue();

    HotSpotCompiledCode code = asm.finish(resolvedMethod);
    resolvedMethod.setNotInlinableOrCompilable();
    return codeCache.setDefaultCode(resolvedMethod, code);
  }

  public InstalledCode injectVoidFunc(long addr, Method method){
    HotSpotResolvedJavaMethod resolvedMethod =
                 (HotSpotResolvedJavaMethod)metaAccess.lookupJavaMethod(method);

    asm.emitPrologue();
    asm.emitCall(addr);
    asm.emitPointerRet(asm.getReturnReg(Object.class));
    asm.emitEpilogue();

    HotSpotCompiledCode code = asm.finish(resolvedMethod);
    resolvedMethod.setNotInlinableOrCompilable();
    return codeCache.setDefaultCode(resolvedMethod, code);
  }

  public InstalledCode injectParallelAdd(Method method)
                       throws IllegalAccessException, InvocationTargetException{
    HotSpotResolvedJavaMethod resolvedMethod =
                 (HotSpotResolvedJavaMethod)metaAccess.lookupJavaMethod(method);

    Register arg0 = asm.getArgReg(0, int[].class, true);
    Register arg1 = asm.getArgReg(1, int[].class, true);
    byte arrayOfs = (byte)Unsafe.ARRAY_INT_BASE_OFFSET;

    asm.emitPrologue();
    asm.emit256bitMemToReg(AMD64.xmm1, arg1, arrayOfs);
    asm.emit256bitParallelIntAdd(AMD64.xmm0, AMD64.xmm1, arg0, arrayOfs);
    asm.emit256bitRegToMem(AMD64.xmm0, arg0, arrayOfs);
    asm.emitPointerRet(asm.getReturnReg(Object.class));
    asm.emitEpilogue();

    HotSpotCompiledCode code = asm.finish(resolvedMethod);
    resolvedMethod.setNotInlinableOrCompilable();
    return codeCache.setDefaultCode(resolvedMethod, code);
  }

}

