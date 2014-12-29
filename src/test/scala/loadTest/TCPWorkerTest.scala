package loadTest

import java.net.InetSocketAddress

import akka.actor.{ActorSystem}
import akka.io.Tcp
import akka.testkit.{TestProbe, TestActorRef, TestKit}
import akka.util.ByteString
import org.scalatest.{BeforeAndAfterAll, MustMatchers, WordSpecLike}

class TCPWorkerTest(_system: ActorSystem)
  extends TestKit(_system)
  with WordSpecLike
  with MustMatchers
  with BeforeAndAfterAll {
  def this() = this(ActorSystem("TCPWorkerSpec"))

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  val tcpProbe = TestProbe()
  private val SOCKET_ADDRESS: InetSocketAddress = new InetSocketAddress("", 0)


  "TCPWorker" must {
    "should connect and write to tcp socket" in {
      val tcpWorker = TestActorRef(new TCPWorker(SOCKET_ADDRESS){
        override def tcp = tcpProbe.ref
      })
      tcpProbe.expectMsg(Tcp.Connect(SOCKET_ADDRESS))
      tcpProbe.forward(tcpWorker, Tcp.Connected)
      tcpWorker ! "test"
      tcpProbe.expectMsg(Tcp.Write(ByteString("test")))
    }
  }
}
