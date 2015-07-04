package pl.typosafe.mgr.benchmark

import akka.actor.{Props, ActorSystem, Actor}
import akka.pattern.ask
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


object SimplePing

object StopSimplePing

class SimplePingSingleThread extends Actor {
  private var count = 0L

  private var stopped = false

  override def receive: Receive = {
    case SimplePing =>
      if (!stopped) {
        count += 1
        self ! SimplePing
      }
    case StopSimplePing =>
      sender ! count
  }
}

object SimplePingSingleThread extends Benchmark("SimplePingSingleThread") with App {

  doTest {
    val simpleActor = system.actorOf(Props[SimplePingSingleThread])
    simpleActor ! SimplePing

    Thread.sleep(defaultTestTime)

    (simpleActor ? StopSimplePing).map {
      case amout: Long => amout.toString
    }
  }
}

class SimplePingMultipleThreads(actorCount: Int) extends Benchmark(s"SimplePing with ${actorCount} thread") with App {

  doTest {
    val sleepTime = 10000L

    val actors = (1 to actorCount).map { nr => system.actorOf(Props[SimplePingSingleThread], name = s"actor$nr")}
    actors.foreach(_ ! SimplePing)

    Thread.sleep(defaultTestTime)

    val resultsPerActor = actors.map(_ ? StopSimplePing map (_.asInstanceOf[Long]))
    val result = Future.sequence(resultsPerActor)
    result.map { counts =>
      s"Total: ${counts.sum}\n${counts.mkString("\n")}"
    }
  }
}

//include hyperthreading
object SimplePingProcesorCountThreads extends SimplePingMultipleThreads(Runtime.getRuntime().availableProcessors() * 2)

object SimplePing16Threads extends SimplePingMultipleThreads(16)

object SimplePing32Threads extends SimplePingMultipleThreads(32)

object SimplePing124Threads extends SimplePingMultipleThreads(124)