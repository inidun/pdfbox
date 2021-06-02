package org.apache.pdfbox.examples.pdmodel;

import java.util.Arrays;
import java.util.List;
import org.apache.pdfbox.tools.PDFCourier2Text;

public class ExtractCourierText {

	public static void main(String[] args) throws Exception {

		PDFCourier2Text stripper = new PDFCourier2Text(5.5f, 8);
		List<String> pages = stripper.extractText("012656engo.pdf");
		List<List<PDFCourier2Text.TitleInfo>> titles = stripper.getTitles();
		System.out.println(Arrays.toString(pages.toArray()));

	}

}
