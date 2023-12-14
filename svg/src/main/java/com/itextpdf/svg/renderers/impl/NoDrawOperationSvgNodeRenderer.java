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
package com.itextpdf.svg.renderers.impl;

import com.itextpdf.svg.exceptions.SvgLogMessageConstant;
import com.itextpdf.svg.renderers.INoDrawSvgNodeRenderer;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;

/**
 * Tags mapped onto this renderer won't be drawn and will be excluded from the renderer tree when processed.
 * Different from being added to the ignored list as this Renderer will allow its children to be processed.
 *
 * @deprecated will be removed in iText 7.2 use {@link INoDrawSvgNodeRenderer} instead
 */
@Deprecated
public class NoDrawOperationSvgNodeRenderer extends AbstractBranchSvgNodeRenderer {
    @Override
    protected void doDraw(SvgDrawContext context) {
        throw new UnsupportedOperationException(SvgLogMessageConstant.DRAW_NO_DRAW);
    }

    @Override
    public ISvgNodeRenderer createDeepCopy() {
        NoDrawOperationSvgNodeRenderer copy = new NoDrawOperationSvgNodeRenderer();
        deepCopyAttributesAndStyles(copy);
        return copy;
    }
}
