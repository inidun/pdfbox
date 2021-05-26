package org.apache.pdfbox.examples.pdmodel;


// import java.io.StringWriter;
import java.util.List;
// import java.io.File;
// import org.apache.pdfbox.Loader;
// import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.tools.PDFCourier2Text;


public class DisturbingThisIs {

	public static void main(String[] args) throws Exception {

		// PDDocument document = Loader.loadPDF(new File("courier_1977_04.pdf"));

		PDFCourier2Text stripper = new PDFCourier2Text(5.5f, 8);

		List<String> pages = stripper.extractText("courier_1977_04.pdf");
		List<List<PDFCourier2Text.TitleInfo>> titles = stripper.getTitles();
		// StringWriter sw = new StringWriter();
		// stripper.writeText(document, sw);
		System.out.println("hej");

//		for(int i=0; i < document.getNumberOfPages(); i++) {
//			PDPage page = document.getPage(i);
//			System.out.println(page.getThreadBeads().size());
//			page.getThreadBeads().forEach( bead -> {
//				System.out.println(bead.getNextBead());
//			});
//		}
	}



}
