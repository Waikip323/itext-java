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
package com.itextpdf.pdfa;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfButtonFormField;
import com.itextpdf.forms.fields.PdfChoiceFormField;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.ParagraphRenderer;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;
import com.itextpdf.test.pdfa.VeraPdfValidator;

import java.io.IOException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

@Category(IntegrationTest.class)
public class PdfAFormFieldTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/";
    public static final String destinationFolder = "./target/test/com/itextpdf/pdfa/PdfAFormFieldTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void pdfAButtonFieldTest() throws Exception {
        PdfDocument pdf;
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        String file = "pdfAButtonField.pdf";
        String filename = destinationFolder + file;
        pdf = new PdfADocument(
                new PdfWriter(new FileOutputStream(filename)),
                PdfAConformanceLevel.PDF_A_1B,
                new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB ICC preference", is));

        PageSize pageSize = PageSize.LETTER;
        Document doc = new Document(pdf, pageSize);
        PdfFontFactory.register(sourceFolder + "FreeSans.ttf", sourceFolder + "FreeSans.ttf");
        PdfFont font = PdfFontFactory.createFont(
                sourceFolder + "FreeSans.ttf", EmbeddingStrategy.PREFER_EMBEDDED);

        PdfButtonFormField group = PdfFormField.createRadioGroup(pdf, "group", "", PdfAConformanceLevel.PDF_A_1B);
        group.setReadOnly(true);

        Paragraph p = new Paragraph();
        Text t = new Text("supported");

        t.setFont(font);

        p.add(t);

        Image ph = new Image(new PdfFormXObject(new Rectangle(10, 10)));
        Paragraph pc = new Paragraph().add(ph);
        PdfAButtonFieldTestRenderer r = new PdfAButtonFieldTestRenderer(pc, group, "v1");

        pc.setNextRenderer(r);

        p.add(pc);

        Paragraph pc1 = new Paragraph().add(ph);
        PdfAButtonFieldTestRenderer r1 = new PdfAButtonFieldTestRenderer(pc, group, "v2");
        pc1.setNextRenderer(r1);

        Paragraph p2 = new Paragraph();
        Text t2 = new Text("supported 2");
        t2.setFont(font);

        p2.add(t2).add(pc1);

        doc.add(p);
        doc.add(p2);
        //set generateAppearance param to false to retain custom appearance
        group.setValue("v1", false);
        PdfAcroForm.getAcroForm(pdf, true).addField(group);

        pdf.close();
        Assert.assertNull(
                new CompareTool().compareByContent(filename, sourceFolder + "cmp/PdfAFormFieldTest/cmp_" + file, destinationFolder, "diff_"));
    }

    static class PdfAButtonFieldTestRenderer extends ParagraphRenderer {
        private PdfButtonFormField _group;
        private String _value;

        public PdfAButtonFieldTestRenderer(Paragraph para, PdfButtonFormField group, String value) {
            super(para);
            _group = group;
            _value = value;
        }

        @Override
        public void draw(DrawContext context) {
            int pageNumber = getOccupiedArea().getPageNumber();
            Rectangle bbox = getInnerAreaBBox();
            PdfDocument pdf = context.getDocument();
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdf, true);
            PdfFormField chk = PdfFormField.createRadioButton(pdf, bbox, _group, _value, PdfAConformanceLevel.PDF_A_1B);
            chk.setPage(pageNumber);

            chk.setVisibility(PdfFormField.VISIBLE);
            chk.setBorderColor(ColorConstants.BLACK);
            chk.setBackgroundColor(ColorConstants.WHITE);
            chk.setReadOnly(true);

            PdfFormXObject appearance = new PdfFormXObject(bbox);
            PdfCanvas canvas = new PdfCanvas(appearance, pdf);

            canvas.saveState()
                    .moveTo(bbox.getLeft(), bbox.getBottom())
                    .lineTo(bbox.getRight(), bbox.getBottom())
                    .lineTo(bbox.getRight(), bbox.getTop())
                    .lineTo(bbox.getLeft(), bbox.getTop())
                    .lineTo(bbox.getLeft(), bbox.getBottom())
                    .setLineWidth(1f)
                    .stroke()
                    .restoreState();

            form.addFieldAppearanceToPage(chk, pdf.getPage(pageNumber));
            //appearance stream was set, while AS has kept as is, i.e. in Off state.
            chk.setAppearance(PdfName.N, "v1".equals(_value) ? _value : "Off", appearance.getPdfObject());
        }

        @Override
        public IRenderer getNextRenderer() {
            return new PdfAButtonFieldTestRenderer((Paragraph) modelElement, _group, _value);
        }
    }

    @Test
    public void pdfA1DocWithPdfA1ButtonFieldTest() throws IOException, InterruptedException {
        String name = "pdfA1DocWithPdfA1ButtonField";
        String fileName = destinationFolder + name + ".pdf";
        String cmp = sourceFolder + "cmp/PdfAFormFieldTest/cmp_pdfA1DocWithPdfA1ButtonField.pdf";

        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");

        PdfAConformanceLevel conformanceLevel = PdfAConformanceLevel.PDF_A_1B;

        PdfADocument pdfDoc = new PdfADocument(new PdfWriter(fileName), conformanceLevel,
                new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));

        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
        PdfFormField emptyField = PdfFormField.createEmptyField(pdfDoc, conformanceLevel).setFieldName("empty");
        emptyField.addKid(PdfFormField
                .createButton(pdfDoc, new Rectangle(36, 756, 20, 20), PdfAnnotation.PRINT, conformanceLevel)
                .setFieldName("button").setValue("hello"));
        form.addField(emptyField);

        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(fileName, cmp, destinationFolder));
        Assert.assertNull(new VeraPdfValidator().validate(fileName));
    }

    @Test
    public void pdfA1DocWithPdfA1CheckBoxFieldTest() throws IOException, InterruptedException {
        String name = "pdfA1DocWithPdfA1CheckBoxField";
        String fileName = destinationFolder + name + ".pdf";
        String cmp = sourceFolder + "cmp/PdfAFormFieldTest/cmp_pdfA1DocWithPdfA1CheckBoxField.pdf";

        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");

        PdfAConformanceLevel conformanceLevel = PdfAConformanceLevel.PDF_A_1B;

        PdfADocument pdfDoc = new PdfADocument(new PdfWriter(fileName), conformanceLevel,
                new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));

        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
        form.addField(PdfFormField
                .createCheckBox(pdfDoc, new Rectangle(36, 726, 20, 20), "checkBox", "1", PdfFormField.TYPE_STAR,
                        conformanceLevel));
        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(fileName, cmp, destinationFolder));
        Assert.assertNull(new VeraPdfValidator().validate(fileName));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.FIELD_VALUE_IS_NOT_CONTAINED_IN_OPT_ARRAY)})
    public void pdfA1DocWithPdfA1ChoiceFieldTest() throws IOException, InterruptedException {
        String name = "pdfA1DocWithPdfA1ChoiceField";
        String fileName = destinationFolder + name + ".pdf";
        String cmp = sourceFolder + "cmp/PdfAFormFieldTest/cmp_pdfA1DocWithPdfA1ChoiceField.pdf";

        PdfFont fontFreeSans = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf",
                "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");

        PdfAConformanceLevel conformanceLevel = PdfAConformanceLevel.PDF_A_1B;
        PdfADocument pdfDoc = new PdfADocument(new PdfWriter(fileName), conformanceLevel,
                new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
        PdfArray options = new PdfArray();
        options.add(new PdfString("Name"));
        options.add(new PdfString("Surname"));
        form.addField(PdfFormField
                .createChoice(pdfDoc, new Rectangle(36, 696, 100, 70), "choice", "1", options, 0, fontFreeSans,
                        conformanceLevel));

        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(fileName, cmp, destinationFolder));
        Assert.assertNull(new VeraPdfValidator().validate(fileName));
    }

    @Test
    public void pdfA1DocWithPdfA1ComboBoxFieldTest() throws IOException, InterruptedException {
        String name = "pdfA1DocWithPdfA1ComboBoxField";
        String fileName = destinationFolder + name + ".pdf";
        String cmp = sourceFolder + "cmp/PdfAFormFieldTest/cmp_pdfA1DocWithPdfA1ComboBoxField.pdf";

        PdfFont fontCJK = PdfFontFactory.createFont(sourceFolder + "NotoSansCJKtc-Light.otf",
                        PdfEncodings.IDENTITY_H, EmbeddingStrategy.FORCE_EMBEDDED);

        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");

        PdfAConformanceLevel conformanceLevel = PdfAConformanceLevel.PDF_A_1B;
        PdfADocument pdfDoc = new PdfADocument(new PdfWriter(fileName), conformanceLevel,
                new PdfOutputIntent("Custom", "",
                        "http://www.color.org", "sRGB IEC61966-2.1", is));

        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
        form.addField(PdfFormField.createComboBox(pdfDoc, new Rectangle(156, 616, 70, 70),
                "combo", "用", new String[] {"用", "规", "表"}, fontCJK, conformanceLevel));

        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(fileName, cmp, destinationFolder));
        Assert.assertNull(new VeraPdfValidator().validate(fileName));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.MULTIPLE_VALUES_ON_A_NON_MULTISELECT_FIELD)})
    public void pdfA1DocWithPdfA1ListFieldTest() throws IOException, InterruptedException {
        String name = "pdfA1DocWithPdfA1ListField";
        String fileName = destinationFolder + name + ".pdf";
        String cmp = sourceFolder + "cmp/PdfAFormFieldTest/cmp_pdfA1DocWithPdfA1ListField.pdf";

        PdfFont fontFreeSans = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf",
                "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);

        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");

        PdfAConformanceLevel conformanceLevel = PdfAConformanceLevel.PDF_A_1B;
        PdfADocument pdfDoc = new PdfADocument(new PdfWriter(fileName), conformanceLevel,
                new PdfOutputIntent("Custom", "",
                        "http://www.color.org", "sRGB IEC61966-2.1", is));

        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);

        PdfChoiceFormField f = PdfFormField.createList(pdfDoc, new Rectangle(86, 556, 50, 200),
                "list", "9", new String[] {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"},
                fontFreeSans, conformanceLevel);
        f.setValue("4");
        f.setTopIndex(2);
        f.setListSelected(new String[] {"3", "5"});
        form.addField(f);

        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(fileName, cmp, destinationFolder));
        Assert.assertNull(new VeraPdfValidator().validate(fileName));
    }

    @Test
    public void pdfA1DocWithPdfA1PushButtonFieldTest() throws IOException, InterruptedException {
        String name = "pdfA1DocWithPdfA1PushButtonField";
        String fileName = destinationFolder + name + ".pdf";
        String cmp = sourceFolder + "cmp/PdfAFormFieldTest/cmp_pdfA1DocWithPdfA1PushButtonField.pdf";

        PdfFont fontFreeSans = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf",
                "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);

        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");

        PdfAConformanceLevel conformanceLevel = PdfAConformanceLevel.PDF_A_1B;
        PdfADocument pdfDoc = new PdfADocument(new PdfWriter(fileName), conformanceLevel,
                new PdfOutputIntent("Custom", "",
                        "http://www.color.org", "sRGB IEC61966-2.1", is));

        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
        form.addField(PdfFormField.createPushButton(pdfDoc, new Rectangle(36, 526, 100, 20),
                "push button", "Push", fontFreeSans, 12, conformanceLevel));

        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(fileName, cmp, destinationFolder));
        Assert.assertNull(new VeraPdfValidator().validate(fileName));
    }

    @Test
    public void pdfA1DocWithPdfA1RadioButtonFieldTest() throws IOException, InterruptedException {
        String name = "pdfA1DocWithPdfA1RadioButtonField";
        String fileName = destinationFolder + name + ".pdf";
        String cmp = sourceFolder + "cmp/PdfAFormFieldTest/cmp_pdfA1DocWithPdfA1RadioButtonField.pdf";

        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");

        PdfAConformanceLevel conformanceLevel = PdfAConformanceLevel.PDF_A_1B;
        PdfADocument pdfDoc = new PdfADocument(new PdfWriter(fileName), conformanceLevel,
                new PdfOutputIntent("Custom", "",
                        "http://www.color.org", "sRGB IEC61966-2.1", is));

        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);

        PdfButtonFormField radioGroup = PdfFormField.createRadioGroup(pdfDoc, "radio group", "", conformanceLevel);
        PdfFormField.createRadioButton(pdfDoc, new Rectangle(36, 496, 20, 20), radioGroup, "1", conformanceLevel)
                .setBorderWidth(2).setBorderColor(ColorConstants.ORANGE);
        PdfFormField.createRadioButton(pdfDoc, new Rectangle(66, 496, 20, 20), radioGroup, "2", conformanceLevel)
                .setBorderWidth(2).setBorderColor(ColorConstants.ORANGE);

        form.addField(radioGroup);

        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(fileName, cmp, destinationFolder));
        Assert.assertNull(new VeraPdfValidator().validate(fileName));
    }

    @Test
    public void pdfA1DocWithPdfA1TextFieldTest() throws IOException, InterruptedException {
        String name = "pdfA1DocWithPdfA1TextField";
        String fileName = destinationFolder + name + ".pdf";
        String cmp = sourceFolder + "cmp/PdfAFormFieldTest/cmp_pdfA1DocWithPdfA1TextField.pdf";

        PdfFont fontFreeSans = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf",
                "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);
        fontFreeSans.setSubset(false);

        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");

        PdfAConformanceLevel conformanceLevel = PdfAConformanceLevel.PDF_A_1B;
        PdfADocument pdfDoc = new PdfADocument(new PdfWriter(fileName), conformanceLevel,
                new PdfOutputIntent("Custom", "",
                        "http://www.color.org", "sRGB IEC61966-2.1", is));

        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
        form.addField(PdfFormField.createText(pdfDoc, new Rectangle(36, 466, 90, 20),
                "text", "textField", fontFreeSans, 12, false, conformanceLevel).setValue("iText"));

        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(fileName, cmp, destinationFolder));
        Assert.assertNull(new VeraPdfValidator().validate(fileName));
    }

    @Test
    public void pdfA1DocWithPdfA1SignatureFieldTest() throws IOException, InterruptedException {
        String name = "pdfA1DocWithPdfA1SignatureField";
        String fileName = destinationFolder + name + ".pdf";
        String cmp = sourceFolder + "cmp/PdfAFormFieldTest/cmp_pdfA1DocWithPdfA1SignatureField.pdf";

        PdfFont fontFreeSans = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf",
                "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);
        fontFreeSans.setSubset(false);

        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");

        PdfAConformanceLevel conformanceLevel = PdfAConformanceLevel.PDF_A_1B;
        PdfADocument pdfDoc = new PdfADocument(new PdfWriter(fileName), conformanceLevel,
                new PdfOutputIntent("Custom", "",
                        "http://www.color.org", "sRGB IEC61966-2.1", is));

        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
        form.addField(PdfFormField.createSignature(pdfDoc, conformanceLevel).setFieldName("signature")
                .setFont(fontFreeSans).setFontSize(20));

        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(fileName, cmp, destinationFolder));
        Assert.assertNull(new VeraPdfValidator().validate(fileName));
    }
}
