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

import com.itextpdf.kernel.ProductInfo;
import com.itextpdf.test.ExtendedITextTest;

import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class FingerPrintTest extends ExtendedITextTest {

    private ProductInfo productInfo;
    private ProductInfo productInfo2;

    @Before
    public void beforeTest() {
        this.productInfo = new ProductInfo("pdfProduct", 1, 0, 0, true);
        this.productInfo2 = new ProductInfo("pdfProduct2", 1, 0, 0, true);
    }

    @Test
    public void normalAddTest() {
        FingerPrint fingerPrint = new FingerPrint();
        Assert.assertTrue(fingerPrint.registerProduct(productInfo));
        Assert.assertTrue(fingerPrint.registerProduct(productInfo2));
        Assert.assertEquals(2, fingerPrint.getProducts().size());
    }

    @Test
    public void duplicateTest() {
        FingerPrint fingerPrint = new FingerPrint();
        fingerPrint.registerProduct(productInfo);
        Assert.assertFalse(fingerPrint.registerProduct(productInfo));
    }
}

