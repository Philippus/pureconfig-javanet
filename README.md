# pureconfig-javanet

[![build](https://github.com/Philippus/pureconfig-javanet/workflows/build/badge.svg)](https://github.com/Philippus/pureconfig-javanet/actions/workflows/scala.yml?query=workflow%3Abuild+branch%3Amain)
[![codecov](https://codecov.io/gh/Philippus/pureconfig-javanet/branch/main/graph/badge.svg)](https://codecov.io/gh/Philippus/pureconfig-javanet)
![Current Version](https://img.shields.io/badge/version-0.0.2-brightgreen.svg?style=flat "0.0.2")
[![license](https://img.shields.io/badge/license-MPL%202.0-blue.svg?style=flat "MPL 2.0")](LICENSE)

pureconfig-javanet is a [PureConfig](https://pureconfig.github.io/docs/) module that supplies readers and writers that
turn a `host:port` pair into a `InetSocketAddress` and turn a list of `host:port` pairs, separated by a comma, into a
`Seq[InetSocketAddress]`. It supports IPv4 and IPv6 addresses and host names in the following shapes:

```
127.0.0.1:80
2001:db8::1:80
[2001:db8::1]:80
127.0.0.1:80, localhost:80
```

To stay pure the `InetSocketAddress` is constructed using the `createUnresolved`-method, and thus will be flagged as
_unresolved_, see https://docs.oracle.com/javase/8/docs/api/java/net/InetSocketAddress.html#createUnresolved-java.lang.String-int-.

## Installation

pureconfig-javanet is published for Scala 2.13. To start using it add the following to your `build.sbt`:

```
libraryDependencies += "nl.gn0s1s" %% "pureconfig-javanet" % "0.0.2"
```

PureConfig itself also needs to be added as a dependency to your project.

## Example usage

```scala
  import java.net.InetSocketAddress

  import com.typesafe.config.ConfigFactory.parseString
  import nl.gn0s1s.pureconfig.module.javanet._
  import pureconfig.generic.auto._
  import pureconfig.syntax._

  case class Config(host: InetSocketAddress)

  val conf = parseString(""""host": "127.0.0.1:65535"""") // val conf: com.typesafe.config.Config = Config(SimpleConfigObject({"host":"127.0.0.1:65535"}))

  conf.to[Config] // val res0: pureconfig.ConfigReader.Result[Config] = Right(Config(127.0.0.1:65535))
```

## License
The code is available under the [Mozilla Public License, version 2.0](LICENSE).
