package com.krud3.rateBoard.core 
import com.krud3.rateBoard.domain.rate._

import cats._
import cats.effect._
import cats.implicits._
import doobie._
import doobie.implicits._
import doobie.postgres.implicits._
import doobie.util._
import java.util.UUID

trait Rates[F[_]]{

    // "algebra"
    // CRUD
    def create(date: Long, rate: Double): F[UUID]
    def all(): F[List[Rate]]
    def find(hotel_id: UUID): F[Option[Rate]]
    def update(hotel_id: UUID, rate: Double): F[Option[Rate]]
    def delete(hotel_id: UUID): F[Int]
}

/*
    hotel_id: UUID,
    date: Long,
    rate: Double
*/

class LiveRates[F[_]: MonadCancelThrow] private (xa: Transactor[F]) extends Rates[F]{

    override def create(date: Long, rate: Double): F[UUID] = 
        sql"""
            INSERT INTO rates(
                date,
                rate  
            ) VALUES (
                ${System.currentTimeMillis()},
                ${rate}
            )
        """
        .update
        .withUniqueGeneratedKeys[UUID]("hotel_id")
        .transact(xa)   

    override def all(): F[List[Rate]] = 
        sql"""
            SELECT
                hotel_id,
                date,
                rate
            FROM rates
        """"
        .query[Rate]
        .to[List]
        .transact(xa)

    override def find(hotel_id: UUID): F[Option[Rate]] = 
        sql"""
            SELECT
                hotel_id,
                date,
                rate
            FROM rates
            WHERE hotel_id = $hotel_id
        """
        .query[Rate]
        .option
        .transact(xa)

    override def update(hotel_id: UUID, rate: Double): F[Option[Rate]] = 
        sql"""
            UPDATE rates
            SET
                rate = $rate
            WHERE hotel_id = $hotel_id
        """
        .update
        .run
        .transact(xa)
        .flatMap(_ => find(hotel_id)) // return the updated rate

    override def delete(hotel_id: UUID): F[Int] = 
        sql"""
            DELETE FROM rates
            WHERE hotel_id = ${hotel_id}  
        """
        .update
        .run
        .transact(xa)
}

object LiveRates {
    def apply[F[_]: MonadCancelThrow](xa: Transactor[F]): F[LiveRates[F]] = new LiveRates[F](xa).pure[F]
}