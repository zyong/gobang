#/bin/bash
FILE=$(cd `dirname $0`; pwd)
PATH=$(dirname $FILE)
# cp resources
RES=$PATH/src/main/res/

cd $PATH/target

if [[ -d "./res" ]]; then
    echo "deleting old resources directory"
    /bin/rm -rf ./res
fi

/bin/mkdir res
/bin/cp -R $RES/* ./res

echo "making macos app"
/Library/Java/JavaVirtualMachines/jdk-14.0.1.jdk/Contents/Home/bin/jpackage \
 --input . \
 --main-jar gobang-1.0-SNAPSHOT-jar-with-dependencies.jar \
 --main-class App \
 --icon res/gobang.icns \
 --name gobang-robot