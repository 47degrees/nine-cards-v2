package com.fortysevendeg.ninecardslauncher.provider

import android.database.Cursor
import com.fortysevendeg.ninecardslauncher.provider.NineCardsSqlHelper._

import scala.annotation.tailrec

trait DBUtils {

  def execAllVersionsDB() = (1 to DatabaseVersion) foreach { version => execVersion(version) }

  def execVersionsDB(oldVersion: Int, newVersion: Int) =
    (oldVersion + 1 to newVersion) foreach { version => execVersion(version) }

  def execVersion(version: Int) = {}

  def getEntityFromCursor[T](conversionFunction: Cursor => T)(cursor: Cursor): Option[T] = {
    val entity = cursor.moveToFirst() match {
      case true => Some(conversionFunction(cursor))
      case _ => None
    }

    cursor.close()
    entity
  }

  def getListFromCursor[T](conversionFunction: Cursor => T)(cursor: Cursor): Seq[T] = {
    @tailrec
    def getListFromEntityLoop(cursor: Cursor, result: Seq[T]): Seq[T] =
      cursor match {
        case validCursor if validCursor.isAfterLast => result
        case _ =>
          val entity = conversionFunction(cursor)
          cursor.moveToNext
          getListFromEntityLoop(cursor, result :+ entity)
      }

    val list = cursor.moveToFirst() match {
      case true => getListFromEntityLoop(cursor, Seq.empty[T])
      case _ => Seq.empty[T]
    }

    cursor.close()
    list
  }
}

object DBUtils extends DBUtils
