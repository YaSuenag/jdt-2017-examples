.PHONY: clean
.SUFFIXES: .java .class

JAVA ?= $(JAVA_HOME)/bin/java
JAVAC ?= $(JAVA_HOME)/bin/javac
JAVA_OPTS += --permit-illegal-access \
             -XX:+UnlockExperimentalVMOptions -XX:+EnableJVMCI

ADD_MODULES = --add-modules jdk.internal.vm.ci
EXPORT_MODULES = \
  --add-exports jdk.internal.vm.ci/jdk.vm.ci.runtime=ALL-UNNAMED \
  --add-exports jdk.internal.vm.ci/jdk.vm.ci.hotspot=ALL-UNNAMED \
  --add-exports jdk.internal.vm.ci/jdk.vm.ci.meta=ALL-UNNAMED

JAVA_CLASSES = TestBase.class \
               CompLevel.class \
               ShowProf.class \
               DisallowJIT.class \
               Reprofile.class

all: $(JAVA_CLASSES)

.java.class:
	$(JAVAC) $(ADD_MODULES) $(EXPORT_MODULES) $<

clean:
	find . -name "*.class" -exec $(RM) {} \;

complv: CompLevel.class
	$(JAVA) $(JAVA_OPTS) CompLevel

showprof: ShowProf.class
	#$(JAVA) $(JAVA_OPTS) -XX:TieredStopAtLevel=2 ShowProf
	$(JAVA) $(JAVA_OPTS) ShowProf

disallow-jit: DisallowJIT.class
	$(JAVA) $(JAVA_OPTS) -XX:+PrintCompilation DisallowJIT

reprofile: Reprofile.class
	$(JAVA) $(JAVA_OPTS) -XX:+PrintCompilation Reprofile

