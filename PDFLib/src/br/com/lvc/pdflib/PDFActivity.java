package br.com.lvc.pdflib;

/*

public class PDFActivity extends BaseActivity {

	private PDFFile pdfFile = null; 


	private static final String PDF_FILE_PATH = "tabela_valores.pdf";

	private TouchImageView touchImageView;
	private PDFFacade pdfFacade; 
	private int page = 0;
	private int numPage = 0;
	private int erroRenderCount = 0;
	private static final int ERROR_RENDER_MAX = 10;
	private byte[] pdfInBytes = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_viewer);
		touchImageView = (TouchImageView)findViewById(R.id.image_view_touch);

		try {
 
			InputStream inputStream = getAssets().open(PDF_FILE_PATH);
			pdfInBytes = toByteArray(inputStream);
			pdfFacade = PDFFacade.getInstance(2.0f);
			pdfFile = pdfFacade.generatePDF(pdfInBytes);
			numPage = pdfFile.getNumPages();

			Bitmap 	bitmap = gerarPDF();//pdfFacade.createAndroidBitmapByPdfBytes(pdfInBytes);

			touchImageView.setImageBitmap(bitmap);

		} catch(Exception e) {
			e.printStackTrace();

		}		
	}

	private Bitmap gerarPDF() {
		Bitmap bitmap = null; 

		if(erroRenderCount == ERROR_RENDER_MAX) {
			showMessageAttention(R.string.falha_ao_rendereziar_pdf);
		} else {
			try {
				bitmap = pdfFacade.createAndroidBitmapByPdfBytes(pdfInBytes);
				erroRenderCount = 0;
			} catch(Exception e) {
				erroRenderCount ++;
				e.printStackTrace();
				return gerarPDF();
			}
		}


		return bitmap;
	}

	public void onClickVoltar(View view) {
		page = page -1;
		if(page >= 1)
			mudarPagina(page);
		else
			page = 1;
	}

	public void onClickIr(View view) {
		page = page + 1;
		if(page <= numPage) 
			mudarPagina(page);
		else 
			page = numPage;			
	}

	private void mudarPagina(int page) {
		if(erroRenderCount == ERROR_RENDER_MAX) {
			showMessageAttention(R.string.falha_ao_rendereziar_pdf);
		} else {
			Bitmap bitmap = null;

			try {
				bitmap = pdfFacade.generateImageByPDF(pdfFile, page);
				BitmapDrawable drawable = (BitmapDrawable) touchImageView.getDrawable();
				drawable.getBitmap().recycle();
				touchImageView.setImageBitmap(bitmap);
				touchImageView.invalidate();
				erroRenderCount = 0;
			} catch (Exception e) {
				Log.e("RENDER", "Falhou renderiza‹o: " + erroRenderCount);
				erroRenderCount ++;
				mudarPagina(page);
			}

		}	
	}



	private byte[] toByteArray(InputStream in) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int next = in.read();
		while (next > -1) {
			bos.write(next);
			next = in.read();
		}
		bos.flush();
		byte[] result = bos.toByteArray();

		return result;
	}




} */

