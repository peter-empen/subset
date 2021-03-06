# Subset

**Subset** 1.x is a library to ease extracting fields from MongoDB documents,
serializing them back and constructing queries.

**Subset** may help you to

* define typed fields and feel safe about both the types of values your application
  reads from MongoDB and the types of values you store into MongoDB, thus keeping
  your MongoDB records sane. As well:

    * utilize and easily write reusable value serializers/deserializers
    * define MongoDB subdocuments

* construct queries to MongoDB based on the fields
* construct MongoDB update operations based on the fields

## More Information

* [Subset 2.x](https://github.com/osinka/subset2)
* [API Reference (scaladoc)](http://osinka.github.io/subset/api/index.html#com.osinka.subset.package)
* [Documentation](http://osinka.github.io/subset/Subset.html)

## Using

### Imports

**Subset** declares most *implicits* via `package object`, so it's a good idea
to import the whole package:

```scala
import com.osinka.subset._
import SmartValues._
```

> More on the choices in the [documentation](http://osinka.github.io/subset/Getting+Started.html)

### SBT Configuration

Depending on *simple* or *scala* configration:

```scala
libraryDependencies += "com.osinka.subset" %% "subset" % "1.1.0"
```

or

```scala
lazy val root = Project(....) dependsOn(subset)

lazy val subset = "com.osinka.subset" %% "subset" % "1.1.0"
```

### Maven Configuration

Dependency:

```xml
<dependency>
  <groupId>com.osinka.subset</groupId>
  <artifactId>subset_2.10.0</artifactId>
  <version>1.1.0</version>
</dependency>
```

No need in repository declaration, **Subset** is in Maven Central.
