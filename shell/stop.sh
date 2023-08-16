#!/usr/bin/env bash

ABSPATH=$(readlink -f $0)
ABSDIR=$(dirname $ABSPATH)
source ${ABSDIR}/profile.sh

IDLE_PORT=$(find_idle_port)

echo "> 쉬고 있는 포트, $IDLE_PORT 의 PID"
IDLE_PID=$(lsof -ti tcp:${IDLE_PORT})

if [ -z ${IDLE_PID} ]
then
  echo "> 해당 포트의 server가 쉬고있다"
else
  echo "> 쉬고있어야할 server가 켜져있어서 SIGTERM $IDLE_PID"
  kill -15 ${IDLE_PID}
  sleep 5
fi