#!/bin/bash
# This script installs the corresponding Web page and sources on
# my Web server. No interest to anybody except me.

WEBSOURCE=/home/kevin/docs/kzone5/source
WEBTARGET=/home/kevin/docs/kzone5/target
VERSION=0.1.1

cp bin/AndroidStart-debug.apk $WEBTARGET/AndroidStart-${VERSION}.apk
ant clean
(cd ..; zip -r $WEBTARGET/androidstart-${VERSION}.zip androidstart/)
cp *.html $WEBSOURCE
cp *.png $WEBTARGET
(cd $WEBSOURCE/..; ./make.pl androidstart)


