package nl.gn0s1s.pureconfig.module.javanet

import java.net.InetSocketAddress

import com.typesafe.config.ConfigFactory.parseString
import com.typesafe.config.ConfigOriginFactory
import pureconfig._
import pureconfig.error.{CannotConvert, ConfigReaderFailures, ConvertFailure}
import pureconfig.generic.auto._
import pureconfig.syntax._

class JavanetSuite extends munit.FunSuite {
  private val expectedConfigOrigin =
    Some(ConfigOriginFactory.newSimple("String").withLineNumber(1))

  test("can read a valid ipv4 address") {
    case class Config(host: InetSocketAddress)

    val conf = parseString(""""host": "127.0.0.1:65535"""")

    assert(conf.to[Config].contains(Config(InetSocketAddress.createUnresolved("127.0.0.1", 65535))))
  }

  test("can read a valid single named address") {
    case class Config(host: InetSocketAddress)

    val conf = parseString(""""host": "abc.def.ghi:65535"""")

    assert(conf.to[Config].contains(Config(InetSocketAddress.createUnresolved("abc.def.ghi", 65535))))
  }

  test("can read a valid ipv6 address") {
    case class Config(host: InetSocketAddress)

    val conf = parseString(""""host": "2001:db8::1:80"""")

    assert(conf.to[Config].contains(Config(InetSocketAddress.createUnresolved("2001:db8::1", 80))))
  }

  test("can read a valid ipv6 address in brackets") {
    case class Config(host: InetSocketAddress)

    val conf = parseString(""""host": "[2001:db8::1]:80"""")

    assert(conf.to[Config].contains(Config(InetSocketAddress.createUnresolved("2001:db8::1", 80))))
  }

  test("validates if a host is defined") {
    case class Config(host: InetSocketAddress)

    val conf = parseString(""""host": ":80"""")

    val expectedErrors = Left(
      ConfigReaderFailures(
        ConvertFailure(CannotConvert(":80", "InetSocketAddress", "no host defined"), expectedConfigOrigin, "host")
      )
    )

    assert(conf.to[Config] == expectedErrors)
  }

  test("validates if a port is defined") {
    case class Config(host: InetSocketAddress)

    val conf = parseString(""""host": "localhost123"""")

    val expectedErrors = Left(
      ConfigReaderFailures(
        ConvertFailure(
          CannotConvert("localhost123", "InetSocketAddress", "no port defined"),
          expectedConfigOrigin,
          "host"
        )
      )
    )

    assert(conf.to[Config] == expectedErrors)
  }

  test("validates if a port is defined when a colon is present") {
    case class Config(host: InetSocketAddress)

    val conf = parseString(""""host": "abc:"""")

    val expectedErrors = Left(
      ConfigReaderFailures(
        ConvertFailure(CannotConvert("abc:", "InetSocketAddress", "no port defined"), expectedConfigOrigin, "host")
      )
    )

    assert(conf.to[Config] == expectedErrors)
  }

  test("validates if a port is a number") {
    case class Config(host: InetSocketAddress)

    val conf = parseString(""""host": "abc:def"""")

    val expectedErrors = Left(
      ConfigReaderFailures(
        ConvertFailure(
          CannotConvert("abc:def", "InetSocketAddress", "port is not a number:def"),
          expectedConfigOrigin,
          "host"
        )
      )
    )

    assert(conf.to[Config] == expectedErrors)
  }

  test("validates if a port is within range") {
    case class Config(host: InetSocketAddress)

    val conf = parseString(""""host": "abc:65536"""")

    val expectedErrors = Left(
      ConfigReaderFailures(
        ConvertFailure(
          CannotConvert("abc:65536", "InetSocketAddress", "port out of range:65536"),
          expectedConfigOrigin,
          "host"
        )
      )
    )

    assert(conf.to[Config] == expectedErrors)
  }

  test("can read back a written address") {
    val address = InetSocketAddress.createUnresolved("localhost", 65535)

    assert(ConfigReader[InetSocketAddress].from(ConfigWriter[InetSocketAddress].to(address)).contains(address))
  }

  test("can read multiple addresses") {
    case class Config(hosts: Seq[InetSocketAddress])

    val conf = parseString("""hosts: "localhost:65535,127.0.0.1:80,localhost:443"""")

    assert(
      conf
        .to[Config]
        .contains(
          Config(
            List(
              InetSocketAddress.createUnresolved("localhost", 65535),
              InetSocketAddress.createUnresolved("127.0.0.1", 80),
              InetSocketAddress.createUnresolved("localhost", 443)
            )
          )
        )
    )
  }

  test("can read a single address as multiple addresses") {
    case class Config(hosts: Seq[InetSocketAddress])

    val conf = parseString("""hosts: "localhost:65535"""")

    assert(conf.to[Config].contains(Config(List(InetSocketAddress.createUnresolved("localhost", 65535)))))
  }

  test("is lenient about whitespace") {
    case class Config(hosts: Seq[InetSocketAddress])

    val conf = parseString("""hosts: "localhost: 65535,127.0.0.1: 80,localhost: 443"""")

    assert(
      conf
        .to[Config]
        .contains(
          Config(
            List(
              InetSocketAddress.createUnresolved("localhost", 65535),
              InetSocketAddress.createUnresolved("127.0.0.1", 80),
              InetSocketAddress.createUnresolved("localhost", 443)
            )
          )
        )
    )
  }

  test("can read back a written Seq[InetSocketAddress]") {
    val addresses = Seq(
      InetSocketAddress.createUnresolved("localhost", 65535),
      InetSocketAddress.createUnresolved("127.0.0.1", 80),
      InetSocketAddress.createUnresolved("localhost", 443)
    )

    assert(
      ConfigReader[Seq[InetSocketAddress]].from(ConfigWriter[Seq[InetSocketAddress]].to(addresses)).contains(addresses)
    )
  }

  test("validates the supplied setting") {
    case class Config(hosts: Seq[InetSocketAddress])

    val conf = parseString("""hosts: "localhost:65535 + localhost:80"""")

    val expectedErrors = Left(
      ConfigReaderFailures(
        ConvertFailure(
          CannotConvert(
            "localhost:65535 + localhost:80",
            "Seq[InetSocketAddress]",
            "Cannot parse string into hosts and ports"
          ),
          expectedConfigOrigin,
          "hosts"
        )
      )
    )

    assert(conf.to[Config] == expectedErrors)
  }
}
