#!/bin/bash

cd ../
javac AeroDB.java
javac Snapshot.java
javac Entry.java
#first test for checking math functions
java AeroDB < tests/largecase1.in | diff - tests/largecase1.out
#second test for checking snapshot functions
java AeroDB < tests/largecase2.in | diff - tests/largecase2.out
if [ $? -eq "1" ]; then
    echo "Fail" 
fi

