#!/usr/bin/env bash

file=$2
python parser.py $file
name=${file:-4}
if [ "$1" == "Java" ]; then
    python java_gen.py $name
elif [ "$1" == "Python" ]; then
    python python_gen.py $name
elif [ "$1" == "Haskell" ]; then
    python haskell_gen.py $name
elif [ "$1" == "Cpp" ]; then
    java -jar Cpp_gen.jar $name
fi