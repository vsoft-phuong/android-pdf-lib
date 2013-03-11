package com.sun.pdfview.facade;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

import net.sf.andpdf.nio.ByteBuffer;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.util.Log;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.decrypt.PDFPassword;

public class PDFFacade {

	private static final int STARTPAGE = 1;
	
	private static final float DEFAULT_ZOOM = 1.0f;
	private static final String TAG = "PDFVIEWER";
	private float zoom = DEFAULT_ZOOM;
	
	
	private static PDFFacade instance = null;
	
	private PDFFacade() {		
	}
	
	private PDFFacade(float zoom) {		
		this.zoom = zoom;
	}
	
	public static PDFFacade getInstance(float zoom) {
		if(instance == null) 
			instance = new PDFFacade(zoom);
		
		return instance;
	}
	
	public static PDFFacade getInstance() {
		if(instance == null) 
			instance = new PDFFacade();
		
		return instance;
	}
	

	public Bitmap createAndroidBitmapByPdfBytes(byte[] fileInBytes) throws Exception {
		Bitmap image = null;
		try { 
			PDFFile mPdfFile =	generatePDF(fileInBytes, null);
			image = generateImageByPDF(mPdfFile);			
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return image;
	}
	
	public Bitmap createAndroidBitmapByPdfBytes(File file) throws Exception {
		Bitmap image = null;
		try { 
			PDFFile mPdfFile =	generatePDF(file, null);
			image = generateImageByPDF(mPdfFile);			
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
		return image;
	}
	
	public PDFFile generatePDF(byte[] fileInBytes) throws IOException {
		return generatePDF(fileInBytes, null);
	}

	public PDFFile generatePDF(byte[] fileInBytes,String password) throws IOException {
		if (fileInBytes == null) 
			throw new RuntimeException("Arquivo não encontrado...");
		
		PDFFile pdfFile = openFile(fileInBytes, password);
		return pdfFile;		
	}
	
	public PDFFile generatePDF(String filename, String password) throws IOException {       		
		File file = new File(filename);		
		return generatePDF(file, password);
	}	
	
	private PDFFile generatePDF(File file,String password) throws IOException {
		PDFFile pdfFile = null;
		long len = file.length();
		if (len == 0) {
			throw new RuntimeException("Arquivo não encontrado...");
		} else {
			pdfFile = openFile(file, password);
		}
		return pdfFile;		
	}
	
	private PDFFile openFile(byte[] fileInBytes, String password) throws IOException {
		ByteBuffer byteBuffer =  ByteBuffer.NEW(fileInBytes);
		PDFFile pdFile = createPDFFile(password, byteBuffer);
		return pdFile;
	}

	private Bitmap generateImageByPDF(PDFFile pdfFile) throws Exception {
		Log.e(TAG,"Valor do zoom: " + zoom);
		Bitmap image = null;
		if (pdfFile != null) {
			image = convertPDFToImage(pdfFile, zoom);					
		}

		return image;

	}
	
	public Bitmap generateImageByPDF(PDFFile pdfFile, int page) throws Exception {
		Log.e(TAG,"Valor do zoom: " + zoom);
		Bitmap image = null;
		if (pdfFile != null) {
			image = convertPDFToImage(pdfFile, zoom,page);					
		}

		return image;
	}
	
	
	private Bitmap convertPDFToImage(PDFFile mPdfFile, float zoom, int page) throws Exception {
		Bitmap image = null;
		try {
			PDFPage mPdfPage = mPdfFile.getPage(page, true);			
			float width = mPdfPage.getWidth();
			float heigth = mPdfPage.getHeight();			
			RectF clip = null;			
			image = mPdfPage.getImage((int)(width*zoom), (int)(heigth*zoom), clip, true, true);

		} catch (Throwable e) {
			e.printStackTrace();
			Log.e(TAG, e.getMessage(), e);		
		}
		return image;
	}

	private Bitmap convertPDFToImage(PDFFile mPdfFile, float zoom) throws Exception {
		return convertPDFToImage(mPdfFile, zoom, STARTPAGE);
	}
	
	private PDFFile openFile(File file, String password) throws IOException {		
		RandomAccessFile randonAccessFile = new RandomAccessFile(file, "r");
		FileChannel channel = randonAccessFile.getChannel();
		ByteBuffer byteBuffer =  ByteBuffer.NEW(channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size()));
		PDFFile pdFile = createPDFFile(password, byteBuffer);	

		return pdFile;
	}	
	
	private PDFFile createPDFFile(String password, ByteBuffer byteBuffer) throws IOException {
		PDFFile pdFile = null;

		if (password == null)
			pdFile = new PDFFile(byteBuffer);
		else
			pdFile = new PDFFile(byteBuffer, new PDFPassword(password));

		return pdFile;
	}
	
	/*public byte[] createImageBitmapMonochromatic(Bitmap image) throws Exception {
	LogHandler.logInfo("PASSO 3 = Imagem Gerada...  = INICIOU" );
	ByteArrayOutputStream out =  new ByteArrayOutputStream();

	//BmpWriter.write(out, image);
	//BmpWriter.write(new FileOutputStream("/sdcard/testeMonochrome.bmp"), image);
	//BmpWriter.writeHalfToneRandom(new FileOutputStream("/sdcard/testeRandom.bmp"), image);
	//BmpWriter.writeHalfToneOrdered(new FileOutputStream("/sdcard/testeOrdenado.bmp"), image);
	BmpWriter.writeHalfToneOrdered(out, image);
	
	LogHandler.logInfo("PASSO 3 = Imagem Gerada...  = TERMINOU" );
	return out.toByteArray();
}	*/


}