package model

import slick.lifted.Tag
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

case class Company(id: Option[Long], name: String)

class CompanyTable(tag: Tag) extends Table[Company](tag, "Company") {
  val id = column[Long]("ID_comp", O.PrimaryKey, O.AutoInc)
  val name = column[String]("name")

  def * = (id.?, name) <> (Company.apply _ tupled, Company.unapply)
}

object CompanyTable {
  val table = TableQuery[CompanyTable]
}

class CompanyRepository(db: Database) {
  val companyTableQuery = TableQuery[CompanyTable]

  def create(company: Company): Future[Company] =
    db.run(companyTableQuery returning companyTableQuery += company)

  def addSeq(company: Seq[Company]): Future[Option[Int]] =
    db.run(companyTableQuery ++= company)

  def update(company: Company): Future[Int] =
    db.run(companyTableQuery.filter(_.id === company.id).update(company))

  def delete(companyId: Long): Future[Int] =
    db.run(companyTableQuery.filter(_.id === companyId).delete)

  def getById(companyId: Long): Future[Option[Company]] =
    db.run(companyTableQuery.filter(_.id === companyId).result.headOption)
}
