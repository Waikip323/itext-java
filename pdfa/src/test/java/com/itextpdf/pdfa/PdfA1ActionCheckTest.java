/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.pdfa;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.pdfa.exceptions.PdfAConformanceException;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@Category(IntegrationTest.class)
public class PdfA1ActionCheckTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/";

    @Test
    public void actionCheck01() throws FileNotFoundException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.addNewPage();
        PdfDictionary openActions = new PdfDictionary();
        openActions.put(PdfName.S, PdfName.Launch);
        doc.getCatalog().put(PdfName.OpenAction, openActions);

        Exception e = Assert.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assert.assertEquals(MessageFormatUtil.format(PdfAConformanceException._0_ACTIONS_ARE_NOT_ALLOWED, PdfName.Launch.getValue()), e.getMessage());
    }

    @Test
    public void actionCheck02() throws FileNotFoundException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.addNewPage();
        PdfDictionary openActions = new PdfDictionary();
        openActions.put(PdfName.S, PdfName.Hide);
        doc.getCatalog().put(PdfName.OpenAction, openActions);

        Exception e = Assert.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assert.assertEquals(MessageFormatUtil.format(PdfAConformanceException._0_ACTIONS_ARE_NOT_ALLOWED, PdfName.Hide.getValue()),
                e.getMessage());
    }

    @Test
    public void actionCheck03() throws FileNotFoundException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.addNewPage();
        PdfDictionary openActions = new PdfDictionary();
        openActions.put(PdfName.S, PdfName.Sound);
        doc.getCatalog().put(PdfName.OpenAction, openActions);

        Exception e = Assert.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assert.assertEquals(MessageFormatUtil.format(PdfAConformanceException._0_ACTIONS_ARE_NOT_ALLOWED, PdfName.Sound.getValue()), e.getMessage());
    }

    @Test
    public void actionCheck04() throws FileNotFoundException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.addNewPage();
        PdfDictionary openActions = new PdfDictionary();
        openActions.put(PdfName.S, PdfName.Movie);
        doc.getCatalog().put(PdfName.OpenAction, openActions);

        Exception e = Assert.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assert.assertEquals(MessageFormatUtil.format(PdfAConformanceException._0_ACTIONS_ARE_NOT_ALLOWED, PdfName.Movie.getValue()),
                e.getMessage());
    }

    @Test
    public void actionCheck05() throws FileNotFoundException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.addNewPage();
        PdfDictionary openActions = new PdfDictionary();
        openActions.put(PdfName.S, PdfName.ResetForm);
        doc.getCatalog().put(PdfName.OpenAction, openActions);

        Exception e = Assert.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assert.assertEquals(MessageFormatUtil.format(PdfAConformanceException._0_ACTIONS_ARE_NOT_ALLOWED, PdfName.ResetForm.getValue()),
                e.getMessage());
    }

    @Test
    public void actionCheck06() throws FileNotFoundException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.addNewPage();
        PdfDictionary openActions = new PdfDictionary();
        openActions.put(PdfName.S, PdfName.ImportData);
        doc.getCatalog().put(PdfName.OpenAction, openActions);

        Exception e = Assert.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assert.assertEquals(MessageFormatUtil.format(PdfAConformanceException._0_ACTIONS_ARE_NOT_ALLOWED, PdfName.ImportData.getValue()),
                e.getMessage());
    }

    @Test
    public void actionCheck07() throws FileNotFoundException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.addNewPage();
        PdfDictionary openActions = new PdfDictionary();
        openActions.put(PdfName.S, PdfName.JavaScript);
        doc.getCatalog().put(PdfName.OpenAction, openActions);

        Exception e = Assert.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assert.assertEquals(MessageFormatUtil.format(PdfAConformanceException._0_ACTIONS_ARE_NOT_ALLOWED, PdfName.JavaScript.getValue()),
                e.getMessage());
    }

    @Test
    public void actionCheck08() throws FileNotFoundException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.addNewPage();
        PdfDictionary openActions = new PdfDictionary();
        openActions.put(PdfName.S, PdfName.Named);
        openActions.put(PdfName.N, new PdfName("CustomName"));
        doc.getCatalog().put(PdfName.OpenAction, openActions);

        Exception e = Assert.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assert.assertEquals(MessageFormatUtil.format(PdfAConformanceException.NAMED_ACTION_TYPE_0_IS_NOT_ALLOWED, "CustomName"),
                e.getMessage());
    }

    @Test
    public void actionCheck09() throws FileNotFoundException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        PdfPage page = doc.addNewPage();
        page.setAdditionalAction(PdfName.C, PdfAction.createJavaScript("js"));

        Exception e = Assert.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assert.assertEquals(MessageFormatUtil.format(PdfAConformanceException._0_ACTIONS_ARE_NOT_ALLOWED, PdfName.JavaScript.getValue()),
                e.getMessage());
    }

    @Test
    public void actionCheck10() throws FileNotFoundException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        PdfPage page = doc.addNewPage();
        PdfDictionary action = new PdfDictionary();
        action.put(PdfName.S, PdfName.SetState);
        page.setAdditionalAction(PdfName.C, new PdfAction(action));

        Exception e = Assert.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assert.assertEquals(PdfAConformanceException.DEPRECATED_SETSTATE_AND_NOOP_ACTIONS_ARE_NOT_ALLOWED, e.getMessage());
    }

    @Test
    public void actionCheck11() throws FileNotFoundException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.addNewPage();
        doc.getCatalog().setAdditionalAction(PdfName.C, PdfAction.createJavaScript("js"));

        Exception e = Assert.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assert.assertEquals(PdfAConformanceException.A_CATALOG_DICTIONARY_SHALL_NOT_CONTAIN_AA_ENTRY, e.getMessage());
    }
}
