package com.juvcarl.shoplist.database.util

import androidx.room.TypeConverter
import kotlinx.datetime.Instant
import java.util.UUID


class InstantConverter {
    @TypeConverter
    fun longToInstant(value: Long?): Instant? =
        value?.let(Instant::fromEpochMilliseconds)

    @TypeConverter
    fun instantToLong(instant: Instant?): Long? =
        instant?.toEpochMilliseconds()
}

class UUIDConverter{
    @TypeConverter
    fun stringToUUID(value: String): UUID = UUID.fromString(value)

    @TypeConverter
    fun UUIDtoString(uuid: UUID): String = uuid.toString()
}