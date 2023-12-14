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
package com.itextpdf.kernel.pdf;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.navigation.PdfDestination;
import com.itextpdf.kernel.pdf.navigation.PdfExplicitDestination;
import com.itextpdf.kernel.pdf.navigation.PdfStringDestination;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfOutlineTest extends ExtendedITextTest {

    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/pdf/PdfOutlineTest/";
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/kernel/pdf/PdfOutlineTest/";

    @BeforeClass
    public static void before() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void createSimpleDocWithOutlines() throws IOException, InterruptedException {
        String filename = "simpleDocWithOutlines.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(DESTINATION_FOLDER + filename));
        pdfDoc.getCatalog().setPageMode(PdfName.UseOutlines);

        PdfPage firstPage = pdfDoc.addNewPage();
        PdfPage secondPage = pdfDoc.addNewPage();

        PdfOutline rootOutline = pdfDoc.getOutlines(false);
        PdfOutline firstOutline = rootOutline.addOutline("First Page");
        PdfOutline secondOutline = rootOutline.addOutline("Second Page");
        firstOutline.addDestination(PdfExplicitDestination.createFit(firstPage));
        secondOutline.addDestination(PdfExplicitDestination.createFit(secondPage));

        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(DESTINATION_FOLDER + filename, SOURCE_FOLDER + "cmp_" + filename,
                DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void outlinesTest() throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(SOURCE_FOLDER + "iphone_user_guide.pdf"));
        PdfOutline outlines = pdfDoc.getOutlines(false);
        List<PdfOutline> children = outlines.getAllChildren().get(0).getAllChildren();

        Assert.assertEquals(outlines.getTitle(), "Outlines");
        Assert.assertEquals(children.size(), 13);
        Assert.assertTrue(children.get(0).getDestination() instanceof PdfStringDestination);
    }

    @Test
    public void outlinesWithPagesTest() throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(SOURCE_FOLDER + "iphone_user_guide.pdf"));
        PdfPage page = pdfDoc.getPage(52);
        List<PdfOutline> pageOutlines = page.getOutlines(true);
        try {
            Assert.assertEquals(3, pageOutlines.size());
            Assert.assertTrue(pageOutlines.get(0).getTitle().equals("Safari"));
            Assert.assertEquals(pageOutlines.get(0).getAllChildren().size(), 4);
        } finally {
            pdfDoc.close();
        }
    }

    @Test
    public void addOutlinesToDocumentTest() throws IOException, InterruptedException {
        PdfReader reader = new PdfReader(SOURCE_FOLDER + "iphone_user_guide.pdf");
        String filename = "addOutlinesToDocumentTest.pdf";
        PdfWriter writer = new PdfWriter(DESTINATION_FOLDER + filename);
        PdfDocument pdfDoc = new PdfDocument(reader, writer);
        pdfDoc.setTagged();

        PdfOutline outlines = pdfDoc.getOutlines(false);

        PdfOutline firstPage = outlines.addOutline("firstPage");
        PdfOutline firstPageChild = firstPage.addOutline("firstPageChild");
        PdfOutline secondPage = outlines.addOutline("secondPage");
        PdfOutline secondPageChild = secondPage.addOutline("secondPageChild");
        firstPage.addDestination(PdfExplicitDestination.createFit(pdfDoc.getPage(1)));
        firstPageChild.addDestination(PdfExplicitDestination.createFit(pdfDoc.getPage(1)));
        secondPage.addDestination(PdfExplicitDestination.createFit(pdfDoc.getPage(2)));
        secondPageChild.addDestination(PdfExplicitDestination.createFit(pdfDoc.getPage(2)));
        outlines.getAllChildren().get(0).getAllChildren().get(1).addOutline("testOutline", 1).addDestination(PdfExplicitDestination.createFit(pdfDoc.getPage(102)));

        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(DESTINATION_FOLDER + filename, SOURCE_FOLDER + "cmp_" + filename,
                DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void readOutlinesFromDocumentTest() throws IOException {
        String filename = SOURCE_FOLDER + "addOutlinesResult.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename));
        PdfOutline outlines = pdfDoc.getOutlines(false);
        try {
            Assert.assertEquals(3, outlines.getAllChildren().size());
            Assert.assertEquals("firstPageChild", outlines.getAllChildren().get(1).getAllChildren().get(0).getTitle());
        } finally {
            pdfDoc.close();
        }
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.FLUSHED_OBJECT_CONTAINS_FREE_REFERENCE, count = 36))
    // TODO DEVSIX-1643: destinations are not removed along with page
    public void removePageWithOutlinesTest() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        String filename = "removePageWithOutlinesTest.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(SOURCE_FOLDER + "iphone_user_guide.pdf"), new PdfWriter(
                DESTINATION_FOLDER + filename));
        // TODO DEVSIX-1643 (this causes log message errors. It's because of destinations pointing to removed page (freed reference, replaced by PdfNull))
        pdfDoc.removePage(102);

        pdfDoc.close();
        CompareTool compareTool = new CompareTool();
        String diffContent = compareTool.compareByContent(DESTINATION_FOLDER + filename, SOURCE_FOLDER + "cmp_" + filename,
                DESTINATION_FOLDER, "diff_");
        String diffTags = compareTool.compareTagStructures(DESTINATION_FOLDER + filename, SOURCE_FOLDER + "cmp_" + filename);
        if (diffContent != null || diffTags != null) {
            diffContent = diffContent != null ? diffContent : "";
            diffTags = diffTags != null ? diffTags : "";
            Assert.fail(diffContent + diffTags);
        }
    }

    @Test
    public void readRemovedPageWithOutlinesTest() throws IOException {
        // TODO DEVSIX-1643: src document is taken from the previous removePageWithOutlinesTest test, however it contains numerous destination objects which contain PdfNull instead of page reference
        String filename = SOURCE_FOLDER + "removePagesWithOutlinesResult.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename));

        PdfPage page = pdfDoc.getPage(102);
        List<PdfOutline> pageOutlines = page.getOutlines(false);
        try {
            Assert.assertEquals(4, pageOutlines.size());
        } finally {
            pdfDoc.close();
        }
    }

    @Test
    public void updateOutlineTitle() throws IOException, InterruptedException {
        PdfReader reader = new PdfReader(SOURCE_FOLDER + "iphone_user_guide.pdf");
        String filename = "updateOutlineTitle.pdf";
        PdfWriter writer = new PdfWriter(DESTINATION_FOLDER + filename);
        PdfDocument pdfDoc = new PdfDocument(reader, writer);

        PdfOutline outlines = pdfDoc.getOutlines(false);
        outlines.getAllChildren().get(0).getAllChildren().get(1).setTitle("New Title");

        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(DESTINATION_FOLDER + filename, SOURCE_FOLDER + "cmp_" + filename,
                DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void getOutlinesInvalidParentLink() throws IOException, InterruptedException {
        PdfReader reader = new PdfReader(SOURCE_FOLDER + "outlinesInvalidParentLink.pdf");
        String filename = "updateOutlineTitleInvalidParentLink.pdf";
        PdfWriter writer = new PdfWriter(DESTINATION_FOLDER + filename);
        PdfDocument pdfDoc = new PdfDocument(reader, writer);

        Assert.assertThrows(NullPointerException.class,
                () -> pdfDoc.getOutlines(false)
        );
    }

    @Test
    public void readOutlineTitle() throws IOException {
        String filename = SOURCE_FOLDER + "updateOutlineTitleResult.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename));

        PdfOutline outlines = pdfDoc.getOutlines(false);
        PdfOutline outline = outlines.getAllChildren().get(0).getAllChildren().get(1);
        try {
            Assert.assertEquals("New Title", outline.getTitle());
        } finally {
            pdfDoc.close();
        }
    }

    @Test
    public void addOutlineInNotOutlineMode() throws IOException, InterruptedException {
        String filename = "addOutlineInNotOutlineMode.pdf";
        PdfReader reader = new PdfReader(SOURCE_FOLDER + "iphone_user_guide.pdf");
        PdfWriter writer = new PdfWriter(DESTINATION_FOLDER + filename);
        PdfDocument pdfDoc = new PdfDocument(reader, writer);

        PdfOutline outlines = new PdfOutline(pdfDoc);

        PdfOutline firstPage = outlines.addOutline("firstPage");
        PdfOutline firstPageChild = firstPage.addOutline("firstPageChild");
        PdfOutline secondPage = outlines.addOutline("secondPage");
        PdfOutline secondPageChild = secondPage.addOutline("secondPageChild");
        firstPage.addDestination(PdfExplicitDestination.createFit(pdfDoc.getPage(1)));
        firstPageChild.addDestination(PdfExplicitDestination.createFit(pdfDoc.getPage(1)));
        secondPage.addDestination(PdfExplicitDestination.createFit(pdfDoc.getPage(2)));
        secondPageChild.addDestination(PdfExplicitDestination.createFit(pdfDoc.getPage(2)));

        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(DESTINATION_FOLDER + filename, SOURCE_FOLDER + "cmp_" + filename,
                DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void readOutlineAddedInNotOutlineMode() throws IOException {
        String filename = SOURCE_FOLDER + "addOutlinesWithoutOutlineModeResult.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename));

        List<PdfOutline> pageOutlines = pdfDoc.getPage(102).getOutlines(true);
        try {
            Assert.assertEquals(5, pageOutlines.size());
        } finally {
            pdfDoc.close();
        }
    }

    @Test
    public void createDocWithOutlines() throws IOException {
        String filename = SOURCE_FOLDER + "documentWithOutlines.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename));

        PdfOutline outlines = pdfDoc.getOutlines(false);
        try {
            Assert.assertEquals(2, outlines.getAllChildren().size());
            Assert.assertEquals("First Page", outlines.getAllChildren().get(0).getTitle());
        } finally {
            pdfDoc.close();
        }
    }


    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY)
    })
    public void copyPagesWithOutlines() throws IOException {
        PdfReader reader = new PdfReader(SOURCE_FOLDER + "iphone_user_guide.pdf");
        PdfWriter writer = new PdfWriter(DESTINATION_FOLDER + "copyPagesWithOutlines01.pdf");

        PdfDocument pdfDoc = new PdfDocument(reader);
        PdfDocument pdfDoc1 = new PdfDocument(writer);

        List<Integer> pages = new ArrayList<>();
        pages.add(1);
        pages.add(2);
        pages.add(3);
        pages.add(5);
        pages.add(52);
        pages.add(102);
        pdfDoc1.initializeOutlines();
        pdfDoc.copyPagesTo(pages, pdfDoc1);
        pdfDoc.close();

        Assert.assertEquals(6, pdfDoc1.getNumberOfPages());
        Assert.assertEquals(4, pdfDoc1.getOutlines(false).getAllChildren().get(0).getAllChildren().size());
        pdfDoc1.close();
    }

    @Test
    public void addOutlinesWithNamedDestinations01() throws IOException, InterruptedException {
        String filename = DESTINATION_FOLDER + "outlinesWithNamedDestinations01.pdf";

        PdfReader reader = new PdfReader(SOURCE_FOLDER + "iphone_user_guide.pdf");
        PdfWriter writer = new PdfWriter(filename);

        PdfDocument pdfDoc = new PdfDocument(reader, writer);
        PdfArray array1 = new PdfArray();
        array1.add(pdfDoc.getPage(2).getPdfObject());
        array1.add(PdfName.XYZ);
        array1.add(new PdfNumber(36));
        array1.add(new PdfNumber(806));
        array1.add(new PdfNumber(0));

        PdfArray array2 = new PdfArray();
        array2.add(pdfDoc.getPage(3).getPdfObject());
        array2.add(PdfName.XYZ);
        array2.add(new PdfNumber(36));
        array2.add(new PdfNumber(806));
        array2.add(new PdfNumber(1.25));

        PdfArray array3 = new PdfArray();
        array3.add(pdfDoc.getPage(4).getPdfObject());
        array3.add(PdfName.XYZ);
        array3.add(new PdfNumber(36));
        array3.add(new PdfNumber(806));
        array3.add(new PdfNumber(1));

        pdfDoc.addNamedDestination("test1", array2);
        pdfDoc.addNamedDestination("test2", array3);
        pdfDoc.addNamedDestination("test3", array1);

        PdfOutline root = pdfDoc.getOutlines(false);

        PdfOutline firstOutline = root.addOutline("Test1");
        firstOutline.addDestination(PdfDestination.makeDestination(new PdfString("test1")));
        PdfOutline secondOutline = root.addOutline("Test2");
        secondOutline.addDestination(PdfDestination.makeDestination(new PdfString("test2")));
        PdfOutline thirdOutline = root.addOutline("Test3");
        thirdOutline.addDestination(PdfDestination.makeDestination(new PdfString("test3")));
        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(filename, SOURCE_FOLDER + "cmp_outlinesWithNamedDestinations01.pdf",
                DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void addOutlinesWithNamedDestinations02() throws IOException, InterruptedException {
        String filename = DESTINATION_FOLDER + "outlinesWithNamedDestinations02.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));
        PdfArray array1 = new PdfArray();
        array1.add(pdfDoc.addNewPage().getPdfObject());
        array1.add(PdfName.XYZ);
        array1.add(new PdfNumber(36));
        array1.add(new PdfNumber(806));
        array1.add(new PdfNumber(0));

        PdfArray array2 = new PdfArray();
        array2.add(pdfDoc.addNewPage().getPdfObject());
        array2.add(PdfName.XYZ);
        array2.add(new PdfNumber(36));
        array2.add(new PdfNumber(806));
        array2.add(new PdfNumber(1.25));

        PdfArray array3 = new PdfArray();
        array3.add(pdfDoc.addNewPage().getPdfObject());
        array3.add(PdfName.XYZ);
        array3.add(new PdfNumber(36));
        array3.add(new PdfNumber(806));
        array3.add(new PdfNumber(1));

        pdfDoc.addNamedDestination("page1", array2);
        pdfDoc.addNamedDestination("page2", array3);
        pdfDoc.addNamedDestination("page3", array1);

        PdfOutline root = pdfDoc.getOutlines(false);
        PdfOutline firstOutline = root.addOutline("Test1");
        firstOutline.addDestination(PdfDestination.makeDestination(new PdfString("page1")));
        PdfOutline secondOutline = root.addOutline("Test2");
        secondOutline.addDestination(PdfDestination.makeDestination(new PdfString("page2")));
        PdfOutline thirdOutline = root.addOutline("Test3");
        thirdOutline.addDestination(PdfDestination.makeDestination(new PdfString("page3")));
        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(filename, SOURCE_FOLDER + "cmp_outlinesWithNamedDestinations02.pdf",
                DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void outlineStackOverflowTest01() throws IOException {
        PdfReader reader = new PdfReader(SOURCE_FOLDER + "outlineStackOverflowTest01.pdf");
        PdfDocument pdfDoc = new PdfDocument(reader);

        try {
            pdfDoc.getOutlines(true);
        } catch (StackOverflowError e) {
            Assert.fail("StackOverflow thrown when reading document with a large number of outlines.");
        }
    }

    @Test
    public void outlineTypeNull() throws IOException, InterruptedException {
        String filename = "outlineTypeNull";
        String outputFile = DESTINATION_FOLDER + filename + ".pdf";
        PdfReader reader = new PdfReader(SOURCE_FOLDER + filename + ".pdf");
        PdfWriter writer = new PdfWriter(new FileOutputStream(outputFile));
        PdfDocument pdfDoc = new PdfDocument(reader, writer);
        pdfDoc.removePage(3);
        pdfDoc.close();
        Assert.assertNull(new CompareTool().compareByContent(outputFile, SOURCE_FOLDER + "cmp_" + filename + ".pdf",
                DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void removeAllOutlinesTest() throws IOException, InterruptedException {
        String filename = "iphone_user_guide_removeAllOutlinesTest.pdf";
        String input = SOURCE_FOLDER + "iphone_user_guide.pdf";
        String output = DESTINATION_FOLDER + "cmp_" + filename;
        String cmp = SOURCE_FOLDER + "cmp_" + filename;
        PdfReader reader = new PdfReader(input);
        PdfWriter writer = new PdfWriter(output);
        PdfDocument pdfDocument = new PdfDocument(reader, writer);
        pdfDocument.getOutlines(true).removeOutline();
        pdfDocument.close();

        Assert.assertNull(new CompareTool().compareByContent(output, cmp, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void removeOneOutlineTest() throws IOException, InterruptedException {
        String filename = "removeOneOutline.pdf";
        String input = SOURCE_FOLDER + "outlineTree.pdf";
        String output = DESTINATION_FOLDER + "cmp_" + filename;
        String cmp = SOURCE_FOLDER + "cmp_" + filename;
        PdfReader reader = new PdfReader(input);
        PdfWriter writer = new PdfWriter(output);
        PdfDocument pdfDocument = new PdfDocument(reader, writer);
        PdfOutline root = pdfDocument.getOutlines(true);
        PdfOutline toRemove = root.getAllChildren().get(2);
        toRemove.removeOutline();
        pdfDocument.close();

        Assert.assertNull(new CompareTool().compareByContent(output, cmp, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void testReinitializingOutlines() throws IOException {
        String input = SOURCE_FOLDER + "outlineTree.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(input));
        PdfOutline root = pdfDocument.getOutlines(false);
        Assert.assertEquals(4, root.getAllChildren().size());
        pdfDocument.getCatalog().getPdfObject().remove(PdfName.Outlines);
        root = pdfDocument.getOutlines(true);
        Assert.assertNull(root);
        pdfDocument.close();
    }

    @Test
    public void removePageInDocWithSimpleOutlineTreeStructTest() throws IOException, InterruptedException {
        String input = SOURCE_FOLDER + "simpleOutlineTreeStructure.pdf";
        String output = DESTINATION_FOLDER + "simpleOutlineTreeStructure.pdf";
        String cmp = SOURCE_FOLDER + "cmp_simpleOutlineTreeStructure.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(input), new PdfWriter(output));
        pdfDocument.removePage(2);
        Assert.assertEquals(2, pdfDocument.getNumberOfPages());

        pdfDocument.close();

        Assert.assertNull(new CompareTool().compareByContent(output, cmp, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void removePageInDocWithComplexOutlineTreeStructTest() throws IOException, InterruptedException {
        String input = SOURCE_FOLDER + "complexOutlineTreeStructure.pdf";
        String output = DESTINATION_FOLDER + "complexOutlineTreeStructure.pdf";
        String cmp = SOURCE_FOLDER + "cmp_complexOutlineTreeStructure.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(input), new PdfWriter(output));
        pdfDocument.removePage(2);
        Assert.assertEquals(2, pdfDocument.getNumberOfPages());

        pdfDocument.close();

        Assert.assertNull(new CompareTool().compareByContent(output, cmp, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void constructOutlinesNoParentTest() throws IOException {
        try (
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PdfDocument pdfDocument = new PdfDocument(new PdfWriter(baos))) {
            pdfDocument.addNewPage();

            PdfDictionary first = new PdfDictionary();
            first.makeIndirect(pdfDocument);

            PdfDictionary outlineDictionary = new PdfDictionary();
            outlineDictionary.put(PdfName.First, first);

            Exception exception = Assert.assertThrows(
                    PdfException.class,
                    () -> pdfDocument.getCatalog().constructOutlines(outlineDictionary, new HashMap<String, PdfObject>())
            );
            Assert.assertEquals(
                    MessageFormatUtil.format(PdfException.CORRUPTED_OUTLINE_NO_PARENT_ENTRY,
                            first.indirectReference),
                    exception.getMessage());
        }
    }

    @Test
    public void constructOutlinesNoTitleTest() throws IOException {
        try (
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PdfDocument pdfDocument = new PdfDocument(new PdfWriter(baos))) {
            pdfDocument.addNewPage();

            PdfDictionary first = new PdfDictionary();
            first.makeIndirect(pdfDocument);

            PdfDictionary outlineDictionary = new PdfDictionary();
            outlineDictionary.makeIndirect(pdfDocument);

            outlineDictionary.put(PdfName.First, first);
            first.put(PdfName.Parent, outlineDictionary);

            Exception exception = Assert.assertThrows(
                    PdfException.class,
                    () -> pdfDocument.getCatalog()
                            .constructOutlines(outlineDictionary, new HashMap<String, PdfObject>())
            );
            Assert.assertEquals(
                    MessageFormatUtil.format(PdfException.CORRUPTED_OUTLINE_NO_TITLE_ENTRY,
                            first.indirectReference),
                    exception.getMessage());
        }
    }
}
