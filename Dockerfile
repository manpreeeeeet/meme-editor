FROM ubuntu:22.04
RUN apt update && apt -y upgrade
RUN apt -y install openjdk-17-jre
COPY . /usr/home/meme-editor
WORKDIR /usr/home/meme-editor/build/libs
ENTRYPOINT ["java", "-jar", "meme-editor-1.0-SNAPSHOT-standalone.jar", "8080"]