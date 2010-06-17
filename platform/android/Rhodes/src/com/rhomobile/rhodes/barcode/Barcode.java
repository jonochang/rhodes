package com.rhomobile.rhodes.barcode;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import net.sourceforge.zbar.*;

import com.rhomobile.rhodes.Logger;

import android.util.Log;

class Barcode {
    
    private static final String TAG = "Barcode";

    private static String m_result;

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
	Log.d(TAG, "scanImage: " + filePath);
       
	Bitmap bitmap = BitmapFactory.decodeFile(filePath);

	if (bitmap == null) {
	    Logger.E(TAG, "Failed to decode bitmap \"" + filePath + "\"");
	    return;
	}
	
	Image image = convertToImage(bitmap);
	if (image == null) {
	    Logger.E(TAG, "Failed to convert bitmap");
	    return;
	}

	ImageScanner scanner = null;
	try {
	    scanner  = new ImageScanner();
	} catch (OutOfMemoryError e) {
	    doCallback(callback, "", "Out of memory error", false);
	}

	int ret = 0;
	try {
	    ret = scanner.scanImage(image);
	} catch ( UnsupportedOperationException e) {
	    doCallback(callback, "", "System error", false);
	}

	if (ret == 0) {
	    Logger.E(TAG, "Failed to recognize barcode");
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
	    Log.v(TAG, "scanImage: " + result);
	    Logger.I(TAG, "scanImage: " + result);
        }

	bitmap.recycle();
	image.destroy();

	m_result = result;

	doCallback (callback, result, "", false);
    }

    public static void scanImageWithPreview (String callback, String filePath) {
	//TODO
	doCallback (callback, "", "", true);
    }

    public static native void doCallback(String callbackUrl, 
					 String result, String error, 
					 boolean cancelled);
}
