package org.apache.pdfbox.examples.pdmodel;


import java.io.StringWriter;
import java.io.File;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.tools.PDFCourier2Text;

public class DisturbingThisIs {

	public static void main(String[] args) throws Exception {

		PDDocument document = Loader.loadPDF(new File("courier_1977_04.pdf"));

		PDFCourier2Text stripper = new PDFCourier2Text();
		StringWriter sw = new StringWriter();
		stripper.writeText(document, sw);
		System.out.println(sw.toString());

//		for(int i=0; i < document.getNumberOfPages(); i++) {
//			PDPage page = document.getPage(i);
//			System.out.println(page.getThreadBeads().size());
//			page.getThreadBeads().forEach( bead -> {
//				System.out.println(bead.getNextBead());
//			});
//		}
	}



}
