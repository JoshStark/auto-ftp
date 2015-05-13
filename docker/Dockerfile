FROM ubuntu:14.04
MAINTAINER Josh Stark <jagfin1@gmail.com>

# Get hold of latest stable release of Java 7
RUN apt-get update && apt-get -y install openjdk-7-jre && apt-get clean

# Set up a default application user for within the container
# We don't want the app running as root, so this user defaults to 1000
RUN groupadd autoftp && \
    useradd autoftp -g autoftp -u 1000

# Configure the running and downloadable directories
RUN mkdir /opt/autoftp
RUN mkdir -p /download/doing
RUN mkdir -p /download/done

RUN chown autoftp:autoftp /opt/autoftp && \
    chown autoftp:autoftp /download/doing && \
    chown autoftp:autoftp /download/done

# Get hold of the latest stable release of AutoFTP as an executable
ADD https://github.com/JAGFin1/auto-ftp/releases/download/1.2/auto-ftp.jar /opt/autoftp/

WORKDIR /opt/autoftp
USER autoftp

# Run the app!
CMD ["java", "-jar", "-Xms30M", "-Xmx100M", "auto-ftp.jar"]
