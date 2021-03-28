FROM gradle:jdk11
USER root
RUN  mkdir /app
COPY . /app
WORKDIR /app
#RUN ./gradlew installDist
#RUN ./build/install/erss-hwk3-ys319-qs33/bin/erss-hwk3-ys319-qs33



