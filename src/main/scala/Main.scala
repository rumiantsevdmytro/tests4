import model._
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.io.StdIn._

object Main extends App{
  val db = Database.forURL(
    "jdbc:postgresql://ec2-23-23-78-213.compute-1.amazonaws.com/d1hm2easc2k65d?sslmode=require&user=odcavfwbxvvgkd&password=e5b38fdb685517bb5bf0f46d80262232ef8cbbaab5e03cc6606e169e0066498a"
  )

  val usersRepository = new UsersRepository(db)
  val tasksRepository = new TasksRepository(db)
  waiter


  def waiter: Unit = {
    val command = readLine().split(" ").toList
    val n = command.length
    initial(command,n)
  }
  def waiter2(code: Long): Unit = {
    val command = readLine().split("\"").toList
    val n = command.length
    manager(command, n, code)
  }

  def initial(command:List[String], n: Int):Unit= n match {
    case 1 => command.head match
    { case "--help" => println("login username password")
        waiter
      case _ =>  println("Please, enter a correct command!")
        waiter
    }
    case 3 => command.head match
      { case "login" => val login = command(1)
                        val password = command(2)
                        approve(login, password)
        case _ =>  println("Please, enter a correct command!")
                   waiter
    }
    case _ => println("Please, enter a correct command!")
              waiter
  }

  def manager(command:List[String], n: Int, code:Long):Unit = n match {
    case 1 => command.head match
      {
      case "--help" => println("All commands take only one argument by call")
        println("Arguments must entered in double quotes")
        println("Each task must be unique")
        println("Commands:")
        println("--add \"<task>\"")
        println("--show \"<all|open|closed>\"")
        println("--delete \"<task>\"")
        println("--close \"<task>\"")
        println("--help")
        println("--exit")
        waiter2(code)
      case "--exit"=> println("Bye!")
      case _ =>   println("Please, enter a correct command!")
        waiter2(code)
    }
    case 2 => command.head.replaceAll("\\s", "") match
      {
      case "--add" => val task=command(1)
                      add(task, code)
                      waiter2(code)
      case "--show"=> val status=command(1)
                      show(status, code)
                      waiter2(code)
      case "--delete"=> val task=command(1)
                        delete(task,code)
                        waiter2(code)
      case "--close" => val task=command(1)
                        close(task,code)
                        waiter2(code)
      case _ =>  println("Please, enter a correct command!")
        waiter2(code)
    }
    case _ => println("Please, enter a correct command!")
      waiter2(code)

  }


  def approve(login: String, password: String): Unit = {
    val correct_pass=exec(UsersTable.table.withFilter(_.login===login).map(_.password).result)
    if (correct_pass.isEmpty) {println("Username or password is incorrect")
      waiter}
    else if (password==correct_pass.head) {
      println("Authentication is successful!")
      val code=exec(UsersTable.table.withFilter(_.login===login).map(_.ID_user).result).head
      waiter2(code)
    }
    else {println("Username or password is incorrect")
    waiter}
  }

  def add(command:String, code:Long): Unit ={
    val taskId = exec(TasksTable.table.withFilter(_.ID_user === code).withFilter(_.task === command).map(_.task_no).result)
    if (taskId.isEmpty) {
    val task=Tasks( None, command, "open", code)
    Await.result(tasksRepository.create(task), Duration.Inf)
    println("Task is added")}
    else println("This task is already created")
  }
  def show(status: String, code:Long): Unit ={
    if (StatusCheck(status, code).isEmpty) println("There are not tasks")
    else
    for{
      x<-StatusCheck(status, code)
    }yield println(x)
    def StatusCheck(status: String, code: Long) = status match{
      case "all" => exec(TasksTable.table.withFilter(_.ID_user===code).map(f=>(f.task, f.status)).result)
      case _ => exec(TasksTable.table.withFilter(_.ID_user===code).withFilter(_.status===status).map(_.task).result)
    }
  }
  def delete(task:String, code:Long):Unit ={
    val taskId = exec(TasksTable.table.withFilter(_.ID_user===code).withFilter(_.task===task).map(_.task_no).result)
    if (taskId.isEmpty) println("No such task")
    else {
      Await.result(tasksRepository.delete(taskId.head), Duration.Inf)
      println("Task is deleted")
    }
  }
  def close(task:String, code:Long):Unit ={
    val taskStatus = TasksTable.table.withFilter(_.ID_user===code).withFilter(_.task===task).map(_.status)
    if (exec(taskStatus.result).isEmpty) println("No such task")
    else {
      exec(taskStatus.update("closed"))
      println("Task is closed")
    }
  }





  def init(): Unit = {
    Await.result(db.run(UsersTable.table.schema.create), Duration.Inf)
    Await.result(db.run(TasksTable.table.schema.create), Duration.Inf)

  }

  val users:Seq[Users] =Seq(Users(Some(1),"data", "data"),
    Users(Some(2),"root", "root"))
  def databaseFill(users: Seq[Users]):Unit ={
    Await.result(usersRepository.addSeq(users), Duration.Inf)
  }
  //databaseFill(users)

  def exec[T](action: DBIO[T]):T = Await.result(db.run(action), Duration.Inf)


}
