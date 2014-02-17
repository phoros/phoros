# phoros #

Archival repository for editions of classical Athenian epigraphic texts related to tribute.

Contents include:

- TEI-conformant XML editions of texts with accompanying catalog ([texts](https://github.com/neelsmith/phoros/tree/master/texts))
- Citable collections of structured data in plain-text `.csv` files, with accompanying XML catalog ([collections](https://github.com/neelsmith/phoros/tree/master/collections))
- Indices linking pairs of citable objects, with accompanying catalog of relationships ([indices](https://github.com/neelsmith/phoros/tree/master/indices))
- A catalog of citable images in plain-text `.csv` files ([images](https://github.com/neelsmith/phoros/tree/master/images)).  Binary images are available for download from <http://shot.holycross.edu/eikon/bannan-epigraphy/>



## License ##

All content in this repository is licensed under the Creative Commons Attribution-ShareAlike 3.0 Unported license, [http://creativecommons.org/licenses/by-sa/3.0/](http://creativecommons.org/licenses/by-sa/3.0/).

## Building ##

The CITE collections in the file `payments.csv` can be rebuilt at any time from the XML source files of the text editions by running the gradle task

    gradle makedb

The organization of the main directories in this repository can be mapped directly on to the configuration of a
CITE repository managed with the CITE manager tool, <https://github.com/neelsmith/citemgr>, and servable as a java
servlet using  the CITE servlet, <https://github.com/neelsmith/citeservlet>.
