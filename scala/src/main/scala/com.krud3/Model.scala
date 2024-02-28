package `com.krud3`

import java.time.LocalDate

case class Rate(hotel_id: String, date: LocalDate, rate: Double)

object SlickTables{
    import slick.jdbc.PostgresProfile.api._

    class RateTable(tag: Tag) extends Table[Rate](tag, Some("rate") /* <- schema name */, "room_type_rate"){
        def hotel_id = column[String]("hotel_id")
        def date = column[LocalDate]("date")
        def rate = column[Double]("rate")
        
        // mapping function to the case class
        override def * = (hotel_id, date, rate) <> (Rate.tupled, Rate.unapply)
    }
    // API ENTRY POINT

    lazy val rateTable = TableQuery[RateTable]
}