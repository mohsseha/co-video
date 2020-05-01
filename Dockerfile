FROM clojure
RUN mkdir /workdir
COPY project.clj /workdir
WORKDIR /workdir
RUN lein deps
COPY . /workdir
RUN lein package
CMD ["lein", "run"]
