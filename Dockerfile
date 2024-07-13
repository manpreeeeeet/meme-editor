FROM ubuntu:22.04
RUN apt-get update && apt-get -y upgrade
RUN apt-get install openjdk-17-jre -y
COPY . /usr/home/meme-editor
WORKDIR /usr/home/meme-editor/build/libs
ENTRYPOINT ["java", "-jar", "meme-editor-1.0-standalone.jar", "880"]