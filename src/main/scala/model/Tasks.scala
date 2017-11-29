package model

import slick.lifted.Tag
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.Future

case class Tasks(task_no: Option[Long], task: String, status: String, ID_user: Long)

class TasksTable(tag: Tag) extends Table[Tasks](tag, "Tasks") {
  val task_no = column[Long]("task_no", O.AutoInc)
  val task = column[String]("task")
  val status = column[String]("status")
  val ID_user = column[Long]("ID_user")
  val user_fk = foreignKey("user_id_fk", ID_user, TableQuery[UsersTable])(_.ID_user)
  def * = (task_no.?, task, status, ID_user) <> (Tasks.apply _ tupled, Tasks.unapply)
}

object TasksTable {
  val table = TableQuery[TasksTable]
}

class TasksRepository(db: Database) {
  val TasksTableQuery = TableQuery[TasksTable]

  def create(task: Tasks): Future[Tasks] =
    db.run(TasksTable.table returning TasksTable.table += task)

  def addSeq(task: Seq[Tasks]): Future[Option[Int]] =
    db.run(TasksTableQuery.forceInsertAll(task))

  def update(task: Tasks): Future[Int] =
    db.run(TasksTableQuery.filter(_.task_no === task.task_no).update(task))

  def delete(taskId: Long): Future[Int] =
    db.run(TasksTableQuery.filter(_.task_no === taskId).delete)

  def getById(passId: Long): Future[Option[Tasks]] =
    db.run(TasksTableQuery.filter(_.task_no === passId).result.headOption)
}
