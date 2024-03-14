package com.krud3.rateBoard.domain

import java.util.UUID

object rate{
    case class Rate(
        hotel_id: UUID,
        date: Long,
        rate: Double
    )
}