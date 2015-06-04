package com.fortysevendeg.repository

import android.database.Cursor
import com.fortysevendeg.ninecardslauncher.commons.{ContentResolverWrapper, NineCardsUri}
import org.specs2.mock.Mockito

import scala.util.Random

trait MockContentResolverWrapper extends Mockito {

  lazy val contentResolverWrapper = mock[MockContentResolverWrapperImpl]

  class MockContentResolverWrapperImpl extends ContentResolverWrapper {

    override def insert(
        nineCardsUri: NineCardsUri,
        values: Map[String, Any]): Int = Random.nextInt(1)

    override def update(
        nineCardsUri: NineCardsUri,
        values: Map[String, Any],
        where: String = "",
        whereParams: Seq[String] = Seq.empty): Int = Random.nextInt(1)

    override def updateById(
        nineCardsUri: NineCardsUri,
        id: Int,
        values: Map[String, Any],
        where: String = "",
        whereParams: Seq[String] = Seq.empty): Int = Random.nextInt(1)

    override def delete(
        nineCardsUri: NineCardsUri,
        where: String = "",
        whereParams: Seq[String] = Seq.empty): Int = Random.nextInt(1)

    override def deleteById(
        nineCardsUri: NineCardsUri,
        id: Int,
        where: String = "",
        whereParams: Seq[String] = Seq.empty): Int = Random.nextInt(1)

    override def fetch[T](
        nineCardsUri: NineCardsUri,
        projection: Seq[String],
        where: String = "",
        whereParams: Seq[String] = Seq.empty,
        orderBy: String = "")(f: (Cursor) => Option[T]): Option[T] = None

    override def fetchAll[T](
        nineCardsUri: NineCardsUri,
        projection: Seq[String],
        where: String = "",
        whereParams: Seq[String] = Seq.empty,
        orderBy: String = "")(f: (Cursor) => Seq[T]): Seq[T] = Seq.empty

    override def findById[T](
        nineCardsUri: NineCardsUri,
        id: Int,
        projection: Seq[String],
        where: String = "",
        whereParams: Seq[String] = Seq.empty,
        orderBy: String = "")(f: (Cursor) => Option[T]): Option[T] = None
  }

}
