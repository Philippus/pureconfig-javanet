package nl.gn0s1s.pureconfig.module

import java.net.InetSocketAddress
import org.apache.commons.validator.routines.InetAddressValidator
import pureconfig.ConfigConvert
import pureconfig.error.{CannotConvert, FailureReason}

package object javanet {
  private val validHostNameRegex =
    """^(([a-zA-Z\d]|[a-zA-Z\d][a-zA-Z\d\-]*[a-zA-Z\d])\.)*([a-zA-Z\d]|[a-zA-Z\d][a-zA-Z\d\-]*[a-zA-Z\d])$""".r

  private def parseHostAndPort(unparsedHostAndPort: String): Either[FailureReason, InetSocketAddress] = {
    val lastOccurrenceOfColon = unparsedHostAndPort.lastIndexOf(':')
    lastOccurrenceOfColon match {
      case -1 =>
        Left(CannotConvert(unparsedHostAndPort, "InetSocketAddress", "no port defined"))
      case 0  =>
        Left(CannotConvert(unparsedHostAndPort, "InetSocketAddress", "no host defined"))
      case n  =>
        val host = {
          val candidate = unparsedHostAndPort.take(n).trim
          if (candidate.startsWith("[") && candidate.endsWith("]"))
            candidate.substring(1, candidate.length - 1)
          else
            candidate
        }
        val port = unparsedHostAndPort.drop(n + 1).trim
        if (port.isEmpty)
          Left(CannotConvert(unparsedHostAndPort, "InetSocketAddress", s"no port defined"))
        else if (port.toIntOption.isEmpty)
          Left(CannotConvert(unparsedHostAndPort, "InetSocketAddress", s"port is not a number:$port"))
        else if (port.toIntOption.exists(i => i < 0 || i > 0xffff))
          Left(CannotConvert(unparsedHostAndPort, "InetSocketAddress", s"port out of range:$port"))
        else if (
          InetAddressValidator.getInstance().isValidInet4Address(host) ||
          InetAddressValidator.getInstance().isValidInet6Address(host) ||
          validHostNameRegex.matches(host)
        )
          Right(InetSocketAddress.createUnresolved(host, port.toInt))
        else
          Left(CannotConvert(unparsedHostAndPort, "InetSocketAddress", "Cannot parse string into host and port"))
    }
  }

  implicit val inetSocketAddressConfigConvert: ConfigConvert[InetSocketAddress] =
    ConfigConvert.viaNonEmptyString(s => parseHostAndPort(s), address => s"${address.getHostString}:${address.getPort}")

  implicit val inetSocketAddressSeqConfigConvert: ConfigConvert[Seq[InetSocketAddress]] =
    ConfigConvert.viaNonEmptyString(
      s =>
        s.split(", *")
          .partitionMap(parseHostAndPort) match {
          case (errors, ok) if errors.isEmpty =>
            Right(ok.toSeq)
          case _                              =>
            Left(CannotConvert(s, "Seq[InetSocketAddress]", "Cannot parse string into hosts and ports"))
        },
      _.map { address =>
        s"${address.getHostString}:${address.getPort}"
      }.mkString(",")
    )
}
