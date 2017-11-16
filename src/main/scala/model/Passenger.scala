package model

import slick.lifted.Tag
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

case class Passenger(id: Option[Long], name: String)

class PassengerTable(tag: Tag) extends Table[Passenger](tag, "Passenger") {
  val id = column[Long]("ID_psg", O.PrimaryKey, O.AutoInc)
  val name = column[String]("name")

  def * = (id.?, name) <> (Passenger.apply _ tupled, Passenger.unapply)
}

object PassengerTable {
  val table = TableQuery[PassengerTable]
}

class PassengerRepository(db: Database) {
  val passengerTableQuery = TableQuery[PassengerTable]

  def create(passenger: Passenger): Future[Passenger] =
    db.run(passengerTableQuery returning passengerTableQuery += passenger)

  def createForce(passenger: Passenger): Future[Passenger] =
    db.run(passengerTableQuery returning passengerTableQuery forceInsert passenger)

  def addSeq(passenger: Seq[Passenger]): Future[Option[Int]] =
    db.run(passengerTableQuery ++= passenger)

  def update(passenger: Passenger): Future[Int] =
    db.run(passengerTableQuery.filter(_.id === passenger.id).update(passenger))

  def delete(passengerId: Long): Future[Int] =
    db.run(passengerTableQuery.filter(_.id === passengerId).delete)

  def getById(passengerId: Long): Future[Option[Passenger]] =
    db.run(passengerTableQuery.filter(_.id === passengerId).result.headOption)
}
