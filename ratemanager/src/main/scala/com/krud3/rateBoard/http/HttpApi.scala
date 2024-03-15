package com.krud3.rateBoard.http

import org.http4s._
import org.http4s.dsl._
import org.http4s.dsl.impl._
import org.http4s.server._
import cats.effect._
import cats.implicits._
import org.typelevel.log4cats.Logger
import com.krud3.rateBoard.http.routes._

class HttpApi[F[_]: Concurrent: Logger] private{
    private val healthRoutes = HealthRoutes[F].routes
    private val rateRoutes = RateRoutes[F].routes

    val endpoints = Router(
        "/api" -> (healthRoutes <+> rateRoutes)
    )
}

object HttpApi {
    def apply[F[_]: Concurrent: Logger] = new HttpApi[F]
}