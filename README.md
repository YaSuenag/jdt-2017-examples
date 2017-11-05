# JVMCI examples for Java Day Tokyo 2017

* http://www.oracle.co.jp/events/javaday/2017/
    * D1-C1: Panamaを先取り！？　JVMTIでJITと遊ぶ
    * https://www.slideshare.net/YaSuenag/panamajvmcijit

**If you want to check original source which is introduced in this presentation, please checkout `jdt-2017-session` tag.**

## Requirements

* GCC
* GNU make
* JDK 9 or later
* Test source of JVMCI (included in OpenJDK source code)

## How to build

```
$ cp <openjdk src>test/hotspot/jtreg/compiler/jvmci/jdk.vm.ci.code.test/src/jdk/vm/ci/code/test/TestAssembler.java code-injection/jdk/vm/ci/code/test/
$ cp <openjdk src>test/hotspot/jtreg/compiler/jvmci/jdk.vm.ci.code.test/src/jdk/vm/ci/code/test/TestHotSpotVMConfig.java code-injection/jdk/vm/ci/code/test/
$ cp <openjdk src>test/hotspot/jtreg/compiler/jvmci/jdk.vm.ci.code.test/src/jdk/vm/ci/code/test/amd64/AMD64TestAssembler.java code-injection/jdk/vm/ci/code/test/amd64/
$ make JAVA_HOME=/path/to/jdk9
```

**If you want to run this example on JDK 9, you might need to change the C++ type of `classMirrorHandleOffset` to `oop` in `TestHotSpotVMConfig.java` .**
[JDK-8186777](https://bugs.openjdk.java.net/browse/JDK-8186777) changes this type to `OopHandle` . So you might see `JVMCIError` on your console.

## How to run tests

### Show complation level

```
$ make JAVA_HOME=/path/to/jdk9 complv
```

### Show profiling data

```
$ make JAVA_HOME=/path/to/jdk9 showprof
```

### Disallow JIT compilation

```
$ make JAVA_HOME=/path/to/jdk9 disallow-jit
```

### Reprofile

```
$ make JAVA_HOME=/path/to/jdk9 reprofile
```

### VPADDD AVX2 instruction

```
$ make JAVA_HOME=/path/to/jdk9 padd
```

### System call (getpid)

```
$ make JAVA_HOME=/path/to/jdk9 syscall
```

### libc function (getuid)

```
$ make JAVA_HOME=/path/to/jdk9 libc
```

### dynamic function call

```
$ make JAVA_HOME=/path/to/jdk9 dyncall
```

## Disassemble JVMCI installed code with HSDIS

If you want to disassemble JVMCI installed code with hsdis, enable `-XX:+UnlockDiagnosticVMOptions` and `-XX:CompilerDirectivesFile` in `code-injection/Makefile` . They are comment outed.
Build and install instruction of hsdis is [here](https://www.slideshare.net/YaSuenag/java-9-62345544/69) (in Japanese).
