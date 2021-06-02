package org.apache.pdfbox.examples.pdmodel;

import java.util.Arrays;
import java.util.List;
import org.apache.pdfbox.tools.PDFCourier2Text;

public class DisturbingThisIs {

	public static void main(String[] args) throws Exception {

		PDFCourier2Text stripper = new PDFCourier2Text(5.5f, 8);

		List<String> pages = stripper.extractText("courier_1977_04.pdf");
		List<List<PDFCourier2Text.TitleInfo>> titles = stripper.getTitles();
		System.out.println(Arrays.toString(pages.toArray()));

	}

}
