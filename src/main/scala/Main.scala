import model.{Pass_in_trip, Trip, _}
import slick.jdbc.PostgresProfile.api._
import java.sql.Timestamp._
import scala.concurrent.Await
import scala.concurrent.duration._

object Main {
  val db = Database.forURL(
    "jdbc:postgresql://127.0.0.1/airport?user=postgres&password=root"
  )

  val tripRepository = new TripRepository(db)
  val pass_in_tripRepository = new Pass_in_tripRepository(db)
  val passengerRepository = new PassengerRepository(db)
  val companyRepository = new CompanyRepository(db)

  val answer1 = Pass_in_tripTable.table.
    join(PassengerTable.table).on(_.ID_psg === _.id).
    groupBy{case (trip,pass)=>(trip.ID_psg, trip.place, pass.name)}.
    map {case(trip,num)=>(trip._3,num.length)}.
    filter {case(trip, num)=>num>1}.
    map {case(trip,num)=>trip}


  val answer2 = TripTable.table.
    groupBy{trip=>(trip.town_from, trip.town_to)}.
    map {case(trip,num)=>(trip,num.length)}.
    sortBy{case(trip,num)=>num.desc}.
    groupBy{case(trip,num)=>num}.
    map {case(trip,num)=>(trip,num.length)}.
    take (1).
    map {case(trip,num)=>num}

  //val answer3=

  val max4 =  exec(Pass_in_tripTable.table.
    join(TripTable.table).on(_.trip_no === _.trip_no).
    join(PassengerTable.table).on{ case((pass,trip),psg) => pass.ID_psg===psg.id}.
    groupBy{case ((pass,trip),psg)=>(pass.ID_psg, trip.ID_comp, psg.name)}.
    map {case(pass,num)=>(num.length)}.result).toList.max

  val answer4 = Pass_in_tripTable.table.
    join(TripTable.table).on(_.trip_no === _.trip_no).
    join(PassengerTable.table).on{ case((pass,trip),psg) => pass.ID_psg===psg.id}.
    groupBy{case ((pass,trip),psg)=>(pass.ID_psg, trip.ID_comp, psg.name)}.
    map {case(pass,num)=>(pass._3,num.length)}.
    filter {case(pass,num)=>num===max4}

  val answer5 = TripTable.table.
    filter(_.town_from==="Rostov").
    join(Pass_in_tripTable.table).on(_.trip_no === _.trip_no).
    map{case (trip, pass)=>(pass.trip_no, pass.date)}.distinct.
    groupBy{case (pass)=>pass}.
    map {case(pass,num)=>(num.length, pass._2)}.
    sortBy(_._2)

  //val answer6 =
  //val answer7 =
  val answer8 = Pass_in_tripTable.table.
    join(TripTable.table).on(_.trip_no === _.trip_no).
    join(PassengerTable.table).on{ case((pass,trip),psg) => pass.ID_psg===psg.id}.
    join(CompanyTable.table).on{ case(((pass,trip),psg), cmp) => cmp.id===trip.ID_comp}.
    groupBy{case (((pass,trip),psg),cmp)=>(pass.ID_psg, cmp.name, psg.name)}.
    map {case(pass,num)=>(pass._3,num.length, pass._2)}.
    filter (_._2===max4)

  //val answer9 =


  val flight10 = Pass_in_tripTable.table.
    join(TripTable.table).on(_.trip_no === _.trip_no).
    map{case(pass,trip)=>(pass.trip_no, pass.date, trip.ID_comp)}.distinct.
    groupBy{case(pass)=>pass._3}.
    map{case(pass,num)=>(pass, num.length)}
  val planes10 = Pass_in_tripTable.table.
    join(TripTable.table).on(_.trip_no === _.trip_no).
    map{case(pass,trip)=>(trip.ID_comp, trip.plane)}.distinct.
    groupBy{case(pass)=>(pass._1)}.
    map{case(pass,num)=>(pass, num.length)}
  val diffPsg10 = Pass_in_tripTable.table.
    join(TripTable.table).on(_.trip_no === _.trip_no).
    map{case(pass,trip)=>(trip.ID_comp, pass.ID_psg)}.distinct.
    groupBy{case(pass)=>(pass._1)}.
    map{case(pass,num)=>(pass, num.length)}
  val totPsg10 = Pass_in_tripTable.table.
    join(TripTable.table).on(_.trip_no === _.trip_no).
    map{case(pass,trip)=>(trip.ID_comp, pass.ID_psg)}.
    groupBy{case(pass)=>(pass._1)}.
    map{case(pass,num)=>(pass, num.length)}

  val answer10 = CompanyTable.table.
    join(flight10).on{case(cmp,pass)=>(cmp.id===pass._1)}.
    map{case(cmp,pass)=>(cmp.id, cmp.name, pass._2)}.
    join(planes10).on{case(cmp,pass)=>(cmp._1===pass._1)}.
    map{case(cmp,pass)=>(cmp._1, cmp._2, cmp._3, pass._2)}.
    join(diffPsg10).on{case(cmp,pass)=>(cmp._1===pass._1)}.
    map{case(cmp,pass)=>(cmp._1, cmp._2, cmp._3, cmp._4, pass._2)}.
    join(totPsg10).on{case(cmp,pass)=>(cmp._1===pass._1)}.
    map{case(cmp,pass)=>(cmp._2, cmp._3, cmp._4, cmp._5, pass._2)}

  val answer15 = Pass_in_tripTable.table.
    join(PassengerTable.table).on(_.ID_psg === _.id).
    groupBy{case (trip,pass)=>(trip.ID_psg, trip.place, pass.name)}.
    map {case(trip,num)=>(trip._3,num.length)}.
    filter {case(trip, num)=>num>1}





  def main(args: Array[String]): Unit = {
    //init()
    //databaseFill(companies,passengers, passenger37,trips, passes)
    println(exec(answer1.result))
    println(exec(answer2.result))
    println(exec(answer4.result))
    println(exec(answer5.result))
    println(exec(answer8.result))
    println(exec(answer10.result))
    println(exec(answer15.result))
  }

  def init(): Unit = {
    Await.result(db.run(PassengerTable.table.schema.create), Duration.Inf)
    Await.result(db.run(CompanyTable.table.schema.create), Duration.Inf)
    Await.result(db.run(TripTable.table.schema.create), Duration.Inf)
    Await.result(db.run(Pass_in_tripTable.table.schema.create), Duration.Inf)
  }


  val companies: Seq[Company] = Seq(Company(Some(1),"Don_avia"),
    Company(Some(2),"Aeroflot"),
    Company(Some(3),"Dale_avia"),
    Company(Some(4),"air_France"),
    Company(Some(5),"British_AW"))

  val passengers: Seq[Passenger] = Seq(Passenger(Some(1),"Bruce Willis"),
    Passenger(Some(2),"George Clooney"),
    Passenger(Some(3),"Kevin Costner"),
    Passenger(Some(4),"Donald Sutherland"),
    Passenger(Some(5),"Jennifer Lopez"),
    Passenger(Some(6),"Ray Liotta"),
    Passenger(Some(7),"Samuel L. Jackson"),
    Passenger(Some(8),"Nikole Kidman"),
    Passenger(Some(9),"Alan Rickman"),
    Passenger(Some(10),"Kurt Russell"),
    Passenger(Some(11),"Harrison Ford"),
    Passenger(Some(12),"Russell Crowe"),
    Passenger(Some(13),"Steve Martin"),
    Passenger(Some(14),"Michael Caine"),
    Passenger(Some(15),"Angelina Jolie"),
    Passenger(Some(16),"Mel Gibson"),
    Passenger(Some(17),"Michael Douglas"),
    Passenger(Some(18),"John Travolta"),
    Passenger(Some(19),"Sylvester Stallone"),
    Passenger(Some(20),"Tommy Lee Jones"),
    Passenger(Some(21),"Catherine Zeta-Jones"),
    Passenger(Some(22),"Antonio Banderas"),
    Passenger(Some(23),"Kim Basinger"),
    Passenger(Some(24),"Sam Neill"),
    Passenger(Some(25),"Gary Oldman"),
    Passenger(Some(26),"Clint Eastwood"),
    Passenger(Some(27),"Brad Pitt"),
    Passenger(Some(28),"Johnny Depp"),
    Passenger(Some(29),"Pierce Brosnan"),
    Passenger(Some(30),"Sean Connery"),
    Passenger(Some(31),"Bruce Willis"))
  val passenger37 = Passenger(Some(37),"Mullah Omar")

  val trips:Seq[Trip] = Seq(Trip(Some(1100),4,"Boeing","Rostov","Paris",valueOf("1900-01-01 14:30:00.000"),valueOf("1900-01-01 17:50:00.000")),
                            Trip(Some(1101),4,"Boeing","Paris","Rostov",valueOf("1900-01-01 08:12:00.000"),valueOf("1900-01-01 11:45:00.000")),
                            Trip(Some(1123),3,"TU-154","Rostov","Vladivostok",valueOf("1900-01-01 16:20:00.000"),valueOf("1900-01-01 03:40:00.000")),
                            Trip(Some(1124),3,"TU-154","Vladivostok","Rostov",valueOf("1900-01-01 09:00:00.000"),valueOf("1900-01-01 19:50:00.000")),
                            Trip(Some(1145),2,"IL-86","Moscow","Rostov",valueOf("1900-01-01 09:35:00.000"),valueOf("1900-01-01 11:23:00.000")),
                            Trip(Some(1146),2,"IL-86","Rostov","Moscow",valueOf("1900-01-01 17:55:00.000"),valueOf("1900-01-01 20:01:00.000")),
                            Trip(Some(1181),1,"TU-134","Rostov","Moscow",valueOf("1900-01-01 06:12:00.000"),valueOf("1900-01-01 08:01:00.000")),
                            Trip(Some(1182),1,"TU-134","Moscow","Rostov",valueOf("1900-01-01 12:35:00.000"),valueOf("1900-01-01 14:30:00.000")),
                            Trip(Some(1187),1,"TU-134","Rostov","Moscow",valueOf("1900-01-01 15:42:00.000"),valueOf("1900-01-01 17:39:00.000")),
                            Trip(Some(1188),1,"TU-134","Moscow","Rostov",valueOf("1900-01-01 22:50:00.000"),valueOf("1900-01-01 00:48:00.000")),
                            Trip(Some(1195),1,"TU-154","Rostov","Moscow",valueOf("1900-01-01 23:30:00.000"),valueOf("1900-01-01 01:11:00.000")),
                            Trip(Some(1196),1,"TU-154","Moscow","Rostov",valueOf("1900-01-01 04:00:00.000"),valueOf("1900-01-01 05:45:00.000")),
                            Trip(Some(7771),5,"Boeing","London","Singapore",valueOf("1900-01-01 01:00:00.000"),valueOf("1900-01-01 11:00:00.000")),
                            Trip(Some(7772),5,"Boeing","Singapore","London",valueOf("1900-01-01 12:00:00.000"),valueOf("1900-01-01 02:00:00.000")),
                            Trip(Some(7773),5,"Boeing","London","Singapore",valueOf("1900-01-01 03:00:00.000"),valueOf("1900-01-01 13:00:00.000")),
                            Trip(Some(7774),5,"Boeing","Singapore","London",valueOf("1900-01-01 14:00:00.000"),valueOf("1900-01-01 06:00:00.000")),
                            Trip(Some(7775),5,"Boeing","London","Singapore",valueOf("1900-01-01 09:00:00.000"),valueOf("1900-01-01 20:00:00.000")),
                            Trip(Some(7776),5,"Boeing","Singapore","London",valueOf("1900-01-01 18:00:00.000"),valueOf("1900-01-01 08:00:00.000")),
                            Trip(Some(7777),5,"Boeing","London","Singapore",valueOf("1900-01-01 18:00:00.000"),valueOf("1900-01-01 06:00:00.000")),
                            Trip(Some(7778),5,"Boeing","Singapore","London",valueOf("1900-01-01 22:00:00.000"),valueOf("1900-01-01 12:00:00.000")),
                            Trip(Some(8881),5,"Boeing","London","Paris",valueOf("1900-01-01 03:00:00.000"),valueOf("1900-01-01 04:00:00.000")),
                            Trip(Some(8882),5,"Boeing","Paris","London",valueOf("1900-01-01 22:00:00.000"),valueOf("1900-01-01 23:00:00.000")))

  val passes:Seq[Pass_in_trip] =Seq(Pass_in_trip(Some(1100),valueOf("2003-04-29 00:00:00.000"),1,"1a"),
                                    Pass_in_trip(Some(1123),valueOf("2003-04-05 00:00:00.000"),3,"2a"),
                                    Pass_in_trip(Some(1123),valueOf("2003-04-08 00:00:00.000"),1,"4c"),
                                    Pass_in_trip(Some(1123),valueOf("2003-04-08 00:00:00.000"),6,"4b"),
                                    Pass_in_trip(Some(1124),valueOf("2003-04-02 00:00:00.000"),2,"2d"),
                                    Pass_in_trip(Some(1145),valueOf("2003-04-05 00:00:00.000"),3,"2c"),
                                    Pass_in_trip(Some(1181),valueOf("2003-04-01 00:00:00.000"),1,"1a"),
                                    Pass_in_trip(Some(1181),valueOf("2003-04-01 00:00:00.000"),6,"1b"),
                                    Pass_in_trip(Some(1181),valueOf("2003-04-01 00:00:00.000"),8,"3c"),
                                    Pass_in_trip(Some(1181),valueOf("2003-04-13 00:00:00.000"),5,"1b"),
                                    Pass_in_trip(Some(1182),valueOf("2003-04-13 00:00:00.000"),5,"4b"),
                                    Pass_in_trip(Some(1187),valueOf("2003-04-14 00:00:00.000"),8,"3a"),
                                    Pass_in_trip(Some(1188),valueOf("2003-04-01 00:00:00.000"),8,"3a"),
                                    Pass_in_trip(Some(1182),valueOf("2003-04-13 00:00:00.000"),9,"6d"),
                                    Pass_in_trip(Some(1145),valueOf("2003-04-25 00:00:00.000"),5,"1d"),
                                    Pass_in_trip(Some(1187),valueOf("2003-04-14 00:00:00.000"),10,"3d"),
                                    Pass_in_trip(Some(8882),valueOf("2005-11-06 00:00:00.000"),37,"1a"),
                                    Pass_in_trip(Some(7771),valueOf("2005-11-07 00:00:00.000"),37,"1c"),
                                    Pass_in_trip(Some(7772),valueOf("2005-11-07 00:00:00.000"),37,"1a"),
                                    Pass_in_trip(Some(8881),valueOf("2005-11-08 00:00:00.000"),37,"1d"),
                                    Pass_in_trip(Some(7778),valueOf("2005-11-05 00:00:00.000"),10,"2a"),
                                    Pass_in_trip(Some(7772),valueOf("2005-11-29 00:00:00.000"),10,"3a"),
                                    Pass_in_trip(Some(7771),valueOf("2005-11-04 00:00:00.000"),11,"4a"),
                                    Pass_in_trip(Some(7771),valueOf("2005-11-07 00:00:00.000"),11,"1b"),
                                    Pass_in_trip(Some(7771),valueOf("2005-11-09 00:00:00.000"),11,"5a"),
                                    Pass_in_trip(Some(7772),valueOf("2005-11-07 00:00:00.000"),12,"1d"),
                                    Pass_in_trip(Some(7773),valueOf("2005-11-07 00:00:00.000"),13,"2d"),
                                    Pass_in_trip(Some(7772),valueOf("2005-11-29 00:00:00.000"),13,"1b"),
                                    Pass_in_trip(Some(8882),valueOf("2005-11-13 00:00:00.000"),14,"3d"),
                                    Pass_in_trip(Some(7771),valueOf("2005-11-14 00:00:00.000"),14,"4d"),
                                    Pass_in_trip(Some(7771),valueOf("2005-11-16 00:00:00.000"),14,"5d"),
                                    Pass_in_trip(Some(7772),valueOf("2005-11-29 00:00:00.000"),14,"1c")
  )

  def databaseFill(companies: Seq[Company], passengers: Seq[Passenger],
                   passenger: Passenger, trips: Seq[Trip], passes: Seq[Pass_in_trip]): Unit = {
     Await.result(companyRepository.addSeq(companies), Duration.Inf)
    Await.result(passengerRepository.addSeq(passengers), Duration.Inf)
    Await.result(passengerRepository.createForce(passenger), Duration.Inf)
    Await.result(tripRepository.addSeq(trips), Duration.Inf)
    Await.result(pass_in_tripRepository.addSeq(passes), Duration.Inf)
 }

  def exec[T](action: DBIO[T]):T = Await.result(db.run(action), Duration.Inf)


}
