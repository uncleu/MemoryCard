package utilities;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

public class ProgressCircle extends View {

    private int progressMax = 100;
    private int progressNum = 0;
    private final int circleLineWidth = 20;
    private final int textWidth = 2;

    private final RectF RectFCir;
    private final Paint PaintCir;

    final Context cont;

    private String textInCircle;

    public ProgressCircle(Context context, AttributeSet attrs) {
        super(context, attrs);
        cont = context;
        RectFCir = new RectF();
        PaintCir = new Paint();
    }

    public int getProgressMax() {
        return progressMax;
    }

    public void setProgressMax(int maxProgress) {
        this.progressMax = maxProgress;
    }

    public void setProgress(int progress) {
        this.progressNum = progress;
        this.invalidate();
    }

    public String getText() {
        return textInCircle;
    }

    public void setText(String tet) {
        this.textInCircle = tet;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = this.getWidth();
        int height = this.getHeight();
        if (width != height) {
            int min = Math.min(width, height);
            width = min;
            height = min;
        }

        //set attribute of draw
        PaintCir.setAntiAlias(true);
        PaintCir.setColor(Color.rgb(0xe9, 0xe9, 0xe9));//background of progress
        canvas.drawColor(Color.TRANSPARENT);
        PaintCir.setStrokeWidth(circleLineWidth);
        PaintCir.setStyle(Paint.Style.STROKE);
        //position
        RectFCir.left = circleLineWidth / 2; //top left corner x
        RectFCir.top = circleLineWidth / 2; //top left corner y
        RectFCir.right = width - circleLineWidth / 2; //lower right corner x
        RectFCir.bottom = height - circleLineWidth / 2; //lower right corner y
        //draw circle
        canvas.drawArc(RectFCir, -90, 360, false, PaintCir);
        PaintCir.setColor(Color.rgb(0xff, 0xde, 0x0));//color circle
        canvas.drawArc(RectFCir, -90, ((float) progressNum / progressMax) * 360, false, PaintCir);
        //text progress
        PaintCir.setStrokeWidth(textWidth);
        String text = progressNum + "%";
        int textHeight = height / 4;
        PaintCir.setTextSize(textHeight);
        int textWidth = (int) PaintCir.measureText(text, 0, text.length());
        PaintCir.setStyle(Paint.Style.FILL);
        canvas.drawText(text, width / 2 - textWidth / 2, height / 2 + textHeight / 2, PaintCir);

        if(!TextUtils.isEmpty(textInCircle)){
            PaintCir.setStrokeWidth(textWidth);
            text = textInCircle;
            textHeight = height / 8;
            PaintCir.setTextSize(textHeight);
            PaintCir.setColor(Color.rgb(0x99, 0x99, 0x99));
            textWidth = (int) PaintCir.measureText(text, 0, text.length());
            PaintCir.setStyle(Paint.Style.FILL);
            canvas.drawText(text, width / 2 - textWidth / 2, height / 4 + textHeight / 2, PaintCir);
        }

    }

}
