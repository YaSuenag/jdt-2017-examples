PROFILING_TARGET = complv showprof disallow-jit reprofile
CODE_INJECTION_TARGET = syscall libc dyncall padd

all:
	$(MAKE) -C profiling
	$(MAKE) -C code-injection

clean:
	$(MAKE) -C profiling $@
	$(MAKE) -C code-injection $@

$(PROFILING_TARGET):
	$(MAKE) -C profiling $@

$(CODE_INJECTION_TARGET):
	$(MAKE) -C code-injection $@

