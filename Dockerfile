FROM clojure
COPY . /workdir
WORKDIR /workdir
lein package
lein run
CMD ["lein", "figwheel"]
