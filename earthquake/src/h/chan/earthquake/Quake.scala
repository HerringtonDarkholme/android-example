package h.chan.earthquake

import java.util.Date
import java.text.SimpleDateFormat
import android.location.Location

case class Quake(
  date: Date, details: String, location: Location, magnitude: Double, link: String) {

  override def toString: String = {
    val smd = new SimpleDateFormat("HH.mm")
    val dateString = smd.format(date)
    dateString + ": " + magnitude + " " + details
  }
}
