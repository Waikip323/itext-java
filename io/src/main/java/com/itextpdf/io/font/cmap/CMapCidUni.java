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
package com.itextpdf.io.font.cmap;

import com.itextpdf.io.util.IntHashtable;
import com.itextpdf.io.util.TextUtil;

/**
 * @author psoares
 */
public class CMapCidUni extends AbstractCMap {

    private static final long serialVersionUID = 6879167385978230141L;
    private IntHashtable map = new IntHashtable(65537);

    @Override
    void addChar(String mark, CMapObject code) {
        if (code.isNumber()) {
            int codePoint;
            String s = toUnicodeString(mark, true);
            if (TextUtil.isSurrogatePair(s, 0)) {
                codePoint = TextUtil.convertToUtf32(s, 0);
            } else {
                codePoint = (int) s.charAt(0);
            }
            map.put((int)code.getValue(), codePoint);
        }
    }

    public int lookup(int character) {
        return map.get(character);
    }

    public int[] getCids(){
        return map.getKeys();
    }
}
