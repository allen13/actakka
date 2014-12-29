package loadTest

import java.net.InetSocketAddress

import akka.actor.{Actor, ActorRef}
import akka.io.{IO, Tcp}
import akka.util.ByteString


class TCPWorker(remote: InetSocketAddress) extends Actor{
  import context.system;

  def tcp: ActorRef = IO(Tcp)
  tcp ! Tcp.Connect(remote)

  def ready(): Receive = {
    case msg: String =>
      tcp ! Tcp.Write(ByteString(msg))
  }

  def receive = {
    case Tcp.Connected =>
      context.become(ready())
  }

}
