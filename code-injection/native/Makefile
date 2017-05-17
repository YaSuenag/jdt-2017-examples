.PHONY: clean

CC ?= gcc
CFLAGS += -g -shared -fPIC
LDFLAGS += -lpthread
TARGET = libnative.so
SOURCES = native.c

all: $(TARGET)

$(TARGET):
	$(CC) $(CFLAGS) -o $@ $(SOURCES) $(LDFLAGS)

clean:
	$(RM) $(TARGET)

