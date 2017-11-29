package model

import slick.lifted.Tag
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

case class Users(ID_user: Option[Long], login: String, password: String)

class UsersTable(tag: Tag) extends Table[Users](tag, "Users") {
  val ID_user = column[Long]("ID_user", O.PrimaryKey, O.AutoInc)
  val login = column[String]("login")
  val password = column[String]("password")

  def * = (ID_user.?, login, password) <> (Users.apply _ tupled, Users.unapply)
}

object UsersTable {
  val table = TableQuery[UsersTable]
}

class UsersRepository(db: Database) {
  val UsersTableQuery = TableQuery[UsersTable]

  def create(users: Users): Future[Users] =
    db.run(UsersTableQuery returning UsersTableQuery += users)

  def addSeq(users: Seq[Users]): Future[Option[Int]] =
    db.run(UsersTableQuery ++= users)

  def update(users: Users): Future[Int] =
    db.run(UsersTableQuery.filter(_.ID_user === users.ID_user).update(users))

  def delete(usersId: Long): Future[Int] =
    db.run(UsersTableQuery.filter(_.ID_user === usersId).delete)

  def getById(usersId: Long): Future[Option[Users]] =
    db.run(UsersTableQuery.filter(_.ID_user === usersId).result.headOption)
}
