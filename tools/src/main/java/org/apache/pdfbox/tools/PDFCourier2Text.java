/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.pdfbox.tools;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
// import java.util.HashSet;
// import java.util.Iterator;
import java.util.List;
// import java.util.Set;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
// import org.apache.pdfbox.pdmodel.font.PDFontDescriptor;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

/**
 * Wrap stripped text in simple HTML, trying to form HTML paragraphs. Paragraphs
 * broken by pages, columns, or figures are not mended.
 *
 * @author John J Barton
 *
 */
public class PDFCourier2Text extends PDFTextStripper
{
    public class TitleInfo {
        public String title;
        public int position;

        public TitleInfo(String title, int position) {
            this.title = title;
            this.position = position;
        }
    }

    private float titleFontSizeInPt = 5.5f;
    private float currentFontSizeInPt = 0f;
    private int minTitleLengthInCharacters = 8;
    private String currentTitle = "";
    private int pageCharacterCount = 0;
    private PDDocument document = null;
    private List<String> pages = null;
    private List<List<TitleInfo>> pageTitles = null;
    private List<TitleInfo> currentTitles = null;
    private int pageSeparatorCount = 0;

    /**
     * Constructor.
     * @throws IOException If there is an error during initialization.
     */
    public PDFCourier2Text(float titleFontSizeInPt, int minTitleLengthInCharacters) throws IOException
    {
        this.titleFontSizeInPt = titleFontSizeInPt;
        this.minTitleLengthInCharacters = minTitleLengthInCharacters;
        setLineSeparator(LINE_SEPARATOR);
        setParagraphEnd(LINE_SEPARATOR);
    }


    public List<String> extractText(String filename) throws IOException
    {
        output = new StringWriter();
        pages = new ArrayList<String>();
        pageTitles = new ArrayList<List<TitleInfo>>();
        document = Loader.loadPDF(new File(filename));
		StringWriter sw = new StringWriter();
		writeText(document, sw);
		return pages;
    }

    public List<List<TitleInfo>> getTitles() {
        return pageTitles;
    }

    @Override
    protected void startDocument(PDDocument document) throws IOException
    {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void endDocument(PDDocument document) throws IOException
    {
    }

    @Override
    protected void writeLineSeparator() throws IOException
    {
        pageSeparatorCount += 1;
        super.writeLineSeparator() ;
    }

    @Override
    protected void writeWordSeparator() throws IOException
    {
        pageSeparatorCount += 1;
        super.writeWordSeparator();
    }

    /**
     * Write a string to the output stream, maintain font state, and escape some HTML characters.
     * The font state is only preserved per word.
     *
     * @param text The text to write to the stream.
     * @param textPositions the corresponding text positions
     * @throws IOException If there is an error writing to the stream.
     */
    @Override
    protected void writeString(String text, List<TextPosition> textPositions) throws IOException
    {
        pageCharacterCount += text.length();
        // + 1;
        if (textPositions.size() > 0) {
            TextPosition textPosition = textPositions.get(0);
            float fontSizeInPt = textPosition.getHeight();
            if ((fontSizeInPt >= titleFontSizeInPt && currentFontSizeInPt < titleFontSizeInPt)
                ||
                    (fontSizeInPt >= titleFontSizeInPt && currentFontSizeInPt >= titleFontSizeInPt)
                ) {
                currentTitle = currentTitle + " " + text;
            } else if (fontSizeInPt < titleFontSizeInPt && currentFontSizeInPt >= titleFontSizeInPt) {
                if (currentTitle.length() > minTitleLengthInCharacters) {
                    currentTitles.add(new TitleInfo(currentTitle, pageCharacterCount + pageSeparatorCount));
                    // super.writeString(LINE_SEPARATOR + String.format("<title>%s</title>", currentTitle) + LINE_SEPARATOR);
                }
                // else {
                //     super.writeString(LINE_SEPARATOR + String.format("<skipped>%s</skipped>", currentTitle) + LINE_SEPARATOR);
                // }
                currentTitle = "";
            }
            currentFontSizeInPt = fontSizeInPt;
            // Check for other fonts. If useful Check for changes instead. As of now, in test document, all fonts seems to be ArialMT.
            // if (!textPosition.getFont().getName().equals("ArialMT")) {
            //    super.writeString(String.format(" %s ", textPosition.getFont().getName()));
            // }

            // super.writeString(String.format(" %f ", currentFontSizeInPt));
            // super.writeString(String.format(" %f ", textPosition.getHeight()));
            
        }
        // Print text
        // super.writeString(text.trim());
        super.writeString(text);
    }


    /**
     * Write something (if defined) at the start of a page.
     *
     * @throws IOException if something went wrong
     */
    @Override
    protected void writePageStart() throws IOException {
        output = new StringWriter();
        currentTitles = new ArrayList<TitleInfo>();
        pageCharacterCount = 0;
        pageSeparatorCount = 0;
    }

    /**
     * Write something (if defined) at the end of a page.
     *
     * @throws IOException if something went wrong
     */
    @Override
    protected void writePageEnd() throws IOException {
        String page = output.toString();
        pages.add(page);
        pageTitles.add(currentTitles);
    }
    /**
     * Writes the paragraph end "&lt;/p&gt;" to the output. Furthermore, it will also clear the font state.
     *
     * {@inheritDoc}
     */
    @Override
    protected void writeParagraphEnd() throws IOException
    {
        super.writeParagraphEnd();
    }

}
