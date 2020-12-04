#!/bin/bash

cd compiler/ || exit

if [ ! -d build ]; then
  mkdir build
fi

cd build || exit

cmake ..
sudo make install