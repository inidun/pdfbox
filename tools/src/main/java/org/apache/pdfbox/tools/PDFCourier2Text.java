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
import java.util.List;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

public class PDFCourier2Text extends PDFTextStripper {
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
    private int currentTitleStartPosition = 0;
    private int minTitleCharacterDistance = 100;
    private int pageCharacterCount = 0;
    private PDDocument document = null;
    private List<String> pages = null;
    private List<List<TitleInfo>> pageTitles = null;
    private List<TitleInfo> currentTitles = null;
    private int pageSeparatorCount = 0;

    public PDFCourier2Text(float titleFontSizeInPt, int minTitleLengthInCharacters) throws IOException {
        this.titleFontSizeInPt = titleFontSizeInPt;
        this.minTitleLengthInCharacters = minTitleLengthInCharacters;
        setLineSeparator(LINE_SEPARATOR);
        setParagraphEnd("");
        setWordSeparator(" ");
    }

    public List<String> extractText(String filename) throws IOException {
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
    protected void startDocument(PDDocument document) throws IOException {
    }

    @Override
    public void endDocument(PDDocument document) throws IOException {
    }

    @Override
    protected void writeLineSeparator() throws IOException {
        if (currentTitle != "") {
            currentTitle += getLineSeparator();
        }
        pageSeparatorCount += getLineSeparator().length();
        super.writeLineSeparator();
    }

    @Override
    protected void writeWordSeparator() throws IOException {

        if (currentTitle != "") {
            currentTitle += getWordSeparator();
        }
        pageSeparatorCount += getWordSeparator().length();
        super.writeWordSeparator();
    }

    @Override
    protected void writeCharacters(TextPosition text) throws IOException {
        pageSeparatorCount += text.getUnicode().length();
        super.writeCharacters(text);
    }

    /**
     * Write a string to the output stream, maintain font state, and escape some
     * HTML characters. The font state is only preserved per word.
     *
     * @param text          The text to write to the stream.
     * @param textPositions the corresponding text positions
     * @throws IOException If there is an error writing to the stream.
     */
    @Override
    protected void writeString(String text, List<TextPosition> textPositions) throws IOException {
        pageCharacterCount += text.length();
        // + 1;
        if (textPositions.size() > 0) {
            TextPosition textPosition = textPositions.get(0);
            float fontSizeInPt = textPosition.getHeight();
            
            

            if (fontSizeHasIncreasedAboveThreshold(fontSizeInPt)
                    || fontSizeIsStillAboveThreshold(fontSizeInPt)) {
                if (currentTitle.isEmpty()) {                    
                    if (distanceToPreviousTitlePositionAboveThreshold()) {
                        currentTitleStartPosition = pageCharacterCount - text.length();
                        currentTitle = text;
                    }
                } else {
                    currentTitle += text;
                }

            } else if (fontSizeHasDroppedBelowThreshold(fontSizeInPt)) {
                if (currentTitle.length() > minTitleLengthInCharacters) {
                    currentTitles.add(new TitleInfo(currentTitle, currentTitleStartPosition));
                }
                currentTitle = "";
            }
            currentFontSizeInPt = fontSizeInPt;

            // Check for other fonts. If useful Check for changes instead. As of now, in
            // test document, all fonts seems to be ArialMT.
            // if (!textPosition.getFont().getName().equals("ArialMT")) {
            // super.writeString(String.format(" %s ", textPosition.getFont().getName()));
            // }

            // super.writeString(String.format(" %f ", currentFontSizeInPt));
            // super.writeString(String.format(" %f ", textPosition.getHeight()));

        }
        // Print text
        // super.writeString(text.trim());
        // super.writeString(text);
        output.write(text);
    }

    private boolean distanceToPreviousTitlePositionAboveThreshold() {
        int previousTitlePosition = previousTitlePositionOnSamePage();
        if (previousTitlePosition < 0 ) {
            return true;
        }
        return pageCharacterCount - previousTitlePosition >= minTitleCharacterDistance;
    }

    private boolean fontSizeHasDroppedBelowThreshold(float fontSizeInPt) {
        return fontSizeInPt < titleFontSizeInPt && currentFontSizeInPt >= titleFontSizeInPt;
    }

    private boolean fontSizeIsStillAboveThreshold(float fontSizeInPt) {
        return fontSizeInPt >= titleFontSizeInPt && currentFontSizeInPt >= titleFontSizeInPt;
    }

    private boolean fontSizeHasIncreasedAboveThreshold(float fontSizeInPt) {
        return fontSizeInPt >= titleFontSizeInPt && currentFontSizeInPt < titleFontSizeInPt;
    }

    private int previousTitlePositionOnSamePage() {
        if (currentTitles.size() > 0) {
            return currentTitles.get(currentTitles.size() - 1).position;
        }
        return -1;
    }

    @Override
    protected void writeString(String text) throws IOException
    {
        if (currentTitle != "") {
            currentTitle += text;
        }
        output.write(text);
    }

    
    @Override
    protected void writePageStart() throws IOException {
        output = new StringWriter();
        currentTitles = new ArrayList<TitleInfo>();
        pageCharacterCount = 0;
        pageSeparatorCount = 0;
        currentTitle = "";
        currentTitleStartPosition = 0;
    }

    @Override
    protected void writePageEnd() throws IOException {
        String page = output.toString();
        pages.add(page);
        pageTitles.add(currentTitles);

        pageSeparatorCount += getLineSeparator().length();
        super.writePageEnd();
    }

    @Override
    protected void writeParagraphEnd() throws IOException {
        pageSeparatorCount += getParagraphEnd().length();
        super.writeParagraphEnd();
    }

}
