package `com.krud3`

import java.time.LocalDate
import java.util.concurrent.Executors
import scala.util.{Try, Success, Failure}
import scala.concurrent.{ExecutionContext, Future}

object PrivateExecutionContext {
  val executor = Executors.newFixedThreadPool(4)
  implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executor)
}

object Main{
    import slick.jdbc.PostgresProfile.api._
    import PrivateExecutionContext._ 
    import scala.concurrent.duration._
    import scala.concurrent.Await
    val rate = Rate("AAA1", LocalDate.of(2020, 1, 1), 100.0)

    def demoInsertRate(): Unit = {
        val queryDeScription = SlickTables.rateTable += rate
        val futureId: Future[Int] = Connection.db.run(queryDeScription)
        futureId.onComplete {
            case Success(newRateId) => println(s"Inserted rate with id and Date: $newRateId")
            case Failure(ex) => println(s"Failed to insert rate: $ex")
        }

        Try(Await.result(futureId, Duration.Inf)) match {
            case Success(newRateId) => println(s"Successfully inserted rate with id: $newRateId")
            case Failure(ex) => println(s"Failed to insert rate: $ex")
        }
    }

    def main(args: Array[String]): Unit = {
        demoInsertRate()
    }
}