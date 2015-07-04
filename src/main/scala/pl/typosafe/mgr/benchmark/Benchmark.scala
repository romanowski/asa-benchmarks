package pl.typosafe.mgr.benchmark

import akka.actor.ActorSystem
import akka.util.Timeout

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

class Benchmark(name: String, timeout: Long = 100L) {
  protected val system = ActorSystem(name)

  protected val defaultTestTime = 10000L

  protected implicit val timeoutDuration = Timeout(timeout millis)


  def reportResult(result: String): Unit = {
    println(s"Benchmark: $name ends with:\n$result")
  }

  def reportFailure(message: String): Unit = {
    println(s"Benchmark: $name failed due to:\n $message")
  }

  def doTest(doBenchmark: => Future[String]): Unit = {
    try {
      reportResult(Await.result(doBenchmark, timeout millis))
    } catch {
      case exception: Throwable =>
        reportFailure(exception.getMessage)
    }
    system.shutdown()
  }

}
