/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
    Authors: Apryse Software.

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
package com.itextpdf.svg.jfreesvg;

import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;


@Category(IntegrationTest.class)
public class JFreeSvgTest extends SvgIntegrationTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/JFreeSvgTest/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/svg/JFreeSvgTest/";

    @BeforeClass
    public static void beforeClass() {
        ITextTest.createDestinationFolder(DESTINATION_FOLDER);
    }

    //Do not make changes in svg file because it was generated by JFreeSVG
    @Test
    public void usingJFreeSvgFromStringTest() throws IOException, InterruptedException {
        //TODO: update cmp-file when DEVSIX-2246 will be fixed
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "rectangleFromStringTest");
    }

    //Do not make changes in svg file because it was generated by JFreeSVG
    @Test
    public void usingJFreeSvgFromFileTest() throws IOException, InterruptedException {
        //TODO: update cmp-file when DEVSIX-2246 will be fixed
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "rectangleFromFileTest");
    }

    //Do not make changes in svg file because it was generated by JFreeSVG
    @Test
    public void pieChartByJFreeSvgTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "usingJFreeSvgPieChartFromFileTest");
    }

    //Do not make changes in svg file because it was generated by JFreeSVG
    @Test
    //TODO: update cmp file when DEVSIX-4043 will be fixed
    public void barChartByJFreeSvgFromStringTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "usingJFreeSvgBarChartFromStringTest");
    }

    //Do not make changes in svg file because it was generated by JFreeSVG
    @Test
    //TODO: update cmp file when DEVSIX-4043 will be fixed
    public void barChartByJFreeSvgFromFileTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "usingJFreeSvgBarChartFromFileTest");
    }

    //Do not make changes in svg file because it was generated by JFreeSVG
    @Test
    public void lineChartByJFreeSvgFromStringTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "usingJFreeSvgLineChartFromStringTest");
    }

    //Do not make changes in svg file because it was generated by JFreeSVG
    @Test
    public void lineChartByJFreeSvgFromFileTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "usingJFreeSvgLineChartFromFileTest");
    }

    //Do not make changes in svg file because it was generated by JFreeSVG
    @Test
    public void xyChartByJFreeSvgFromStringTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "usingJFreeSvgXYChartFromStringTest");
    }

    //Do not make changes in svg file because it was generated by JFreeSVG

    @Test
    public void xyChartByJFreeSvgFromFileTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "usingJFreeSvgXYChartFromFileTest");
    }
}
