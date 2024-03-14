package com.krud3.rateBoard

import cats.effect.IOApp
import cats.* 
import cats.implicits.*
import cats.effect.IO
import cats.effect.*
import org.http4s.ember.server.EmberServerBuilder
import com.krud3.rateBoard.http.HttpApi
import com.krud3.rateBoard.config._
import com.krud3.rateBoard.config.syntax._
import pureconfig.ConfigSource
import pureconfig.error.ConfigReaderException

/* 
    1- Add a plain health endpoint to our app
    2- Add minimal configuration
    3- Basic http server layout 
 */

object Application extends IOApp.Simple {
    val configSource = ConfigSource.default.load[EmberConfig]

    override def run = ConfigSource.default.loadF[IO, EmberConfig].flatMap { config =>
        EmberServerBuilder
            .default[IO]
            .withHost{config.host}
            .withPort(config.port)
            .withHttpApp(HttpApi[IO].endpoints.orNotFound)
            .build.use(_ => IO.println("Server ready!") *> IO.never)
    }
    
}