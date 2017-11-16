package model

import slick.lifted.Tag
import slick.jdbc.PostgresProfile.api._
import java.sql.Timestamp
import scala.concurrent.Future

case class Pass_in_trip(trip_no: Option[Long], date: Timestamp, ID_psg: Long, place: String)

class Pass_in_tripTable(tag: Tag) extends Table[Pass_in_trip](tag, "Pass_in_trip") {
  val trip_no = column[Long]("trip_no", O.AutoInc)
  val date = column[Timestamp]("[date]")
  val ID_psg = column[Long]("ID_psg")
  val place = column[String]("place")
  val psgFk = foreignKey("ID_psg_fk", ID_psg, TableQuery[PassengerTable])(_.id)
  val tripFk = foreignKey("trip_no_fk", trip_no, TableQuery[TripTable])(_.trip_no)
  val dt =primaryKey("dt", (trip_no,date,ID_psg))

  def * = (trip_no.?, date, ID_psg, place) <> (Pass_in_trip.apply _ tupled, Pass_in_trip.unapply)
}

object Pass_in_tripTable {
  val table = TableQuery[Pass_in_tripTable]
}

class Pass_in_tripRepository(db: Database) {
  val Pass_in_tripTableQuery = TableQuery[Pass_in_tripTable]

  def create(pass: Pass_in_trip): Future[Pass_in_trip] =
    db.run(Pass_in_tripTable.table returning Pass_in_tripTable.table += pass)

  def addSeq(pass: Seq[Pass_in_trip]): Future[Option[Int]] =
    db.run(Pass_in_tripTableQuery.forceInsertAll(pass))

  def update(pass: Pass_in_trip): Future[Int] =
    db.run(Pass_in_tripTableQuery.filter(_.trip_no === pass.trip_no).update(pass))

  def delete(passId: Long): Future[Int] =
    db.run(Pass_in_tripTableQuery.filter(_.trip_no === passId).delete)

  def getById(passId: Long): Future[Option[Pass_in_trip]] =
    db.run(Pass_in_tripTableQuery.filter(_.trip_no === passId).result.headOption)
}
