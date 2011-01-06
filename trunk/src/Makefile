###############################################################################
#
# Makefile for Java project 11
#
# Students:
# Itay Sabato, ID 036910008, itay.sabato@cs.huji.ac.il
# Rotem Barzilay, ID 300618592, rotem.barzilay@cs.huji.ac.il
#
###############################################################################

JAVAC=javac
JAVACFLAGS=

SRCS=*.java
EXEC=JackCompiler

TAR=tar
TARFLAGS=cvf
TARNAME=project11.tar
TARSRCS= README Makefile $(EXEC) *.java

all: compile

compile:
	$(JAVAC) $(JAVACFLAGS) $(SRCS)
	chmod +x $(EXEC)

tar:
	$(TAR) $(TARFLAGS) $(TARNAME) $(TARSRCS)

clean:
	rm -f *.class *~

