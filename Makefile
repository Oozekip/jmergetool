# Copyright (c) 2016 Matt Samudio (Zenteknix)  All Rights Reserved.
# Contact information for Zenteknix is available at http://www.zenteknix.com
#
# This file is part of JMergeTool.
#
# JMergeTool is free software: you can redistribute it and/or modify
# it under the terms of the GNU Lesser General Public License as
# published by the Free Software Foundation, either version 3 of
# the License, or (at your option) any later version.
#
# JMergeTool is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Lesser General Public License for more details.
#
# You should have received a copy of the
# GNU Lesser General Public License along with JMergeTool.
# If not, see <http://www.gnu.org/licenses/>.
#SLD = /usr/local/lib
SLD = ${HOME}/lib
SRC = $(wildcard ./com/ztx/jmergetool/*.java)
OBJ = $(SRC:.java=.class)
RSD = "root@nowhere://opt/samba/pub/archive/baseline/jmergetool"
WWW = "webhost://home/webserver/www/"

all : app
	@echo Done

clean :
	@rm -f com/ztx/jmergetool/*.class
	@rm -f jmergetool.jar

jar : app
	@rm -f jmergetool.jar
	@jar cf jmergetool.jar com/ztx/jmergetool/*.class
	@echo Jar file built

deploywww :
	@rsync -ai --no-owner --no-group --no-perms --no-times --exclude='*.swp' ./www/ $(WWW)

sync : clean
	@rsync -ai --exclude='.git*' ./ ${RSD}/

app : $(OBJ)
	@echo JMergeTool build OK

%.class : %.java
	javac -d . -cp "${SLD}/jztx.jar:${SLD}/jmergetool.jar:." $<

%.class : %_class.bin
	cp $< $@
