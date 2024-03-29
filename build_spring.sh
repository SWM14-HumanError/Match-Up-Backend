#!/bin/bash
set -a
build_part="spring"
version="${build_part}-v1.0"
docker_image_name="ghcr.io/jujemu/match-up"
docker_version="${version}-$(date +"%y%m%d")"

echo -e "\033[1;33m # Gradle build\033[0m"
chmod +x ./gradlew
./gradlew clean build

echo "\n"
echo -e "\033[1;33m # Dockerfile build\033[0m"
docker build --platform linux/amd64 -t ${docker_image_name}:${docker_version} .
docker build -t "${build_part}":test .
docker tag ${docker_image_name}:${docker_version} ${docker_image_name}:${build_part}

if [ ${PUSH} = "push" ]
then
  echo "\n"
  echo -e "\033[1;33m # Docker image push on ghcr.io/jujemu\033[0m"
  docker push ${docker_image_name}:${docker_version}
  docker push ${docker_image_name}:${build_part}
  docker rmi -f ${docker_image_name}:${docker_version}
fi

# if [ "${TEST}" = "test" ]
# then
#     echo "\n"
#     echo -e "\033[1;33m # Docker run image built for test\033[0m"
#     if docker ps -a | grep -q "${build_part}"; then
#         docker rm -f "${build_part}"
#     fi
#     docker run -d --name "${build_part}" -p 8080:8080 --network private "${build_part}":test
# fi