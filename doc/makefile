###############################################################################
#     File Name           :     makefile                                      #
#     Created By          :     Klas Segeljakt                                #
#     Creation Date       :     [2016-08-21 21:25]                            #
#     Last Modified       :     [2017-02-19 15:02]                            #
#     Description         :     Makefile for project.                         #
###############################################################################
# > Settings ------------------------------------------------------------------
# <Project name>
NAME=Report
# <Source file format>
SRC_FMT=.md
# <Target file format>
TGT_FMT=.pdf
# <Referencing style>
STYLE=/Users/Klas/Git/templates/markdown/styles/elsevier-harvard.csl
# <Compiler>
CC=pandoc
# > Files ---------------------------------------------------------------------
DIR=.
SRC=$(DIR)/$(NAME)$(SRC_FMT)
TGT=$(DIR)/$(NAME)$(TGT_FMT)
BIB=$(DIR)/$(NAME).bib
TEM=$(DIR)/$(NAME).latex
#PLT=$(DIR)/Result.plot
# > Flags ---------------------------------------------------------------------
FLAGS += --bibliography $(BIB)          # Bibliography
FLAGS += --csl=$(STYLE)                 # Referencing style
FLAGS += -o $(TGT)                      # Output file
FLAGS += --template $(TEM)              # YAML template
FLAGS += -N                             # Numbered sections
FLAGS += -F pandoc-crossref
FLAGS += -F pandoc-citeproc
# > Gnuplot -------------------------------------------------------------------
#GNUPLOT = gnuplot $(PLT)
# > Compile -------------------------------------------------------------------
DOC = $(CC) $(SRC) $(FLAGS)
# > Functions =================================================================
all: $(NAME)
#------------------------------------------------------------------------------
$(NAME):
#	$(GNUPLOT)
	$(DOC)
#------------------------------------------------------------------------------
clean:
	rm $(TGT)
