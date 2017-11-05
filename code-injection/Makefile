.PHONY: clean
.SUFFIXES: .java .class

JAVA ?= $(JAVA_HOME)/bin/java
JAVAC ?= $(JAVA_HOME)/bin/javac

ADD_MODULES = --add-modules jdk.internal.vm.ci \
              --add-modules jdk.internal.vm.compiler
EXPORT_MODULES = \
  --add-exports jdk.internal.vm.ci/jdk.vm.ci.meta=ALL-UNNAMED \
  --add-exports jdk.internal.vm.ci/jdk.vm.ci.code=ALL-UNNAMED \
  --add-exports jdk.internal.vm.ci/jdk.vm.ci.code.site=ALL-UNNAMED \
  --add-exports jdk.internal.vm.ci/jdk.vm.ci.runtime=ALL-UNNAMED,jdk.internal.vm.compiler \
  --add-exports jdk.internal.vm.ci/jdk.vm.ci.hotspot=ALL-UNNAMED,jdk.internal.vm.compiler \
  --add-exports jdk.internal.vm.ci/jdk.vm.ci.amd64=ALL-UNNAMED \
  --add-exports jdk.internal.vm.compiler/org.graalvm.compiler.api.runtime=ALL-UNNAMED \
  --add-exports jdk.internal.vm.compiler/org.graalvm.compiler.hotspot=ALL-UNNAMED

JAVA_OPTS += $(ADD_MODULES) $(EXPORT_MODULES) \
             --add-opens jdk.internal.vm.ci/jdk.vm.ci.hotspot=ALL-UNNAMED \
             -XX:+UnlockExperimentalVMOptions -XX:+EnableJVMCI

# If you disassemble with hsdis, please enable here:
#JAVA_OPTS += -XX:+UnlockDiagnosticVMOptions \
#             -XX:CompilerDirectivesFile=printasm-directive.json

JAVA_CLASSES = jdk/vm/ci/code/test/TestAssembler.class \
               jdk/vm/ci/code/test/TestHotSpotVMConfig.class \
               jdk/vm/ci/code/test/amd64/AMD64TestAssembler.class \
               helper/ExtendedAMD64TestAssembler.class \
               helper/AsmInjector.class \
               helper/Util.class \
               SyscallTest.class \
               FuncLoader.class \
               LibCFuncTest.class \
               DynCallTest.class \
               PAddTest.class

all: $(JAVA_CLASSES)
	$(MAKE) -C native

.java.class:
	$(JAVAC) $(ADD_MODULES) $(EXPORT_MODULES) $<

clean:
	find . -name "*.class" -exec $(RM) {} \;
	$(MAKE) -C native clean

syscall: SyscallTest.class
	$(JAVA) $(JAVA_OPTS) SyscallTest

libc: LibCFuncTest.class
	$(JAVA) $(JAVA_OPTS) LibCFuncTest

# libnative.so in ./native should be compiled before running this target.
dyncall: DynCallTest.class
	$(JAVA) $(JAVA_OPTS) DynCallTest ./native/libnative.so call_native

padd: PAddTest.class
	$(JAVA) $(JAVA_OPTS) PAddTest

