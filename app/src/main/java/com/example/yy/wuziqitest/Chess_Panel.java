package com.example.yy.wuziqitest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public class Chess_Panel extends View{
    private int myPanelWidth ;
    private float myLineHeight;
    private int maxLine = 10;
    private Paint myPaint;
    private Bitmap myWhitePice;
    private Bitmap myBlackPice;
    private float ratioPieceOfLineHight = 3 * 1.0f / 4;

    private boolean isGemOver;
    public static int WHITE_WIN = 0;
    public static int BLACK_WIN = 1;
    private boolean isWhite = true;

    private List<Point> myWhiteArray = new ArrayList<Point>();
    private List<Point> myBlackArray = new ArrayList<Point>();

    private onGameListener onGameListener;
    private int mUnder;

    public Chess_Panel(Context context) {
        this(context, null);
    }

    public Chess_Panel(Context context ,AttributeSet attributeSet){
        super(context , attributeSet);

        init();
    }

    public interface onGameListener {
        void onGameOver(int i);
    }

    public void setOnGameListener(Chess_Panel.onGameListener onGameListener) {
        this.onGameListener = onGameListener;
    }

    private void init() {
        myPaint = new Paint();
        myPaint.setColor(Color.BLACK);
        myPaint.setAntiAlias(true);
        myPaint.setDither(true);
        myPaint.setStyle(Paint.Style.STROKE);
        myWhitePice = BitmapFactory.decodeResource(getResources(),R.drawable.bai);
        myBlackPice = BitmapFactory.decodeResource(getResources(), R.drawable.hei);

    }

    public boolean onTouchEvent(MotionEvent event){
        if (isGemOver) {
            return false;
        }

        int action = event.getAction();
        if (action == MotionEvent.ACTION_UP) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            Point p = getVaLidPiont(x,y);
            if (myWhiteArray.contains(p)|| myBlackArray.contains(p)) {
                return false;
            }
            if (isWhite) {
                myWhiteArray.add(p);
            }else {
                myBlackArray.add(p);
            }
            invalidate();
            isWhite = !isWhite;
        }
        return true;
    }


    private Point getVaLidPiont(int x , int y){
        return new Point((int)(x/myLineHeight),(int)(y/myLineHeight));
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width = Math.min(widthSize, heightSize);

        if (widthMode == MeasureSpec.UNSPECIFIED) {
            width = heightSize;
        }else if (heightMode == MeasureSpec.UNSPECIFIED) {
            width = widthSize;
        }
        setMeasuredDimension(width, width);
    }

    protected void onSizeChanged(int w, int h ,int oldw , int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        myPanelWidth = w;
        myLineHeight = myPanelWidth*1.0f/maxLine;
        mUnder = h - (h - myPanelWidth) / 2;

        int pieceWidth = (int) (myLineHeight*ratioPieceOfLineHight);
        myWhitePice = Bitmap.createScaledBitmap(myWhitePice, pieceWidth, pieceWidth, false);
        myBlackPice = Bitmap.createScaledBitmap(myBlackPice, pieceWidth, pieceWidth, false);
    }

    protected void  onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBroad(canvas);
        drawPiece(canvas);
        checkGameOver();
    }

    private void drawBroad(Canvas canvas){
        int w = myPanelWidth;
        float lineHeight = myLineHeight;
        int startX = (int) (lineHeight/2);
        int endX = (int)(w-lineHeight/2);
        for(int i = 0; i< maxLine; i++){
            int y = (int)((i+1.5)*lineHeight);

            canvas.drawLine(startX, y, endX, y, myPaint);
            canvas.drawLine(y, startX, y, endX, myPaint);
        }
    }

    private void drawPiece(Canvas canvas) {
        int n1 = myWhiteArray.size();
        int n2 = myBlackArray.size();
        for(int i =0; i< n1 ;i++){
            Point whitePoint = myWhiteArray.get(i);
            canvas.drawBitmap(myWhitePice, (whitePoint.x+(1-ratioPieceOfLineHight)/2)*myLineHeight,
                    (whitePoint.y+(1-ratioPieceOfLineHight)/2)*myLineHeight, null);
        }

        for(int i =0; i< n2 ;i++){
            Point blackPoint = myBlackArray.get(i);
            canvas.drawBitmap(myBlackPice, (blackPoint.x+(1-ratioPieceOfLineHight)/2)*myLineHeight,
                    (blackPoint.y+(1-ratioPieceOfLineHight)/2)*myLineHeight, null);
        }
    }

    private void checkGameOver(){
        boolean whiteWin = checkFiveInLine(myWhiteArray);
        boolean blackWin = checkFiveInLine(myBlackArray);

        if (whiteWin || blackWin) {
            isGemOver = true;
            if (onGameListener != null) {
                onGameListener.onGameOver(whiteWin ? WHITE_WIN : BLACK_WIN);
            }
        }
    }

    public int getUnder() {

        return mUnder;
    }


    private boolean checkFiveInLine(List<Point> myArray){
        for(Point p : myArray){
            int x = p.x;
            int y = p.y;

            boolean win_flag =
                    checkHorizontal(x , y ,myArray)||checkVertical(x,y,myArray)
                            ||checkLeftDiagonal(x,y,myArray)||checkRightDiagonal(x,y,myArray);
            if (win_flag) {
                return true;
            }
        }
        return false;
    }


    private boolean checkHorizontal(int x ,int y ,List<Point> myArray){
        int count = 1;
        for(int i = 1;i < 5; i++){
            if (myArray.contains(new Point(x+i,y))) {
                count++;
            }else {
                break;
            }
        }
        if (count == 5) {
            return true;
        }
        for(int i = 1;i < 5; i++){
            if (myArray.contains(new Point(x-i,y))) {
                count++;
            }else {
                break;
            }

            if (count == 5) {
                return true;
            }
        }
        return false;
    }

    private boolean checkVertical(int x ,int y ,List<Point> myArray){
        int count = 1;
        for(int i = 1;i < 5; i++){
            if (myArray.contains(new Point(x,y+i))) {
                count++;
            }else {
                break;
            }

        }
        if (count == 5) {
            return true;
        }
        for(int i = 1;i < 5; i++){
            if (myArray.contains(new Point(x,y-i))) {
                count++;
            }else {
                break;
            }
            if (count == 5) {
                return true;
            }
        }
        return false;
    }

    private boolean checkLeftDiagonal(int x ,int y ,List<Point> myArray){
        int count = 1;
        for(int i = 1;i < 5; i++){
            if (myArray.contains(new Point(x-i,y+i))) {
                count++;
            }else {
                break;
            }

        }
        if (count == 5) {
            return true;
        }
        for(int i = 1;i < 5; i++){
            if (myArray.contains(new Point(x+i,y-i))) {
                count++;
            }else {
                break;
            }
            if (count == 5) {
                return true;
            }
        }
        return false;
    }

    private boolean checkRightDiagonal(int x ,int y ,List<Point> myArray){
        int count = 1;
        for(int i = 1;i < 5; i++){
            if (myArray.contains(new Point(x-i,y-i))) {
                count++;
            }else {
                break;
            }
        }
        if (count == 5) {
            return true;
        }
        for(int i = 1;i < 5; i++){
            if (myArray.contains(new Point(x+i,y+i))) {
                count++;
            }else {
                break;
            }
            if (count == 5) {
                return true;
            }
        }
        return false;
    }

    protected void restartGame(){
        myWhiteArray.clear();
        myBlackArray.clear();
        isGemOver = false;
        isWhite = false;
        invalidate();
    }
}
