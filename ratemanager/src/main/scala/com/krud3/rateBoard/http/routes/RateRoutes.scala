package com.krud3.rateBoard.http.routes

import io.circe.generic.auto._
import org.http4s.circe.CirceEntityCodec._

import org.http4s._
import org.http4s.dsl._
import org.http4s.dsl.impl._
import org.http4s.server._
import cats.effect._
import cats.implicits._

import scala.collection.mutable
import com.krud3.rateBoard.domain.rate._
import java.util.UUID
import com.krud3.rateBoard.http.responses._

class RateRoutes[F[_]: Concurrent] extends Http4sDsl[F] {

    // "database"
    private val database = mutable.Map[UUID, Rate]()
    // POST /rates?offset=x&limit=y { filters } // TODO add query params and filters
    private val allRatesRoute: HttpRoutes[F] = HttpRoutes.of[F] {
        case POST -> Root =>
            Ok(database.values)
    }

    // GET /rates/uuid
    private val findRateRoute: HttpRoutes[F] =  HttpRoutes.of[F] {
        case GET -> Root / UUIDVar(hotel_id) =>
            database.get(hotel_id) match {
                case Some(rate) => Ok(rate)
                case None       => NotFound(FailureResponse(s"Rate $hotel_id not found"))
            }
    }

    // POST /rates/create { rateInfo }
    private def createRate(): F[Rate] =
        Rate(
            hotel_id = UUID.randomUUID(),
            date = System.currentTimeMillis(),
            rate = 100.0
        ).pure[F]

    private val createRateRoute: HttpRoutes[F] = HttpRoutes.of[F] {
        case POST -> Root / "create" =>
            for {
                rate <- createRate()
                resp <- Created(rate.hotel_id)
            }yield resp
    }


    // PUT /rates/uuid  { rateInfo }
    private val updateRateRoute: HttpRoutes[F] = HttpRoutes.of[F] {
        case PUT -> Root / UUIDVar(hotel_id) =>
            database.get(hotel_id) match {
                case Some(rate) =>
                    for {
                        rate <- createRate()
                        _ <- database.put(hotel_id, rate).pure[F]
                        resp <- Ok()
                    }yield resp
                case None       => NotFound(FailureResponse(s"Can not update Rate $hotel_id not found"))
            }
    }

    // DELETE /rates/uuid
    private val deleteRateRoute: HttpRoutes[F] = HttpRoutes.of[F] {
        case DELETE -> Root / UUIDVar(hotel_id) =>
            database.get(hotel_id) match {
                case Some(rate) =>
                    for {
                        rate <- createRate()
                        _ <- database.remove(hotel_id).pure[F]
                        resp <- Ok()
                    }yield resp
                case None       => NotFound(FailureResponse(s"Can not delete Rate $hotel_id not found"))
            }
    }

    val routes = Router(
        "/rate" -> (allRatesRoute <+> findRateRoute <+> createRateRoute <+> updateRateRoute <+> deleteRateRoute)
    )
}

object RateRoutes {
    def apply[F[_]: Concurrent] = new RateRoutes[F]
}