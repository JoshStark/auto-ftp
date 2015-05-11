FROM ubuntu:14.04
MAINTAINER Josh Stark <jagfin1@gmail.com>
RUN apt-get update
RUN apt-get -y install openjdk-7-jre && apt-get clean
RUN mkdir /opt/autoftp
ADD https://github.com/JAGFin1/auto-ftp/releases/download/1.0/auto-ftp.jar /opt/autoftp/
WORKDIR /opt/autoftp
CMD ["java", "-jar", "-Xms30M", "-Xmx100M", "auto-ftp.jar"]
