package com.rhomobile.rhodes.barcode;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.util.Log;

import com.rhomobile.rhodes.Rhodes;
import com.rhomobile.rhodes.Logger;

import net.sourceforge.zbar.*;


class Barcode {    
    private static final String TAG = "Barcode";

    private static Image convertToImage (Bitmap bitmap) {
	Bitmap mutable_bitmap = bitmap.copy(Bitmap.Config.RGB_565, true);
	
	//convert bitmap to one-byte greyscale
	Canvas canvas = new Canvas(mutable_bitmap);
	Paint  paint = new Paint();
	ColorMatrix matrix = new ColorMatrix();
	matrix.setSaturation(0);
	ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
	paint.setColorFilter(filter);
	canvas.drawBitmap(bitmap, 0, 0, paint);

	//get bitmap pixels
	int width  = bitmap.getWidth();
	int height = bitmap.getHeight();

	int []  pixels = new int [width * height];
	bitmap.getPixels(pixels, 0, width, 0 , 0, width, height);

	//get grey bytes from pixels
	byte[] bytes = new byte[width * height];
	int pixel, r, g, b;
	for (int i = 0; i < pixels.length; i++) {
	    pixel = pixels[i];
	    r = (pixel >> 16) & 0xff;
	    g = (pixel >> 8) & 0xff;
	    b = pixel & 0xff;
	    bytes[i] = (byte )r;
	}

	//create image
	Image image = new Image(width, height, "Y800");
	image.setData(bytes);
	return image;
    }

    public static void scanImage (String callback, String filePath) {
	Bitmap bitmap = BitmapFactory.decodeFile(filePath);

	Logger.I(TAG, "Trying to scan image \"" + filePath + "\"");

	if (bitmap == null) {
	    Logger.E(TAG, "Failed to decode bitmap \"" + filePath + "\"");
	    doCallback(callback, "", "Failed to decode bitmap.", false);
	    return;
	}
	
	Image image = convertToImage(bitmap);
	if (image == null) {
	    Logger.E(TAG, "Failed to convert bitmap \"" + filePath + "\" to greyscale");
	    doCallback(callback, "", "Failed to convert bitmap.", false);
	    return;
	}

	ImageScanner scanner = null;
	try {
	    scanner  = new ImageScanner();
	} catch (OutOfMemoryError e) {
	    Logger.E(TAG, "Failed to create image scanner - out of memory error");
	    doCallback(callback, "", "Out of memory error", false);
	}

	int ret = 0;
	try {
	    ret = scanner.scanImage(image);
	} catch ( UnsupportedOperationException e) {
	    Logger.E(TAG, "Failed to scan image - system error");
	    doCallback(callback, "", "System error", false);
	}

	if (ret == 0) {
	    Logger.E(TAG, "Failed to recognize barcode - bogus image");
	    doCallback(callback, "", "Failed to recognize barcode", false);
	    return;
	}
	
	String result = "";
        SymbolSet syms = scanner.getResults();
        for(Symbol sym : syms) {

	    switch (sym.getType()) {
	    case Symbol.PARTIAL:
		result += "PARTIAL";
		break;
	    case Symbol.EAN8:
		result += "EAN8";
		break;
	    case Symbol.UPCE:
		result += "UPCE";
		break;
	    case Symbol.ISBN10:
		result += "ISBN10";
		break;
	    case Symbol.UPCA:
		result += "UPCA";
		break;
	    case Symbol.EAN13:
		result += "EAN13";
		break;
	    case Symbol.ISBN13:
		result += "ISBN13";
		break;
	    case Symbol.I25:
		result += "I25";
		break;
	    case Symbol.CODE39:
		result += "CODE39";
		break;
	    case Symbol.PDF417:
		result += "PDF417";
		break;
	    case Symbol.QRCODE:
		result += "QRCODE";
		break;
	    case Symbol.CODE128:
		result += "CODE128";
		break;
	    default:
	    }
	    
	    result += ":" + sym.getData() + " ";
	    Logger.I(TAG, "scanImage result: " + result);
        }

	bitmap.recycle();
	image.destroy();

	Rhodes.performOnUiThread(new RunCallback (callback, result, "", false), false);
    }

    public static void scanImageWithPreview (String callback, String filePath) {
	//TODO
	doCallback (callback, "", "", true);
    }



    private static class RunCallback implements Runnable {
	String callbackUrl, resultMsg, errorMsg;
	boolean isCancelled;

	public RunCallback (String callback, String result, String error, 
			    boolean cancelled) 
	{
	    callbackUrl = callback;
	    resultMsg   = result;
	    errorMsg    = error;
	    isCancelled = cancelled;
	}

	public void run() {
	    doCallback (callbackUrl, resultMsg, errorMsg, isCancelled);
	} 
    }

    public static native void doCallback(String callbackUrl, 
					 String result, String error, boolean cancelled);
}
