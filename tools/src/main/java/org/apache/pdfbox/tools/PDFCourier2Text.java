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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFontDescriptor;
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

    private static final int INITIAL_PDF_TO_HTML_BYTES = 8192;

    /**
     * Constructor.
     * @throws IOException If there is an error during initialization.
     */
    public PDFCourier2Text() throws IOException
    {
        setLineSeparator(LINE_SEPARATOR);
        // setParagraphStart("<p>");
        setParagraphEnd(LINE_SEPARATOR);
        // setPageStart("<page>");
        // setPageEnd("</page>"+ LINE_SEPARATOR);
    }

    @Override
    protected void startDocument(PDDocument document) throws IOException
    {
        StringBuilder buf = new StringBuilder(INITIAL_PDF_TO_HTML_BYTES);
        buf.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"" + "\n"
                + "\"http://www.w3.org/TR/html4/loose.dtd\">\n");

        buf.append("<ourier>");

        super.writeString(buf.toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void endDocument(PDDocument document) throws IOException
    {
        super.writeString("</courier>");
    }

    /**
     * This method will attempt to guess the title of the document using
     * either the document properties or the first lines of text.
     *
     * @return returns the title.
     */
    protected String getTitle()
    {
        String titleGuess = document.getDocumentInformation().getTitle();
        if(titleGuess != null && titleGuess.length() > 0)
        {
            return titleGuess;
        }
        else
        {
            Iterator<List<TextPosition>> textIter = getCharactersByArticle().iterator();
            float lastFontSize = -1.0f;

            StringBuilder titleText = new StringBuilder();
            while (textIter.hasNext())
            {
                for (TextPosition position : textIter.next())
                {
                    float currentFontSize = position.getFontSize();
                    //If we're past 64 chars we will assume that we're past the title
                    //64 is arbitrary
                    if (Float.compare(currentFontSize, lastFontSize) != 0 || titleText.length() > 64)
                    {
                        if (titleText.length() > 0)
                        {
                            return titleText.toString();
                        }
                        lastFontSize = currentFontSize;
                    }
                    if (currentFontSize > 13.0f)
                    { // most body text is 12pt
                        titleText.append(position.getUnicode());
                    }
                }
            }
        }
        return "";
    }

    private float titleFontSizeInPt = 5.5f;
    private float currentFontSizeInPt = 0f;
    private int minTitleLengthInCharacters =  8;
    private String currentTitle = "";
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

        // if (textPositions.size() > 0) {
        //     TextPosition textPosition = textPositions.get(0);
        //     float fontDelta = Math.abs(textPosition.getFontSizeInPt() - currentFontSizeInPt);
        //     currentFontSizeInPt = textPosition.getFontSizeInPt();
        //     if (fontDelta > 2.0) {
        //         super.writeString(String.format("# FONT %f ", currentFontSizeInPt));
        //     }
        // }

        if (textPositions.size() > 0) {
            TextPosition textPosition = textPositions.get(0);
            float fontSizeInPt = textPosition.getHeight();
            if ((fontSizeInPt >= titleFontSizeInPt && currentFontSizeInPt < titleFontSizeInPt)
                ||
                    (fontSizeInPt >= titleFontSizeInPt && currentFontSizeInPt >= titleFontSizeInPt)
                ) {
                currentTitle = currentTitle + " " + text;
                // super.writeString(LINE_SEPARATOR + String.format("TITLE START: font %f ", fontSizeInPt) + LINE_SEPARATOR);
            } else if (fontSizeInPt < titleFontSizeInPt && currentFontSizeInPt >= titleFontSizeInPt) {
                if (currentTitle.length() > minTitleLengthInCharacters) {
                    super.writeString(LINE_SEPARATOR + String.format("<title>%s</title>", currentTitle) + LINE_SEPARATOR);
                } else {
                    super.writeString(LINE_SEPARATOR + String.format("<skipped>%s</skipped>", currentTitle) + LINE_SEPARATOR);
                }
                currentTitle = "";
                // super.writeString(LINE_SEPARATOR + String.format("TITLE END: font %f ", fontSizeInPt) + LINE_SEPARATOR);
            }
            currentFontSizeInPt = fontSizeInPt;
            // super.writeString(String.format(" %f ", currentFontSizeInPt));
            super.writeString(String.format(" %f ", textPosition.getHeight()));
        }
        super.writeString(text);
    }

    /**
     * Write a string to the output stream and escape some HTML characters.
     *
     * @param chars String to be written to the stream
     * @throws IOException
     *             If there is an error writing to the stream.
     */
    @Override
    protected void writeString(String chars) throws IOException
    {
        super.writeString(chars);
    }

    /**
     * Write something (if defined) at the start of a page.
     *
     * @throws IOException if something went wrong
     */
    @Override
    protected void writePageStart() throws IOException {
        super.writeString(LINE_SEPARATOR+String.format("<page number=\"%d\">", getCurrentPageNo()));
    }

    /**
     * Write something (if defined) at the end of a page.
     *
     * @throws IOException if something went wrong
     */
    @Override
    protected void writePageEnd() throws IOException {
        super.writeString("</page>");
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
