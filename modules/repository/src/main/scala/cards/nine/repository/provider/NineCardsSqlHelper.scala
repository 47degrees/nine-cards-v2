package cards.nine.repository.provider

import android.content.Context
import android.database.sqlite.{SQLiteDatabase, SQLiteOpenHelper}
import cards.nine.commons.javaNull

class NineCardsSqlHelper(context: Context)
  extends SQLiteOpenHelper(context, NineCardsSqlHelper.databaseName, javaNull, NineCardsSqlHelper.databaseVersion) {

  override def onCreate(db: SQLiteDatabase) = {
    db.execSQL(AppEntity.createTableSQL)
    db.execSQL(CollectionEntity.createTableSQL)
    db.execSQL(CardEntity.createTableSQL)
    db.execSQL(DockAppEntity.createTableSQL)
    db.execSQL(MomentEntity.createTableSQL)
    db.execSQL(UserEntity.createTableSQL)
    db.execSQL(WidgetEntity.createTableSQL)
  }

  override def onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = { }
}

object NineCardsSqlHelper {
  val id = "_id"
  val databaseName = "nine-cards.db"
  val databaseVersion = 1
}
