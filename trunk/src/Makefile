###############################################################################
#
# Makefile for Java project 10
#
# Students:
# Itay Sabato, ID 036910008, itay.sabato@cs.huji.ac.il
# Rotem Barzilay, ID 300618592, rotem.barzilay@cs.huji.ac.il
#
###############################################################################

JAVAC=javac
JAVACFLAGS=

SRCS=*.java
EXEC=JackAnalyzer

TAR=tar
TARFLAGS=cvf
TARNAME=project10.tar
TARSRCS= README Makefile $(EXEC) *.java

all: compile

compile:
	$(JAVAC) $(JAVACFLAGS) $(SRCS)
	chmod +x $(EXEC)

tar:
	$(TAR) $(TARFLAGS) $(TARNAME) $(TARSRCS)

clean:
	rm -f *.class *~

