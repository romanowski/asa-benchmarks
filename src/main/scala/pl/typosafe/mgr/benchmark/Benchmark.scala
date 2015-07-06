package pl.typosafe.mgr.benchmark

import akka.actor.ActorSystem
import akka.util.Timeout

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

class Benchmark(name: String, timeout: Long = 100L) {
  protected val system = ActorSystem(name.replace(' ', '-'))

  protected val defaultTestTime = 10000L

  protected implicit val timeoutDuration = Timeout(timeout millis)


  def reportResult(result: String, success: Boolean): Unit = {
    val successMgs = if(success) "ends" else "failed"
    println(s"Benchmark: $name $successMgs with:\n$result")
  }

  def doTest(doBenchmark: => Future[String]): Unit = {
    try {
      reportResult(Await.result(doBenchmark, timeout millis), true)
    } catch {
      case exception: Throwable =>
        reportResult(exception.getMessage, false)
    }
    system.shutdown()
  }

}
