#!/bin/bash

bin/flume-ng agent --conf ./conf/ -f conf/flume2-es.conf -Dflume.root.logger=DEBUG,console -n a1 -Dflume.monitoring.type=http -Dflume.monitoring.port=9081
