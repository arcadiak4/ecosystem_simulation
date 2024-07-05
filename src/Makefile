# MAKEFILE pour src5+

## DEPENDANCES ##
# x-terminal-emulator
# python 2
# matplotlib 2.2.2

## MACROS ##

CC = javac
#CFLAGS = 
#AR =
#ARFLAGS =

SRC_LIB = World.java Cell.java Biomes.java BiomeOcean.java BiomeDesert.java BiomeTemperate.java BiomeMountain.java Agents.java Firefox.java Iceweasel.java FailSpreadException.java Animation.java Frame.java Sprite.java PathFinding.java Node.java
SRC_PRG = MainWorld.java 

OBJ_LIB = $(SRC_LIB:.java=.class)
OBJ_PRG = $(SRC_PRG:.java=.class)

TEMP_FILES = ./data/biomes_data.csv ./data/data.csv

## COMMANDES ##

.PHONY : all clean run runx show
all : $(OBJ_PRG) $(OBJ_LIB)
run : all
	rm -f $(TEMP_FILES)
	java MainWorld
runx : all
	rm -f $(TEMP_FILES)
	x-terminal-emulator -geometry 1x1+0-0 -e java MainWorld
clean :
	-rm -f $(OBJ_PRG) $(OBJ_LIB) $(TEMP_FILES)
show :
	cd data; x-terminal-emulator -geometry 1x1+0-0 -e python plot_distrib.py
	cd data; python plot_data.py

## CONSTRUCTEURS ##

$(OBJ_PRG) : $(SRC_PRG)
	$(CC) $< $(SRC_LIB)
%.class: %.java
	$(CC) $<
