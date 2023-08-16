#!/usr/bin/env bash

# find idle profile 
function find_idle_profile()
{
    RESPONSE_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://match-up.xyz/profile)

    if [ ${RESPONSE_CODE} -ge 400 ] 
    then 
        CURRENT_PROFILE=green
    else
        CURRENT_PROFILE=$(curl -s http://match-up.xyz/profile)
    fi

    if [ ${CURRENT_PROFILE} == blue ]
    then
        IDLE_PROFILE=green
    else
        IDLE_PROFILE=blue
    fi

    echo "${IDLE_PROFILE}"
}

# find idle port
function find_idle_port()
{
    IDLE_PROFILE=$(find_idle_profile)

    if [ ${IDLE_PROFILE} == blue]
    then
        echo "8081"
    else
        echo "8082"
    fi
}