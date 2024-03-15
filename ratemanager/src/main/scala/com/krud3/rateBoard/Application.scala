package com.krud3.rateBoard

import org.http4s._
import org.http4s.dsl._
import org.http4s.dsl.impl._
import org.http4s.server._
import cats.effect._
import cats.effect.IOApp
import cats._
import cats.implicits._
import cats.effect.IO
import org.http4s.ember.server.EmberServerBuilder
import org.typelevel.log4cats.Logger

import com.krud3.rateBoard.http.HttpApi
import com.krud3.rateBoard.config._
import com.krud3.rateBoard.config.syntax._
import pureconfig.ConfigSource
import pureconfig.error.ConfigReaderException
import org.typelevel.log4cats.slf4j.Slf4jLogger

/* 
    1- Add a plain health endpoint to our app
    2- Add minimal configuration
    3- Basic http server layout 
 */

object Application extends IOApp.Simple {

    given logger: Logger[IO] = Slf4jLogger.getLogger[IO]

    override def run = ConfigSource.default.loadF[IO, EmberConfig].flatMap { config =>
        EmberServerBuilder
            .default[IO]
            .withHost{config.host}
            .withPort(config.port)
            .withHttpApp(HttpApi[IO].endpoints.orNotFound)
            .build.use(_ => IO.println("Server ready!") *> IO.never)
    }
    
}