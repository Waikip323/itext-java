/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.kernel.utils;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.xml.sax.SAXException;
import static org.junit.Assert.assertNull;

@Category(IntegrationTest.class)
public class PdfMergerTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/utils/PdfMergerTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/utils/PdfMergerTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void mergeDocumentTest01() throws IOException, InterruptedException {
        String filename = sourceFolder + "courierTest.pdf";
        String filename1 = sourceFolder + "helveticaTest.pdf";
        String filename2 = sourceFolder + "timesRomanTest.pdf";
        String resultFile = destinationFolder + "mergedResult01.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfReader reader1 = new PdfReader(filename1);
        PdfReader reader2 = new PdfReader(filename2);

        FileOutputStream fos1 = new FileOutputStream(resultFile);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc = new PdfDocument(reader);
        PdfDocument pdfDoc1 = new PdfDocument(reader1);
        PdfDocument pdfDoc2 = new PdfDocument(reader2);
        PdfDocument pdfDoc3 = new PdfDocument(writer1);

        PdfMerger merger = new PdfMerger(pdfDoc3).setCloseSourceDocuments(true);
        merger.merge(pdfDoc, 1, 1);
        merger.merge(pdfDoc1, 1, 1);

        merger.merge(pdfDoc2, 1, 1);

        pdfDoc3.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(resultFile, sourceFolder + "cmp_mergedResult01.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY)
    })
    public void mergeDocumentOutlinesWithNullDestinationTest01() throws IOException, InterruptedException {
        String resultFile = destinationFolder + "mergeDocumentOutlinesWithNullDestinationTest01.pdf";
        String filename = sourceFolder + "null_dest_outline.pdf";
        PdfDocument sourceDocument = new PdfDocument(new PdfReader(filename));

        PdfMerger resultDocument = new PdfMerger(new PdfDocument(new PdfWriter(resultFile)));
        resultDocument.merge(sourceDocument, 1, 1);
        resultDocument.close();
        sourceDocument.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(resultFile, sourceFolder + "cmp_mergeDocumentOutlinesWithNullDestinationTest01.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void mergeDocumentTest02() throws IOException, InterruptedException {
        String filename = sourceFolder + "doc1.pdf";
        String filename1 = sourceFolder + "doc2.pdf";
        String filename2 = sourceFolder + "doc3.pdf";
        String resultFile = destinationFolder + "mergedResult02.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfReader reader1 = new PdfReader(filename1);
        PdfReader reader2 = new PdfReader(filename2);

        FileOutputStream fos1 = new FileOutputStream(resultFile);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc = new PdfDocument(reader);
        PdfDocument pdfDoc1 = new PdfDocument(reader1);
        PdfDocument pdfDoc2 = new PdfDocument(reader2);
        PdfDocument pdfDoc3 = new PdfDocument(writer1);
        PdfMerger merger = new PdfMerger(pdfDoc3).setCloseSourceDocuments(true);

        merger.merge(pdfDoc, 1, 1).merge(pdfDoc1, 1, 1).merge(pdfDoc2, 1, 1).close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(resultFile, sourceFolder + "cmp_mergedResult02.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY)
    })
    public void mergeDocumentTest03() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        String filename = sourceFolder + "pdf_open_parameters.pdf";
        String filename1 = sourceFolder + "iphone_user_guide.pdf";
        String resultFile = destinationFolder + "mergedResult03.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfReader reader1 = new PdfReader(filename1);

        FileOutputStream fos1 = new FileOutputStream(resultFile);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc = new PdfDocument(reader);
        PdfDocument pdfDoc1 = new PdfDocument(reader1);
        PdfDocument pdfDoc3 = new PdfDocument(writer1);
        pdfDoc3.setTagged();

        new PdfMerger(pdfDoc3)
                .merge(pdfDoc, 2, 2)
                .merge(pdfDoc1, 7, 8)
                .close();

        pdfDoc.close();
        pdfDoc1.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = "";
        String contentErrorMessage = compareTool.compareByContent(resultFile, sourceFolder + "cmp_mergedResult03.pdf", destinationFolder, "diff_");
        String tagStructErrorMessage = compareTool.compareTagStructures(resultFile, sourceFolder + "cmp_mergedResult03.pdf");

        errorMessage += tagStructErrorMessage == null ? "" : tagStructErrorMessage + "\n";
        errorMessage += contentErrorMessage == null ? "" : contentErrorMessage;
        if (!errorMessage.isEmpty()) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY),
            @LogMessage(messageTemplate = LogMessageConstant.CREATED_ROOT_TAG_HAS_MAPPING, count = 2)
    })
    public void mergeDocumentTest04() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        String filename = sourceFolder + "pdf_open_parameters.pdf";
        String filename1 = sourceFolder + "iphone_user_guide.pdf";
        String resultFile = destinationFolder + "mergedResult04.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfReader reader1 = new PdfReader(filename1);

        FileOutputStream fos1 = new FileOutputStream(resultFile);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc = new PdfDocument(reader);
        PdfDocument pdfDoc1 = new PdfDocument(reader1);
        PdfDocument pdfDoc3 = new PdfDocument(writer1);
        pdfDoc3.setTagged();

        PdfMerger merger = new PdfMerger(pdfDoc3).setCloseSourceDocuments(true);
        List<Integer> pages = new ArrayList<>();
        pages.add(3);
        pages.add(2);
        pages.add(1);
        merger.merge(pdfDoc, pages);

        List<Integer> pages1 = new ArrayList<>();
        pages1.add(5);
        pages1.add(9);
        pages1.add(4);
        pages1.add(3);
        merger.merge(pdfDoc1, pages1);

        merger.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = "";
        String contentErrorMessage = compareTool.compareByContent(resultFile, sourceFolder + "cmp_mergedResult04.pdf", destinationFolder, "diff_");
        String tagStructErrorMessage = compareTool.compareTagStructures(resultFile, sourceFolder + "cmp_mergedResult04.pdf");

        errorMessage += tagStructErrorMessage == null ? "" : tagStructErrorMessage + "\n";
        errorMessage += contentErrorMessage == null ? "" : contentErrorMessage;
        if (!errorMessage.isEmpty()) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void mergeTableWithEmptyTdTest() throws IOException, ParserConfigurationException, SAXException {
        String filename = sourceFolder + "tableWithEmptyTd.pdf";
        String resultFile = destinationFolder + "tableWithEmptyTdResult.pdf";

        PdfReader reader = new PdfReader(filename);

        PdfDocument sourceDoc = new PdfDocument(reader);
        PdfDocument output = new PdfDocument(new PdfWriter(resultFile));
        output.setTagged();
        PdfMerger merger = new PdfMerger(output).setCloseSourceDocuments(true);
        merger.merge(sourceDoc, 1, sourceDoc.getNumberOfPages());
        sourceDoc.close();
        reader.close();
        merger.close();
        output.close();

        CompareTool compareTool = new CompareTool();
        String tagStructErrorMessage = compareTool.compareTagStructures(resultFile, sourceFolder + "cmp_tableWithEmptyTd.pdf");

        String errorMessage = tagStructErrorMessage == null ? "" : tagStructErrorMessage + "\n";
        if (!errorMessage.isEmpty()) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.NAME_ALREADY_EXISTS_IN_THE_NAME_TREE, count = 2)})
    public void mergeOutlinesNamedDestinations() throws IOException, InterruptedException {
        String filename = sourceFolder + "outlinesNamedDestinations.pdf";
        String resultFile = destinationFolder + "mergeOutlinesNamedDestinations.pdf";

        PdfReader reader = new PdfReader(filename);

        PdfDocument sourceDoc = new PdfDocument(reader);
        PdfDocument output = new PdfDocument(new PdfWriter(resultFile));
        PdfMerger merger = new PdfMerger(output).setCloseSourceDocuments(false);
        merger.merge(sourceDoc, 2, 3);
        merger.merge(sourceDoc, 2, 3);
        sourceDoc.close();
        reader.close();
        merger.close();
        output.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(resultFile, sourceFolder + "cmp_mergeOutlinesNamedDestinations.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    // TODO DEVSIX-1743. Update cmp file after fix
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY)
    })
    public void mergeWithAcroFormsTest() throws IOException, InterruptedException {
        String pdfAcro1 = sourceFolder + "pdfSource1.pdf";
        String pdfAcro2 = sourceFolder + "pdfSource2.pdf";
        String outFileName = destinationFolder + "mergeWithAcroFormsTest.pdf";
        String cmpFileName= sourceFolder + "cmp_mergeWithAcroFormsTest.pdf";

        List<File> sources = new ArrayList<File>();
        sources.add(new File(pdfAcro1));
        sources.add(new File(pdfAcro2));
        mergePdfs(sources, outFileName);

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.DOCUMENT_HAS_CONFLICTING_OCG_NAMES, count = 3)
    })
    public void mergePdfWithOCGTest() throws IOException, InterruptedException {
        String pdfWithOCG1 = sourceFolder  + "sourceOCG1.pdf";
        String pdfWithOCG2 = sourceFolder  + "sourceOCG2.pdf";
        String outPdf = destinationFolder + "mergePdfWithOCGTest.pdf";
        String cmpPdf = sourceFolder + "cmp_mergePdfWithOCGTest.pdf";

        List<File> sources = new ArrayList<File>();
        sources.add(new File(pdfWithOCG1));
        sources.add(new File(pdfWithOCG2));
        sources.add(new File(pdfWithOCG2));
        sources.add(new File(pdfWithOCG2));
        mergePdfs(sources, outPdf);

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.DOCUMENT_HAS_CONFLICTING_OCG_NAMES)
    })
    public void mergePdfWithComplexOCGTest() throws IOException, InterruptedException {
        String pdfWithOCG1 = sourceFolder  + "sourceOCG1.pdf";
        String pdfWithOCG2 = sourceFolder  + "pdfWithComplexOCG.pdf";
        String outPdf = destinationFolder + "mergePdfWithComplexOCGTest.pdf";
        String cmpPdf = sourceFolder + "cmp_mergePdfWithComplexOCGTest.pdf";

        List<File> sources = new ArrayList<File>();
        sources.add(new File(pdfWithOCG1));
        sources.add(new File(pdfWithOCG2));
        mergePdfs(sources, outPdf);

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.DOCUMENT_HAS_CONFLICTING_OCG_NAMES)
    })
    public void mergeTwoPagePdfWithComplexOCGTest() throws IOException, InterruptedException {
        String pdfWithOCG1 = sourceFolder  + "sourceOCG1.pdf";
        String pdfWithOCG2 = sourceFolder  + "twoPagePdfWithComplexOCGTest.pdf";
        String outPdf = destinationFolder + "mergeTwoPagePdfWithComplexOCGTest.pdf";
        String cmpPdf = sourceFolder + "cmp_mergeTwoPagePdfWithComplexOCGTest.pdf";

        PdfDocument mergedDoc = new PdfDocument(new PdfWriter(outPdf));
        PdfMerger merger = new PdfMerger(mergedDoc);
        List<File> sources = new ArrayList<File>();
        sources.add(new File(pdfWithOCG1));
        sources.add(new File(pdfWithOCG2));

        // The test verifies that are copying only those OCGs and properties that are used on the copied pages
        for(File source : sources){
            PdfDocument sourcePdf = new PdfDocument(new PdfReader(source));
            merger.merge(sourcePdf, 1, 1).setCloseSourceDocuments(true);
            sourcePdf.close();
        }
        merger.close();
        mergedDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder));
    }

    @Test
    public void mergePdfWithComplexOCGTwiceTest() throws IOException, InterruptedException {
        String pdfWithOCG = sourceFolder  + "pdfWithComplexOCG.pdf";
        String outPdf = destinationFolder + "mergePdfWithComplexOCGTwiceTest.pdf";
        String cmpPdf = sourceFolder + "cmp_mergePdfWithComplexOCGTwiceTest.pdf";

        PdfDocument mergedDoc = new PdfDocument(new PdfWriter(outPdf));
        PdfMerger merger = new PdfMerger(mergedDoc);
        PdfDocument sourcePdf = new PdfDocument(new PdfReader(new File(pdfWithOCG)));
        // The test verifies that identical layers from the same document are not copied
        merger.merge(sourcePdf, 1, sourcePdf.getNumberOfPages());
        merger.merge(sourcePdf, 1, sourcePdf.getNumberOfPages());
        sourcePdf.close();
        merger.close();
        mergedDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder));
    }

    @Test
    @Ignore ("TODO: DEVSIX-5064 (when doing merge with outlines infinite loop occurs )")
    public void mergeOutlinesWithWrongStructureTest() throws IOException, InterruptedException {
        PdfDocument inputDoc = new PdfDocument(new PdfReader(
                sourceFolder + "infiniteLoopInOutlineStructure.pdf"));

        PdfDocument outputDoc = new PdfDocument(new PdfWriter(
                destinationFolder + "infiniteLoopInOutlineStructure.pdf"));

        PdfMerger merger = new PdfMerger(outputDoc, false, true);
        System.out.println("Doing merge");
        merger.merge(inputDoc, 1, 2);
        merger.close();
        System.out.println("Merge done");

        Assert.assertNull(new CompareTool().compareByContent(
                destinationFolder + "infiniteLoopInOutlineStructure.pdf",
                sourceFolder + "cmp_infiniteLoopInOutlineStructure.pdf", destinationFolder));
    }

    private void mergePdfs(List<File> sources, String destination) throws IOException {
        PdfDocument mergedDoc = new PdfDocument(new PdfWriter(destination));
        PdfMerger merger = new PdfMerger(mergedDoc);
        for(File source : sources){
            PdfDocument sourcePdf = new PdfDocument(new PdfReader(source));
            merger.merge(sourcePdf, 1, sourcePdf.getNumberOfPages()).setCloseSourceDocuments(true);
            sourcePdf.close();
        }

        merger.close();
        mergedDoc.close();
    }
}
