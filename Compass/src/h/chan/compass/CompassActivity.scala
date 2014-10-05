package h.chan.compass

import android.util.AttributeSet
import android.content.Context
import android.view.View
import android.view.View.MeasureSpec
import android.app.Activity
import android.graphics.Paint
import android.view.accessibility.AccessibilityEvent
import android.os.Bundle
import android.graphics.Canvas

class CompassView(ctx: Context,
                  attrs: AttributeSet,
                  style:  Int
  ) extends View(ctx, attrs, style) {

  var markerPaint: Paint = _
  var textPaint: Paint = _
  var circlePaint: Paint = _
  var northString: String = _
  var eastString: String = _
  var southString: String = _
  var westString: String = _
  var textHeight: Int = _

  initCompassView

  def initCompassView: Unit = {
    setFocusable(true)

    val r = getResources()

    circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG)
    circlePaint.setColor(r.getColor(R.color.background_color))
    circlePaint.setStrokeWidth(1)
    circlePaint.setStyle(Paint.Style.FILL_AND_STROKE)

    northString = r.getString(R.string.cardinal_north)
    eastString = r.getString(R.string.cardinal_east)
    southString = r.getString(R.string.cardinal_south)
    westString = r.getString(R.string.cardinal_west)

    textPaint = new Paint(Paint.ANTI_ALIAS_FLAG)
    textPaint.setColor(r.getColor(R.color.text_color))
    textHeight = textPaint.measureText("yY").toInt

    markerPaint = new Paint(Paint.ANTI_ALIAS_FLAG)
    markerPaint.setColor(r.getColor(R.color.marker_color))

  }


  private var _bearing: Float = _

  def bearing = _bearing
  def bearing_=(b: Float) = {
    _bearing = b
    sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED)
  }

  def this(ctx: Context) = this(ctx, null, 0)
  def this(ctx: Context, attrs: AttributeSet) =
    this(ctx, attrs, 0)


  override def onMeasure(widthSpec: Int, heightSpec: Int): Unit = {
    val measuredWidth = measure(widthSpec)
    val measuredHeight = measure(heightSpec)
    val dim = Math.min(measuredWidth, measuredHeight)

    setMeasuredDimension(dim, dim)
  }

  private[this] def measure(spec: Int): Int = {
    val specMode = MeasureSpec.getMode(spec)
    val specSize = MeasureSpec.getSize(spec)
    if (specMode == MeasureSpec.UNSPECIFIED) 200
    else specSize
  }

  override def onDraw(canvas: Canvas): Unit = {
    val width = getMeasuredWidth()
    val height = getMeasuredHeight()
    val px = width / 2
    val py = height / 2
    val radius = Math.min(px, py)
    // draw the background
    canvas.drawCircle(px, py, radius, circlePaint)
    // rotate our pespective
    canvas.save()
    canvas.rotate(-bearing, px, py)

    val textWidth = textPaint.measureText("W")
    val cardinalX = px - (textWidth / 2)
    val cardinalY = py - radius + textHeight

    (0 until 24) foreach { i =>
      // draw a marker
      canvas.drawLine(px, py-radius, px, py-radius+10, markerPaint)
      canvas.save()
      canvas.translate(0, textHeight)

      if (i % 6 == 0) {
        var dirString = ""
        i match {
          case 0 =>
            dirString = northString
            val arrowY = 2 * textHeight
            canvas.drawLine(px, arrowY,
              px-5, 3*textHeight, markerPaint)
            canvas.drawLine(px, arrowY,
              px+5, 3*textHeight, markerPaint)
          case 6 =>
            dirString = eastString
          case 12 =>
            dirString = southString
          case 18 =>
            dirString = westString
        }
      } else if ( i % 3 == 0) {
        val angle = String.valueOf(i * 15)
        val angleTextWidth = textPaint.measureText(angle)

        val angleTextX = (px - angleTextWidth/2).toInt
        val angleTextY = (py - radius + textHeight).toInt
        canvas.drawText(angle, angleTextX, angleTextY, textPaint)
      }
      canvas.restore()
      canvas.rotate(15, px, py)
    }
    canvas.restore()
  }

  override def dispatchPopulateAccessibilityEvent(event: AccessibilityEvent): Boolean = {
    super.dispatchPopulateAccessibilityEvent(event)
  }
}

class CompassActivity extends Activity {

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_compass)
    val cv = findViewById(R.id.compassView).asInstanceOf[CompassView]
    cv.bearing = 45.toFloat
  }
}
