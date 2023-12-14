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
package com.itextpdf.kernel.pdf.filters;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;

/**
 * A filter that doesn't modify the stream at all
 */
public class DoNothingFilter implements IFilterHandler {
    private PdfName lastFilterName;

    @Override
    public byte[] decode(byte[] b, PdfName filterName, PdfObject decodeParams, PdfDictionary streamDictionary) {
        lastFilterName = filterName;
        return b;
    }

    /**
     * Returns the last decoded filter name.
     *
     * @return the last decoded filter name.
     * @deprecated Will be removed in 7.2. Used as a crutch in
     * {@link PdfImageXObject#getImageBytes()} implementation. Now this method does not needed.
     * If the user has been used it, then the same approach can be reached with nested class.
     */
    @Deprecated
    public PdfName getLastFilterName() {
        return lastFilterName;
    }
}
