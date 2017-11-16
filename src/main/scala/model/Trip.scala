package model

import slick.lifted.Tag
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future
import scala.concurrent.duration.Duration
import java.sql.Timestamp


case class Trip(trip_no: Option[Long], ID_comp: Long, plane: String,
                town_from: String, town_to: String, time_out: Timestamp, time_in: Timestamp)

class TripTable(tag: Tag) extends Table[Trip](tag, "Trip") {
  val trip_no = column[Long]("trip_no", O.PrimaryKey, O.AutoInc)
  val ID_comp = column[Long]("ID_comp")
  val plane = column[String]("plane")
  val town_from = column[String]("town_from")
  val town_to = column[String]("town_to")
  val time_out = column[Timestamp]("time_out")
  val time_in = column[Timestamp]("time_in")


  val compFk = foreignKey("ID_comp_fk", ID_comp, TableQuery[CompanyTable])(_.id)

  def * = (trip_no.?, ID_comp, plane, town_from, town_to, time_out, time_in) <> (Trip.apply _ tupled, Trip.unapply)
}

object TripTable {
  val table = TableQuery[TripTable]
}

class TripRepository(db: Database) {

   val TripTableQuery = TableQuery[TripTable]

    def create(trip: Trip): Future[Trip] =
      db.run(TripTable.table returning TripTable.table += trip)

    def addSeq(trip: Seq[Trip]): Future[Option[Int]] =
      db.run(TripTableQuery.forceInsertAll(trip))

    def update(trip: Trip): Future[Int] =
      db.run(TripTableQuery.filter(_.trip_no === trip.trip_no).update(trip))

    def delete(tripId: Long): Future[Int] =
      db.run(TripTableQuery.filter(_.trip_no === tripId).delete)

    def getById(tripId: Long): Future[Option[Trip]] =
      db.run(TripTableQuery.filter(_.trip_no === tripId).result.headOption)
  }


