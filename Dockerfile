FROM clojure
COPY . /workdir
WORKDIR /workdir
CMD ["lein", "figwheel"]
