package com.example.hello

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import android.graphics.Canvas
import android.graphics.Paint

class ToDoListItemView(ctx: Context,
                       attrs: AttributeSet,
                       ds: Int)
  extends TextView(ctx, attrs, ds) {

  init

  private var marginPaint: Paint = _
  private var linePaint: Paint = _
  private var paperColor: Int = _
  private var margin: Float = _

  def this(ctx: Context) =
    this(ctx, null, 0)

  def this(ctx: Context, attrs: AttributeSet) =
    this(ctx, attrs, 0)

  private def init: Unit = {
    // get a reference to resources
    val myResources = getResources()
    // create brushes
    marginPaint = new Paint(Paint.ANTI_ALIAS_FLAG)
    marginPaint.setColor(myResources.getColor(R.color.notepad_margin))
    linePaint = new Paint(Paint.ANTI_ALIAS_FLAG)
    linePaint.setColor(myResources.getColor(R.color.notepad_lines))

    // get background color
    paperColor = myResources.getColor(R.color.notepad_paper)
    margin = myResources.getDimension(R.dimen.notepad_margin)
  }

  override def onDraw(canvas: Canvas): Unit = {
    // color as paper
    canvas.drawColor(paperColor)

    // draw ruled lines
    canvas.drawLine(0, 0, 0, getMeasuredHeight(), linePaint)
    canvas.drawLine(0, getMeasuredHeight(), getMeasuredWidth(),
                    getMeasuredHeight(), linePaint)
    // draw margin
    canvas.drawLine(margin, 0, margin, getMeasuredHeight(), marginPaint)

    // move text across from the margin
    canvas.save()
    canvas.translate(margin, 0)

    super.onDraw(canvas)
    canvas.restore()
  }
}
